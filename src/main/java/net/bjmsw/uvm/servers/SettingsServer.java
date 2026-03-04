package net.bjmsw.uvm.servers;

import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinFreemarker;
import net.bjmsw.uvm.VisitorManager;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class SettingsServer {

    private static SettingsServer instance;
    private final Javalin app;

    private SettingsServer(String configFailed) {
        if (configFailed != null) {
            System.out.println("[UniFi SettingsServer] Configuration failed: " + configFailed);
        } else {
            System.out.println("[UniFi SettingsServer] No configuration issues detected. Starting normally...");
        }

        this.app = Javalin.create(config -> {
            config.fileRenderer(new JavalinFreemarker());

            config.staticFiles.add("/public");

            config.routes.exception(Exception.class, (e, ctx) -> {
                System.err.println("[SettingsServer] Unhandled exception: " + e.getMessage());
                e.printStackTrace();
                ctx.status(500).result("The actual error is: " + e.getMessage());
                VisitorManager.shutdown();
            });

            config.routes.get("/", ctx -> {
                System.out.println("[UniFi SettingsServer] Rendering settings page...");
                Map<String, Object> model = new HashMap<>();

                String success = ctx.cookie("successMessage");
                if (success != null) {
                    model.put("successMessage", URLDecoder.decode(success, StandardCharsets.UTF_8));
                    ctx.removeCookie("successMessage");
                }

                String error = ctx.cookie("errorMessage");
                if (error != null) {
                    model.put("errorMessage", URLDecoder.decode(error, StandardCharsets.UTF_8));
                    ctx.removeCookie("errorMessage");
                }

                if (configFailed != null) {
                    model.put("configFailed", configFailed);
                }

                ctx.render("templates/settings.ftl", model);
            });

            config.routes.post("/update-settings", ctx -> {
                try {
                    // --- 1. UniFi Settings ---
                    String hostname = ctx.formParam("hostname");
                    String token = ctx.formParam("token");

                    if (hostname != null) {
                        if (hostname.startsWith("http://")) hostname = hostname.replace("http://", "https://");
                        if (!hostname.startsWith("https://")) hostname = "https://" + hostname;
                        if (hostname.endsWith("/")) hostname = hostname.substring(0, hostname.length() - 1);
                        VisitorManager.getSettings().put("hostname", hostname);
                    }
                    if (token != null && !token.isBlank()) {
                        VisitorManager.getSettings().put("token", token);
                    }

                    // --- 2. Apple Wallet Settings (jpasskit) ---
                    VisitorManager.getSettings().put("appleTeamId", ctx.formParam("appleTeamId"));
                    VisitorManager.getSettings().put("applePassTypeId", ctx.formParam("applePassTypeId"));
                    VisitorManager.getSettings().put("appleOrgName", ctx.formParam("appleOrgName"));
                    VisitorManager.getSettings().put("appleP12Path", ctx.formParam("appleP12Path")); // Path to cert on server
                    VisitorManager.getSettings().put("appleP12Password", ctx.formParam("appleP12Password"));
                    VisitorManager.getSettings().put("appleWwdrPath", ctx.formParam("appleWwdrPath"));

                    // --- 3. SMTP Email Settings ---
                    VisitorManager.getSettings().put("smtpHost", ctx.formParam("smtpHost"));
                    VisitorManager.getSettings().put("smtpPort", ctx.formParam("smtpPort"));
                    VisitorManager.getSettings().put("smtpUser", ctx.formParam("smtpUser"));
                    VisitorManager.getSettings().put("smtpPass", ctx.formParam("smtpPass"));
                    VisitorManager.getSettings().put("smtpFromName", ctx.formParam("smtpFromName"));

                    // Save to MapDB
                    VisitorManager.getDb().commit();

                    ctx.cookie("successMessage", URLEncoder.encode("Settings updated successfully!", StandardCharsets.UTF_8));
                    ctx.redirect("/");

                    VisitorManager.dispatchRestart(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                    ctx.cookie("errorMessage", URLEncoder.encode("Failed to update settings: " + e.getMessage(), StandardCharsets.UTF_8));
                    ctx.redirect("/");
                }
            });

        }).start(8080);
    }

    public static synchronized void start() {
        if (instance == null) {
            instance = new SettingsServer(null);
        }
    }

    public static synchronized void start(String configFailed) {
        if (instance == null) {
            instance = new SettingsServer(configFailed);
        }
    }



    public static boolean isRunning() {
        return instance != null;
    }
}