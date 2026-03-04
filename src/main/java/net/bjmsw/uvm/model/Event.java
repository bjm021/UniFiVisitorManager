package net.bjmsw.uvm.model;

import java.util.List;

public class Event {

    private final String id, name, description;

    private final int start_time, end_time;

    private List<PrivilegedVisitor> visitors;

    public Event(String id, String name, String description, int start_time, int end_time) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.start_time = start_time;
        this.end_time = end_time;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getStart_time() {
        return start_time;
    }

    public int getEnd_time() {
        return end_time;
    }

    public List<PrivilegedVisitor> getVisitors() {
        return visitors;
    }

    public String getDescription() {
        return description;
    }
}
