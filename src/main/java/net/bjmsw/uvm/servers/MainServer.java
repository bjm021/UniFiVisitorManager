package net.bjmsw.uvm.servers;

import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinFreemarker;
import net.bjmsw.uvm.VisitorManager;

import java.util.HashMap;

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

                ctx.render("templates/main.ftl", model);
            });


        }).start(8080);
    }

    public static synchronized void start() {
        if (instance == null) {
            instance = new MainServer();
        }
    }

}
