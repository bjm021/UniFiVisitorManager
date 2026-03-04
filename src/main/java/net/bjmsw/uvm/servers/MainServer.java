package net.bjmsw.uvm.servers;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.rendering.template.JavalinFreemarker;
import net.bjmsw.uvm.VisitorManager;
import net.bjmsw.uvm.model.AccessResource;
import net.bjmsw.uvm.model.Event;
import net.bjmsw.uvm.model.PrivilegedVisitor;
import net.bjmsw.uvm.model.Visitor;
import net.bjmsw.uvm.util.TimeUtils;

import java.util.*;

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
                model.put("events", VisitorManager.getEvents());

                checkAndAddToModel(ctx, model, "pv_success");
                checkAndAddToModel(ctx, model, "otv_success");
                checkAndAddToModel(ctx, model, "otv_error");
                checkAndAddToModel(ctx, model, "delete_otv_success");
                checkAndAddToModel(ctx, model, "delete_otv_error");
                checkAndAddToModel(ctx, model, "pe_success");

                ctx.render("templates/main.ftl", model);
            });

            config.routes.get("/emailtest", ctx -> {
                Visitor v = VisitorManager.getApiClient().getVisitors().get(0);
                Map<String, Object> model = new HashMap<>();
                model.put("visitorName", v.getFirstName());
                model.put("eventName", "Onetime Visitor");
                if (VisitorManager.getSettings().get("appleOrgName") != null) {
                    model.put("companyName", VisitorManager.getSettings().get("appleOrgName"));
                } else {
                    model.put("companyName", "Powered by UniFi Visitor Manager");
                }
                model.put("currentYear", TimeUtils.getCurrentYear());
                model.put("visitStartTime", TimeUtils.fromEpochSecondsToDateTimeString(v.getStart_time(), "E dd MMM yyyy HH:mm"));
                model.put("visitEndTime", TimeUtils.fromEpochSecondsToDateTimeString(v.getEnd_time(), "E dd MMM yyyy HH:mm"));
                model.put("customMessage", "This is a custom message from UniFi Visitor Manager.");


                ctx.render("templates/email/visitor_invite.ftl", model);
            });

            config.routes.post("/create-pv", this::handleNewPrivilegedVisitor);
            config.routes.post("/create-otv", this::handleNewOnetimeVisitor);
            config.routes.post("/delete-otv", this::handleDeleteOTV);
            config.routes.post("/plan_event", this::handleNewEvent);

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

            long startTime = TimeUtils.formStringToEpochSeconds(ctx.formParam("access_start"));
            long endTime = TimeUtils.formStringToEpochSeconds(ctx.formParam("access_end"));

            String resourceId = ctx.formParam("resource_id");

            AccessResource ars = AccessResource.findResourceById(VisitorManager.getApiClient().getAccessResources(), resourceId);
            if (ars == null) {
                System.err.println("[UniFi API Error] Access Resource not found: " + resourceId);
                ctx.cookie("otv_error", "true");
                ctx.redirect("/");
                return;
            }

            Visitor v = VisitorManager.getApiClient().createVisitor(first_name, last_name, email, startTime, endTime, "UVM_ONETIME_" + TimeUtils.buildInternalDateTimeString(System.currentTimeMillis() / 1000), ars.getId(), ars.getType());

            String customMessage = ctx.formParam("custom_message");

            VisitorManager.getMailScheduler().scheduleMail(v, "Onetime Visitor Access", customMessage);

            ctx.cookie("otv_success", "true");
            ctx.redirect("/#otv-add-form");
        } catch (Exception e) {
            System.err.println("[UniFi API Error] Failed to create Onetime Visitor: " + e.getMessage());
            e.printStackTrace();
            ctx.cookie("otv_error", "true");
            ctx.redirect("/#otv-add-form");
        }
    }

    private void handleNewEvent(Context ctx) {

        // get all data needed for the event
        try {
            AccessResource ar = AccessResource.findResourceById(VisitorManager.getApiClient().getAccessResources(), ctx.formParam("resource_id"));
            String description = ctx.formParam("description");
            String name = ctx.formParam("name");
            long startTime = TimeUtils.formStringToEpochSeconds(ctx.formParam("start_time"));
            long endTime = TimeUtils.formStringToEpochSeconds(ctx.formParam("end_time"));
            List<String> pv_ids = ctx.formParams("pv_ids");
            List<PrivilegedVisitor> pv_list = VisitorManager.getPrivilegedVisitors().values().stream().filter(pv -> pv_ids.contains(pv.getId())).toList();

            Event e = new Event(UUID.randomUUID().toString(), name, description, startTime, endTime, ar, pv_list);
            VisitorManager.getEvents().put(e.getId(), e);
            e.publishAsync(VisitorManager.getApiClient());
            VisitorManager.getDb().commit();

            ctx.cookie("pe_success", "true");
            ctx.redirect("/#plan-event");
        } catch (Exception e) {
            System.err.println("[UniFi API Error] Failed to create Onetime Visitor: " + e.getMessage());
            e.printStackTrace();
            ctx.cookie("pe_error", "true");
            ctx.redirect("/#plan-event");
        }
    }

    private void handleDeleteOTV(Context ctx) {
        String id = ctx.formParam("id");

        boolean success = VisitorManager.getApiClient().deleteVisitor(id);

        if (success) {
            ctx.cookie("delete_otv_success", "true");
        } else {
            ctx.cookie("delete_otv_error", "true");
        }

        String redirect = ctx.formParam("redirect");

        ctx.redirect("/#" + redirect);
    }
}
