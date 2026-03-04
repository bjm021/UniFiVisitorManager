package net.bjmsw.uvm;

import net.bjmsw.uvm.model.Event;
import net.bjmsw.uvm.model.PrivilegedVisitor;
import net.bjmsw.uvm.servers.MainServer;
import net.bjmsw.uvm.servers.SettingsServer;
import net.bjmsw.uvm.util.ApiClient;
import net.bjmsw.uvm.util.GarbageCollector;
import net.bjmsw.uvm.util.MailScheduler;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

public class VisitorManager {

    private static DB db;
    private static HTreeMap<String, PrivilegedVisitor> privilegedVisitors;
    private static HTreeMap<String, Event> events;
    private static HTreeMap<String, String> settings;
    private static ApiClient apiClient;
    private static MailScheduler mailScheduler;
    private static GarbageCollector gc;

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {

        // prepare Data Storage
        try  {
            // do something with the database
            db = DBMaker.fileDB("uvm_data.db").make();

            privilegedVisitors = db.<String, PrivilegedVisitor>hashMap("visitors")
                    .keySerializer(Serializer.STRING)
                    .valueSerializer((Serializer<PrivilegedVisitor>) Serializer.JAVA)
                    .createOrOpen();

            events = db.<String, Event>hashMap("events")
                    .keySerializer(Serializer.STRING)
                    .valueSerializer((Serializer<Event>) Serializer.JAVA)
                    .createOrOpen();

            settings = db.<String, String>hashMap("settings")
                    .keySerializer(Serializer.STRING)
                    .valueSerializer(Serializer.STRING)
                    .createOrOpen();

            Runtime.getRuntime().addShutdownHook(new Thread(VisitorManager::shutdown));


            if (settings.isEmpty() || settings.get("hostname") == null || settings.get("token") == null) {
                System.out.println("[UniFi VisitorManager] Database is empty. Starting setup wizard...");
                settings.clear();
                SettingsServer.start();
                return; // works because we run inside a Docker container with restart: always
            }

            System.out.println("[UniFi VisitorManager] Database initialized successfully!");
            System.out.println("[UniFi VisitorManager] Settings: ");
            System.out.println("---------------[UniFi Settings]---------------");
            System.out.println("- Hostname: " + settings.get("hostname"));
            System.out.println("- Token: " + (settings.get("token") != null ? "<redacted>" : "<MISSING>"));
            System.out.println("---------------[Apple Settings]---------------");
            System.out.println("- Apple Team ID: " + settings.get("appleTeamId"));
            System.out.println("- Apple PassType ID: " + settings.get("applePassTypeId"));
            System.out.println("- Organization Name: " + settings.get("appleOrgName"));
            System.out.println("- Private Key Path: " + settings.get("appleP12Path"));
            System.out.println("- Private Key Password: " + (settings.get("appleP12Password") != null ? "<redacted>" : "<MISSING>"));
            System.out.println("- Private WWDR Path: " + settings.get("appleWwdrPath"));
            System.out.println("---------------[Mailer Settings]---------------");
            System.out.println("- SMTP Host: " + settings.get("smtpHost"));
            System.out.println("- SMTP Port: " + settings.get("smtpPort"));
            System.out.println("- SMTP Username: " + settings.get("smtpUser"));
            System.out.println("- SMTP Password: " + (settings.get("smtpPass") != null ? "<redacted>" : "<MISSING>"));
            System.out.println("- From Name: " + settings.get("smtpFromName"));
            System.out.println("---------------[UVM Settings End]---------------");
            System.out.println("[UniFi MailSubsystem] Starting MailScheduler...");
            mailScheduler = new MailScheduler(5);

            System.out.println("[UniFi VisitorManager] Starting API Client...");

            try {
                apiClient = new ApiClient(settings.get("hostname"), settings.get("token"));
                apiClient.getVisitors();
            } catch (Exception e) {
                System.err.println("[UniFi VisitorManager] Failed to initialize API client: " + e.getMessage());
                System.err.println("[UniFi VisitorManager] Starting setup wizard...");
                settings.clear();
                SettingsServer.start("Failed to connect to UniFi Controller. Please check your settings and try again.");
                return;
            }

            gc = new GarbageCollector(3600000);
            gc.start();

            System.out.println("[UniFi VisitorManager] Starting main program...");
            MainServer.start();

        } catch (Exception e) {
            System.err.println("[UniFi VisitorManager] Failed to initialize database: " + e.getMessage());
            System.err.println("[UniFi VisitorManager] Exiting...");
            System.exit(1);
        }
    }

    /**
     * IMPORTANT! Always call this method instead of System.exit() to preserve the integrity of the database and prevent data loss.
     * This will ensure that the database is properly closed before exiting the application.
     */
    public static void shutdown(boolean noExit) {
        if (db != null && !db.isClosed()) {
            if (mailScheduler != null) mailScheduler.shutdown();
            db.close();
            System.out.println("[UniFi VisitorManager] Database closed successfully!");
        }
        if (gc != null) gc.stop();
        if (!noExit)
            System.exit(0);
    }

    public static void shutdown() {
        shutdown(true);
    }

    public static HTreeMap<String, String> getSettings() {
        return settings;
    }

    public static DB getDb() {
        return db;
    }

    public static ApiClient getApiClient() {
        return apiClient;
    }

    public static HTreeMap<String, PrivilegedVisitor> getPrivilegedVisitors() {
        return privilegedVisitors;
    }

    public static MailScheduler getMailScheduler() {
        return mailScheduler;
    }

    public static HTreeMap<String, Event> getEvents() {
        return events;
    }

    public static void dispatchRestart(int milliseconds) {
        new Thread(() -> {
            try {
                Thread.sleep(milliseconds);
            } catch (InterruptedException _) {}
            System.out.println("[UniFi SettingsServer] Restarting UniFi VisitorManager...");
            VisitorManager.shutdown(false);
        }).start();
    }
}
