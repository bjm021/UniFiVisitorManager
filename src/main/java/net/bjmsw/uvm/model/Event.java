package net.bjmsw.uvm.model;

import net.bjmsw.uvm.VisitorManager;
import net.bjmsw.uvm.util.ApiClient;
import net.bjmsw.uvm.util.TimeUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Event implements Serializable {
    private String id, name, description;

    AccessResource accessResource;

    private long start_time, end_time;

    private List<PrivilegedVisitor> visitors;
    private List<Visitor> correspondingConsoleVisitorObjects;

    /**
     * Default constructor for the Event class.
     * Initializes a new instance of the Event class with no properties set.
     * This is needed for deserialization purposes.
     */
    private Event() {}

    public Event(String id, String name, String description, long start_time, long end_time, AccessResource accessResource, List<PrivilegedVisitor> visitors) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.start_time = start_time;
        this.end_time = end_time;
        this.visitors = visitors;
        this.accessResource = accessResource;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getStart_time() {
        return start_time;
    }

    public long getEnd_time() {
        return end_time;
    }

    public List<PrivilegedVisitor> getVisitors() {
        return visitors;
    }

    public String getDescription() {
        return description;
    }

    private boolean publish(ApiClient apiClient) {

        correspondingConsoleVisitorObjects = new ArrayList<>();
        for (int i = 0; i < visitors.size(); i++) {
            PrivilegedVisitor pv = visitors.get(i);
            correspondingConsoleVisitorObjects.add(apiClient.createVisitor(pv.getFirstName(), pv.getLastName(), pv.getEmail(), start_time, end_time, "UVM_EVENT_" + id, accessResource.getId(), accessResource.getType()));
        }

        for (Visitor v : correspondingConsoleVisitorObjects) {
            VisitorManager.getMailScheduler().scheduleMail(v, name, description);
        }

        return false;
    }

    /**
     * Publishes the current event using the provided API client. The process is executed in a
     * separate thread. Upon completion, it logs a message indicating success or failure.
     *
     * @param apiClient the instance of {@code ApiClient} used to facilitate the publishing process
     */
    public void publishAsync(ApiClient apiClient) {
        new Thread(() -> {
            try {
                if (publish(apiClient)) {
                    System.out.println("[Event] Published event: " + id);
                } else {
                    System.out.println("[Event] Failed to publish event: " + id);
                }
            } catch (Exception e) {
                System.err.println("[Event] Failed to publish event: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    public String getPrettyDateRange() {
        return TimeUtils.getPrettyDateRange(start_time, end_time);
    }
}

