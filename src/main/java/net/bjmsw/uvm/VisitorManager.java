package net.bjmsw.uvm;

import net.bjmsw.uvm.model.PrivilegedVisitor;
import net.bjmsw.uvm.model.Visitor;
import net.bjmsw.uvm.servers.MainServer;
import net.bjmsw.uvm.servers.SettingsServer;
import net.bjmsw.uvm.util.ApiClient;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

public class VisitorManager {

    private static DB db;
    private static HTreeMap<String, PrivilegedVisitor> privilegedVisitors;
    private static HTreeMap<String, String> settings;
    private static ApiClient apiClient;

    @SuppressWarnings("unchecked")
    static void main() {

        // prepare Data Storage
        try  {
            // do something with the database
            db = DBMaker.fileDB("visitors.db").make();

            privilegedVisitors = db.<String, PrivilegedVisitor>hashMap("visitors")
                    .keySerializer(Serializer.STRING)
                    .valueSerializer((Serializer<PrivilegedVisitor>) Serializer.JAVA) // Cast to fix the second warning
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
        db.close();
        System.out.println("[UniFi VisitorManager] Database closed successfully!");
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
