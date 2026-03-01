package net.bjmsw.uvm.model;

import net.bjmsw.uvm.util.ApiClient;
import org.json.JSONObject;
import org.jspecify.annotations.NonNull;
import org.mapdb.DataInput2;
import org.mapdb.DataOutput2;
import org.mapdb.Serializer;

import java.io.IOException;
import java.io.Serializable;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Represents a visitor in the UniFi system.
 */
public class Visitor implements Serializable {

    private final String id, firstname, lastname, status, inviter_id, inviter_name, remarks, email, visitor_company;

    private final int start_time, end_time;

    private final JSONObject allData;

    public Visitor(JSONObject allData, String id, String firstName, String lastName, String status, String inviter_id, String inviter_name, String remarks, String email, String visitor_company, int start_time, int end_time) {
        this.allData = allData;
        this.id = id;
        this.firstname = firstName;
        this.lastname = lastName;
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
        this.allData = allData;
        this.id = allData.getString("id");
        this.firstname = allData.getString("first_name");
        this.lastname = allData.getString("last_name");
        this.status = allData.getString("status");
        this.inviter_id = allData.getString("inviter_id");
        this.inviter_name = allData.getString("inviter_name");
        this.remarks = allData.getString("remarks");
        this.email = allData.getString("email");
        this.visitor_company = allData.getString("visitor_company");
        this.start_time = allData.getInt("start_time");
        this.end_time = allData.getInt("end_time");
     }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstname;
    }

    public String getInviter_id() {
        return inviter_id;
    }

    public String getInviter_name() {
        return inviter_name;
    }

    public String getLastName() {
        return lastname;
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

    public int getStart_time() {
        return start_time;
    }

    public int getEnd_time() {
        return end_time;
    }

    /**
     * Used in the frontend, display human readable visitor date range (times are Unix timestamps)
     * @return String
     */
    public String getPrettyDateRange() {
        // 1. Always use a specific ZoneId instead of systemDefault()
        // Replace "Europe/Berlin" with the actual timezone of the UniFi site
        ZoneId siteZone = ZoneId.of("Europe/Berlin");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String start = formatter.format(
                java.time.Instant.ofEpochSecond(start_time)
                        .atZone(siteZone)
        );

        String end = formatter.format(
                java.time.Instant.ofEpochSecond(end_time)
                        .atZone(siteZone)
        );

        return start + " to " + end;
    }

    @Override
    public String toString() {
        return "[UniFi Visitor] " + firstname + " " + lastname + " (" + email + ") [" + remarks + "] <" + id + ">";
    }
}
