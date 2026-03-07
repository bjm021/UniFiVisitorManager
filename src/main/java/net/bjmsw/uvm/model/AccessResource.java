package net.bjmsw.uvm.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This represents a UniFi Access Resource
 * That being a door or door group, basically a resource that can be used in an Access Policy
 */
public class AccessResource implements Serializable {

    private String id, name, type;
    private boolean isGroup;

    private List<AccessResource> children;

    /**
     * Private constructor for the AccessResource class.
     * This constructor is designed to restrict direct instantiation of the AccessResource class
     * from outside its defining class. This is needed for deserialization purposes.
     */
    private AccessResource() {}

    public AccessResource(String id, String name, String type, boolean isGroup) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.isGroup = isGroup;
        this.children = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public List<AccessResource> getChildren() {
        return children;
    }

    public void addChild(AccessResource child) {
        children.add(child);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // print all infos and recursivly print children
        sb.append(name).append(" (").append(id).append(") [").append(type).append("]");
        if (isGroup) {
            sb.append(" (group)");
        }
        for (AccessResource child : children) {
            sb.append("\n").append(child.toString());
        }
        return sb.toString();

    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    /**
     * Recursively searches a list of AccessResources and their children for a specific ID.
     */
    public static AccessResource findResourceById(List<AccessResource> resources, String targetId) {
        if (resources == null) return null;

        for (AccessResource resource : resources) {
            if (resource.getId().equals(targetId)) {
                return resource;
            }
            if (resource.hasChildren()) {
                AccessResource foundChild = findResourceById(resource.getChildren(), targetId);
                if (foundChild != null) {
                    return foundChild;
                }
            }
        }
        return null; // Not found in this branch
    }
}
