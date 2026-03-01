package net.bjmsw.uvm.model;

import java.io.Serializable;

public class PrivilegedVisitor implements Serializable {

    private final String firstname, lastname, email, id;

    public PrivilegedVisitor(String firstname, String lastname, String email, String id) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.id = id;
    }

    public String getFirstName() {
        return firstname;
    }
    public String getLastName() {
        return lastname;
    }
    public String getEmail() {
        return email;
    }
    public String getId() {
        return id;
    }

}
