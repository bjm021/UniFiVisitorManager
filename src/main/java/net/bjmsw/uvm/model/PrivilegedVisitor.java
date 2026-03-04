package net.bjmsw.uvm.model;

import java.io.Serializable;

public class PrivilegedVisitor implements Serializable {

    private String firstName, lastName, email, id;

    /**
     * Default constructor for the PrivilegedVisitor class.
     * Initializes a new instance of the PrivilegedVisitor class with no properties set.
     * This constructor is needed for deserialization purposes.
     */
    private PrivilegedVisitor() {}

    public PrivilegedVisitor(String firstname, String lastname, String email, String id) {
        this.firstName = firstname;
        this.lastName = lastname;
        this.email = email;
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getEmail() {
        return email;
    }
    public String getId() {
        return id;
    }

}
