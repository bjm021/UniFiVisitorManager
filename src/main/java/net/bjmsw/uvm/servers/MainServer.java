package net.bjmsw.uvm.servers;

import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinFreemarker;
import net.bjmsw.uvm.VisitorManager;
import net.bjmsw.uvm.model.PrivilegedVisitor;

import java.util.HashMap;
import java.util.UUID;

public class MainServer {

    private static MainServer instance;
    private final Javalin app;

    @SuppressWarnings("ConstantConditions,CallToPrintStackTrace")
    private MainServer() {
        app = Javalin.create(config -> {
            config.staticFiles.add("/public");

            config.fileRenderer(new JavalinFreemarker());

            config.routes.exception(Exception.class, (e, ctx) -> {
                e.printStackTrace();
                ctx.status(500).result("The actual error is: " + e.getMessage());
            });

            config.routes.get("/", ctx -> {
                HashMap<String, Object> model = new HashMap<>();
                model.put("visitors", VisitorManager.getApiClient().getVisitors());
                model.put("privilegedVisitors", VisitorManager.getPrivilegedVisitors());

                String success = ctx.cookie("pv_success");
                if (success != null) {
                    model.put("pv_success", success);
                    ctx.removeCookie("pv_success");
                }

                ctx.render("templates/main.ftl", model);
            });

            config.routes.post("/create-pv", ctx -> {

                String first_name = ctx.formParam("first_name");
                String last_name = ctx.formParam("last_name");
                String email = ctx.formParam("email");

                String id = UUID.randomUUID().toString();

                VisitorManager.getPrivilegedVisitors().put(id, new PrivilegedVisitor(first_name, last_name, email, id));
                VisitorManager.getDb().commit();

                ctx.cookie("pv_success", "true");

                ctx.redirect("/");
            });


        }).start(8080);
    }

    public static synchronized void start() {
        if (instance == null) {
            instance = new MainServer();
        }
    }

}
