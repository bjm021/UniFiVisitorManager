package net.bjmsw.uvm.servers;

import de.brendamour.jpasskit.PKPass;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.rendering.template.JavalinFreemarker;
import net.bjmsw.uvm.VisitorManager;
import net.bjmsw.uvm.model.AccessResource;
import net.bjmsw.uvm.model.PrivilegedVisitor;
import net.bjmsw.uvm.model.Visitor;
import net.bjmsw.uvm.util.MailUtil;
import net.bjmsw.uvm.util.PKPassUtil;
import net.bjmsw.uvm.util.QRCodeUtils;
import net.bjmsw.uvm.util.TimeUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainServer {

    private static MainServer instance;
    private final Javalin app;

    private MainServer() {
        app = Javalin.create(config -> {
            config.staticFiles.add("/public");

            config.fileRenderer(new JavalinFreemarker());

            config.routes.exception(Exception.class, (e, ctx) -> {
                System.err.println("[MainServer] Unhandled exception: " + e.getMessage());
                e.printStackTrace();
                ctx.status(500).result("The actual error is: " + e.getMessage());
            });

            config.routes.get("/", ctx -> {
                HashMap<String, Object> model = new HashMap<>();
                model.put("visitors", VisitorManager.getApiClient().getVisitors());
                model.put("privilegedVisitors", VisitorManager.getPrivilegedVisitors());
                model.put("accessResources", VisitorManager.getApiClient().getAccessResources());

                checkAndAddToModel(ctx, model, "pv_success");
                checkAndAddToModel(ctx, model, "otv_success");
                checkAndAddToModel(ctx, model, "otv_error");

                ctx.render("templates/main.ftl", model);
            });

            config.routes.post("/create-pv", this::handleNewPrivilegedVisitor);
            config.routes.post("/create-otv", this::handleNewOnetimeVisitor);

            /*
            // DEBUG: Test Wallet Route - Do not commit enabled
            config.routes.get("/testWallet", ctx -> {
                if (VisitorManager.getApiClient().getVisitors().isEmpty()) {
                    ctx.result("No visitors found");
                    return;
                }
                Visitor v = VisitorManager.getApiClient().getVisitors().get(0);
                String data = QRCodeUtils.downloadAndDecodeUniFiQR(VisitorManager.getApiClient(), v);

                PKPass p = PKPassUtil.buildWalletPass(v, data,"TestEvent");
                byte[] pkdata = PKPassUtil.signAndZipPass(p);

                ctx.contentType("application/vnd.apple.pkpass");
                ctx.header("Content-Disposition", "attachment; filename=\"AccessPass.pkpass\"");
                ctx.result(pkdata);
            });
            */

        }).start(8080);
    }

    private void checkAndAddToModel(Context ctx, HashMap<String, Object> model, String cookieName) {
        String success = ctx.cookie(cookieName);
        if (success != null) {
            model.put(cookieName, true);
            ctx.removeCookie(cookieName);
        }
    }

    public static synchronized void start() {
        if (instance == null) {
            instance = new MainServer();
        }
    }

    private void handleNewPrivilegedVisitor(Context ctx) {
        String first_name = ctx.formParam("first_name");
        String last_name = ctx.formParam("last_name");
        String email = ctx.formParam("email");

        String id = UUID.randomUUID().toString();

        VisitorManager.getPrivilegedVisitors().put(id, new PrivilegedVisitor(first_name, last_name, email, id));
        VisitorManager.getDb().commit();

        ctx.cookie("pv_success", "true");

        ctx.redirect("/");
    }

    private void handleNewOnetimeVisitor(Context ctx) {
        try {
            String first_name = ctx.formParam("first_name");
            String last_name = ctx.formParam("last_name");
            String email = ctx.formParam("email");

            long startTime = TimeUtils.formToEpochSeconds(ctx.formParam("access_start"));
            long endTime = TimeUtils.formToEpochSeconds(ctx.formParam("access_end"));

            String resourceId = ctx.formParam("resource_id");

            // String customMessage = ctx.formParam("custom_message"); // Unused

            AccessResource ars = AccessResource.findResourceById(VisitorManager.getApiClient().getAccessResources(), resourceId);
            if (ars == null) {
                System.err.println("[UniFi API Error] Access Resource not found: " + resourceId);
                ctx.cookie("otv_error", "true");
                ctx.redirect("/");
                return;
            }

            // build remark String for Unifi Admins UVM_ONETIME_DDMMYY_HHMMSS (for easy identification and cleanup)

            Visitor v = VisitorManager.getApiClient().createVisitor(first_name, last_name, email, startTime, endTime, "UVM_ONETIME_" + TimeUtils.buildInternalDateTimeString(System.currentTimeMillis() / 1000), ars.getId(), ars.getType());
            v.assignQR(VisitorManager.getApiClient());

            sendInviteMail(v, ctx);

            ctx.cookie("otv_success", "true");
            ctx.redirect("/");
        } catch (Exception e) {
            System.err.println("[UniFi API Error] Failed to create Onetime Visitor: " + e.getMessage());
            e.printStackTrace();
            ctx.cookie("otv_error", "true");
            ctx.redirect("/");
        }
    }

    private boolean sendInviteMail(Visitor v, Context ctx) {
        try {
            JavalinFreemarker freemarker = new JavalinFreemarker();
            Map<String, Object> model = new HashMap<>();
            model.put("visitorName", v.getFirstName());
            model.put("eventName", "Onetime Visitor");

            String htmlBody = freemarker.render("templates/email/visitor_invite.ftl", model, ctx);

            String qrData = QRCodeUtils.downloadAndDecodeUniFiQR(VisitorManager.getApiClient(), v);
            System.out.println("[UniFi QR] QR Code Data: " + qrData);

            File qrImage = new File("qr-data", v.getId() + ".png");

            PKPass pk = PKPassUtil.buildWalletPass(v, qrData, "Onetime Visitor Access");
            byte[] pkdata = PKPassUtil.signAndZipPass(pk);

            MailUtil.sendVisitorInvite(
                    v.getEmail(),
                    v.getFirstName(),
                    v.getLastName(),
                    "Onetime Visitor Access",
                    pkdata,
                    qrImage,
                    htmlBody
            );
            return true;
        } catch (Exception e) {
            System.err.println("[UniFi Error] Failed to send invite email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}
