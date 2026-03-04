package net.bjmsw.uvm.model;

import net.bjmsw.uvm.util.ApiClient;
import net.bjmsw.uvm.util.TimeUtils;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Represents a visitor in the UniFi system.
 */
public class Visitor implements Serializable {

    private String id, firstName, lastName, status, inviter_id, inviter_name, remarks, email, visitor_company;

    private long start_time, end_time;

    /**
     * Default constructor for the Visitor class.
     * Initializes a new instance of the Visitor class with no properties set.
     * This constructor is needed for deserialization purposes.
     */
    private Visitor() {}

    public Visitor(String id, String firstName, String lastName, String status, String inviter_id, String inviter_name, String remarks, String email, String visitor_company, long start_time, long end_time) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = status;
        this.inviter_id = inviter_id;
        this.inviter_name = inviter_name;
        this.remarks = remarks;
        this.email = email;
        this.visitor_company = visitor_company;
        this.start_time = start_time;
        this.end_time = end_time;
    }

    public Visitor(JSONObject allData) {
        this.id = allData.getString("id");
        this.firstName = allData.getString("first_name");
        this.lastName = allData.getString("last_name");
        this.status = allData.getString("status");
        this.inviter_id = allData.getString("inviter_id");
        this.inviter_name = allData.getString("inviter_name");
        this.remarks = allData.getString("remarks");
        this.email = allData.getString("email");
        this.visitor_company = allData.getString("visitor_company");
        this.start_time = allData.getLong("start_time");
        this.end_time = allData.getLong("end_time");
     }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getInviter_id() {
        return inviter_id;
    }

    public String getInviter_name() {
        return inviter_name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getStatus() {
        return status;
    }

    public String getVisitor_company() {
        return visitor_company;
    }

    public boolean assignQR(ApiClient apiClient) {
        return apiClient.assignQR(this);
    }

    public boolean unAssignQR(ApiClient apiClient) {
        return apiClient.unAssignQR(this);
    }

    public void downloadQR(ApiClient apiClient) {
        apiClient.downloadQR(this);
    }

    public long getStart_time() {
        return start_time;
    }

    public long getEnd_time() {
        return end_time;
    }

    /**
     * Used in the frontend, display human readable visitor date range (times are Unix timestamps)
     * @return String
     */
    public String getPrettyDateRange() {
        return TimeUtils.getPrettyDateRange(start_time, end_time);
    }

    @Override
    public String toString() {
        return "[UniFi Visitor] " + firstName + " " + lastName + " (" + email + ") [" + remarks + "] <" + id + ">";
    }
}
