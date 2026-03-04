package net.bjmsw.uvm.util;

import net.bjmsw.uvm.VisitorManager;
import net.bjmsw.uvm.model.AccessResource;
import net.bjmsw.uvm.model.Visitor;
import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.ssl.SSLContexts;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jspecify.annotations.Nullable;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This is a Simple REST API Client for the UniFi API
 */
public class ApiClient {

    private static final boolean DEBUG = false;
    private final String baseURL;
    private final String token;

    private final CloseableHttpClient httpClient;

    public ApiClient(String baseURL, String token) throws Exception {
        this.baseURL = baseURL;
        this.token = token;
        this.httpClient = createInsecureClient();
    }

    public ApiClient(String hostname, int port, String token) throws Exception {
        this.baseURL = "https://" + hostname + ":" + port;
        this.token = token;
        this.httpClient = createInsecureClient();
    }


    /**
     * Retrieves a list of visitors from the UniFi API.
     *
     * @return a {@code List} of {@code Visitor} objects representing the retrieved visitors
     */
    public List<Visitor> getVisitors() {
        if (DEBUG)
            System.out.println("[UniFi /getVisitors] Making call to: " + baseURL + "/api/v1/developer/visitors");
        List<Visitor> visitorsOut = new ArrayList<>();

        HttpGet get = new HttpGet(baseURL + "/api/v1/developer/visitors");
        get.setHeader("Authorization", "Bearer " + token);
        get.setHeader("Accept", "application/json");

        try (CloseableHttpResponse resp = httpClient.execute(get)) {
            String s = IOUtils.toString(resp.getEntity().getContent(), StandardCharsets.UTF_8);
            JSONObject root = new JSONObject(s);
            JSONArray visitors = root.getJSONArray("data");
            for (Object visitorObj : visitors) {
                JSONObject visitor = (JSONObject) visitorObj;
                visitorsOut.add(new Visitor(visitor));
            }
        } catch (IOException e) {
            System.err.println("[UniFi /getVisitors] Failed to get visitors: " + e.getMessage());
            throw new RuntimeException(e);
        }

        return visitorsOut;
    }

    /**
     * Retrieves a specific visitor by their unique identifier from the API.
     *
     * @param id the unique identifier of the visitor to retrieve
     * @return a {@code Visitor} object representing the retrieved visitor, or {@code null} if the visitor was not found
     */
    public Visitor getVisitor(String id) {
        if (DEBUG)
            System.out.println("[UniFi /getVisitors] Making call to: " + baseURL + "/api/v1/developer/visitors/" + id);
        HttpGet get = new HttpGet(baseURL + "/api/v1/developer/visitors/" + id);
        get.setHeader("Authorization", "Bearer " + token);
        get.setHeader("Accept", "application/json");

        return extractVisitor(get);
    }

    /**
     * Creates a new visitor in the UniFi system and assigns them to a specific access resource.
     *
     * @param firstName    the first name of the visitor
     * @param lastName     the last name of the visitor
     * @param email        the email address of the visitor
     * @param startTime    the start time of the visitor's access in Unix timestamp format (seconds)
     * @param endTime      the end time of the visitor's access in Unix timestamp format (seconds)
     * @param remarks      additional notes (e.g., internal Event ID)
     * @param resourceId   the UUID of the UniFi location/door
     * @param resourceType the type of the resource (e.g., "building", "door", "floor")
     * @return the created Visitor, or null if it failed
     */
    public Visitor createVisitor(String firstName, String lastName, String email, long startTime, long endTime, String remarks, String resourceId, String resourceType) {
        if (DEBUG)
            System.out.println("[UniFi /createVisitor] Making call to: " + baseURL + "/api/v1/developer/visitors");

        HttpPost post = new HttpPost(baseURL + "/api/v1/developer/visitors");
        post.setHeader("Authorization", "Bearer " + token);
        post.setHeader("Accept", "application/json");
        post.setHeader("Content-Type", "application/json");

        JSONObject body = new JSONObject();
        body.put("first_name", firstName);
        body.put("last_name", lastName);
        body.put("email", email);
        body.put("start_time", startTime);
        body.put("end_time", endTime);
        body.put("remarks", remarks);

        JSONArray resources = new JSONArray();
        JSONObject resource = new JSONObject();
        resource.put("id", resourceId);
        resource.put("type", sanitizeResourceType(resourceType));
        resources.put(resource);

        body.put("resources", resources);

        post.setEntity(new StringEntity(body.toString(), StandardCharsets.UTF_8));

        return extractVisitor(post);
    }

    /**
     * Assigns the QR code option to a specific visitor.
     *
     * @param visitor the {@code Visitor} object representing the visitor to whom the QR code will be assigned
     * @return {@code true} if the QR code was successfully assigned, {@code false} otherwise
     */
    public boolean assignQR(Visitor visitor) {
        if (DEBUG)
            System.out.println("[UniFi /assignQR] Making call to: " + baseURL + "/api/v1/developer/visitors/" + visitor.getId() + "/qr_codes");

        HttpPut put = new HttpPut(baseURL + "/api/v1/developer/visitors/" + visitor.getId() + "/qr_codes");
        return simpleNoDataResponse(put);
    }

    /**
     * Unassigns the QR code option from a specific visitor, effectively preventing the visitor from entering any doors using a QR code.
     *
     * @param visitor the {@code Visitor} object representing the visitor whose QR code will be unassigned
     * @return {@code true} if the QR code was successfully unassigned, {@code false} otherwise
     */
    public boolean unAssignQR(Visitor visitor) {
        if (DEBUG)
            System.out.println("[UniFi /assignQR] Making call to: " + baseURL + "/api/v1/developer/visitors/" + visitor.getId() + "/qr_codes");

        HttpDelete delete = new HttpDelete(baseURL + "/api/v1/developer/visitors/" + visitor.getId() + "/qr_codes");
        return simpleNoDataResponse(delete);
    }

    public List<AccessResource> getAccessResources() {
        if (DEBUG)
            System.out.println("[UniFi /getAccessResources] Making call to: " + baseURL + "/api/v1/developer/door_groups/topology");
        List<AccessResource> accessResourcesOut = new ArrayList<>();

        HttpGet get = new HttpGet(baseURL + "/api/v1/developer/door_groups/topology");
        get.setHeader("Authorization", "Bearer " + token);
        get.setHeader("Accept", "application/json");

        try (CloseableHttpResponse resp = httpClient.execute(get)) {
            if (resp.getCode() >= 300) {
                System.err.println("[UniFi API Error] Status: " + resp.getCode());
                return accessResourcesOut;
            }

            String jsonString = IOUtils.toString(resp.getEntity().getContent(), StandardCharsets.UTF_8);
            JSONObject root = new JSONObject(jsonString);

            if (!"SUCCESS".equals(root.optString("code"))) {
                System.err.println("[UniFi API Error] " + root.optString("msg"));
                return accessResourcesOut;
            }

            JSONArray dataArray = root.optJSONArray("data");
            if (dataArray == null) return accessResourcesOut;

            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject topologyData = dataArray.getJSONObject(i);

                AccessResource ar = new AccessResource(
                        topologyData.getString("id"),
                        topologyData.getString("name"),
                        topologyData.getString("type"),
                        isResourceAGroup(topologyData.getString("type"))
                );

                // Use optJSONArray to prevent crashes if it's missing
                JSONArray floors = topologyData.optJSONArray("resource_topologies");

                if (floors != null) {
                    // THE FIX: Loop through ALL floors/location groups instead of just get(0)
                    for (int j = 0; j < floors.length(); j++) {
                        JSONObject floorData = floors.getJSONObject(j);
                        JSONArray resources = floorData.optJSONArray("resources");

                        if (resources != null) {
                            for (int k = 0; k < resources.length(); k++) {
                                JSONObject resourceData = resources.getJSONObject(k);
                                AccessResource arc = new AccessResource(
                                        resourceData.getString("id"),
                                        resourceData.getString("name"),
                                        resourceData.getString("type"),
                                        isResourceAGroup(resourceData.getString("type"))
                                );
                                ar.addChild(arc);
                            }
                        }
                    }
                }
                accessResourcesOut.add(ar);
            }
        } catch (IOException | org.json.JSONException e) {
            System.err.println("[UniFi /getAccessResources] Failed to parse topology: " + e.getMessage());
            e.printStackTrace();
        }

        return accessResourcesOut;
    }

    /**
     * Downloads the QR code associated with the specified visitor and saves it as a PNG file.
     * The file is named using the visitor's unique identifier.
     *
     * @param visitor the {@code Visitor} object for which the QR code is to be downloaded
     */
    public void downloadQR(Visitor visitor) {
        if (DEBUG) {
            System.out.println("[UniFi /downloadQR] Making call to: " + baseURL + "/api/v1/developer/credentials/qr_codes/download/" + visitor.getId());
        }

        HttpGet get = new HttpGet(baseURL + "/api/v1/developer/credentials/qr_codes/download/" + visitor.getId());
        get.setHeader("Authorization", "Bearer " + token);

        // save to /qr-data/<uuid>.png
        Path out = Paths.get("qr-data", visitor.getId() + ".png");

        try {
            Files.createDirectories(out.getParent());
        } catch (IOException e) {
            System.err.println("[UniFi /downloadQR] Failed to create directory for QR Code: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // Put BOTH the network connection and the file stream inside the parentheses!
        try (CloseableHttpResponse resp = httpClient.execute(get);
             OutputStream outStream = Files.newOutputStream(out)) {

            // Copy from the network stream directly into the file stream
            IOUtils.copy(resp.getEntity().getContent(), outStream);
            System.out.println("QR Code saved to: " + out.toAbsolutePath());

        } catch (IOException e) {
            System.err.println("[UniFi /downloadQR] Failed to download QR for " + visitor.getId());
            e.printStackTrace();
        }
    }

    /**
     * Deletes a visitor with the specified unique identifier from the UniFi API.
     * The deletion process forces removal of the visitor, even if they are currently active.
     *
     * @param visitorID the unique identifier of the visitor to be deleted
     * @return {@code true} if the visitor was successfully deleted, {@code false} otherwise
     */
    public boolean deleteVisitor(String visitorID) {
        if (DEBUG) {
            System.out.println("[UniFi /deleteVisitor] Making call to: " + baseURL + "/api/v1/developer/visitors/" + visitorID);
        }
        HttpDelete delete = new HttpDelete(baseURL + "/api/v1/developer/visitors/" + visitorID + "?is_force=true");
        delete.setHeader("Authorization", "Bearer " + token);
        delete.setHeader("Accept", "application/json");

        return simpleNoDataResponse(delete);
    }

    // ------------------------------ HELPER FUNCTIONS ------------------------------


    /**
     * Creates an {@code CloseableHttpClient} instance that bypasses SSL security checks, allowing connections
     * to be established with SSL endpoints without verifying certificates or hostnames.
     * <p>
     * This is important because UniFi Consoles have self-signed certificates by default, and this client needs to be able to connect to them without
     * throwing SSL exceptions.
     *
     * @return a {@code CloseableHttpClient} instance configured to bypass SSL certificate and
     * hostname verification
     * @throws NoSuchAlgorithmException if the specified SSL algorithm does not exist
     * @throws KeyStoreException        if there is an issue with the trust material
     * @throws KeyManagementException   if there is an error initializing the SSL context
     */
    private static CloseableHttpClient createInsecureClient() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        // 1. SSLContext erstellen, der ALLEM vertraut
        SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(null, TrustAllStrategy.INSTANCE)
                .build();

        // 2. SocketFactory konfigurieren (Hostname-Prüfung abschalten)
        var sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
                .setSslContext(sslContext)
                .setHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .build();

        // 3. Connection Manager mit dieser Factory erstellen
        var connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(sslSocketFactory)
                .build();

        // 4. Den finalen Client bauen
        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();
    }

    /**
     * Extracts a {@code Visitor} object from the HTTP response of the given HTTP request operation.
     *
     * @param op the {@code HttpUriRequestBase} operation to execute and extract a {@code Visitor} from
     * @return the {@code Visitor} object parsed from the response data, or {@code null} if an error occurs during the extraction
     */
    @Nullable
    private Visitor extractVisitor(HttpUriRequestBase op) {
        try (CloseableHttpResponse resp = httpClient.execute(op)) {
            int statusCode = resp.getCode();
            String s = IOUtils.toString(resp.getEntity().getContent(), StandardCharsets.UTF_8);

            if (statusCode >= 300) {
                System.err.println("[UniFi API Error] Status: " + statusCode + ", Response: " + s);
                return null;
            }

            JSONObject root = new JSONObject(s);

            if (!"SUCCESS".equals(root.optString("code"))) {
                System.err.println("[UniFi API Error] UniFi returned error: " + root.optString("msg"));
                return null;
            }

            JSONObject visitor = root.getJSONObject("data");
            return new Visitor(visitor);
        } catch (IOException | org.json.JSONException e) {
            System.err.println("[UniFi API Error] Failed to extract visitor: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Executes the given HTTP request and checks for a simple success response without processing additional data.
     * This method sets necessary headers, including authorization and accept type, then performs the request.
     *
     * @param base the {@code HttpUriRequestBase} representing the HTTP request to be executed
     * @return {@code true} if the request was executed successfully, {@code false} otherwise
     */
    private boolean simpleNoDataResponse(HttpUriRequestBase base) {
        base.setHeader("Authorization", "Bearer " + token);
        base.setHeader("Accept", "application/json");

        try (CloseableHttpResponse resp = httpClient.execute(base)) {
            int statusCode = resp.getCode();
            String s = IOUtils.toString(resp.getEntity().getContent(), StandardCharsets.UTF_8);

            if (statusCode >= 300) {
                System.err.println("[UniFi API Error] Status: " + statusCode + ", Response: " + s);
                return false;
            }

            JSONObject root = new JSONObject(s);

            // FIX: Return true ONLY if UniFi explicitly says SUCCESS
            boolean isSuccess = "SUCCESS".equals(root.optString("code"));
            if (!isSuccess) {
                System.err.println("[UniFi API Error] Operation failed: " + root.optString("msg"));
            }
            return isSuccess;

        } catch (IOException | org.json.JSONException e) {
            System.err.println("[UniFi API Error] Request failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Sanitizes the provided resource type by normalizing and mapping it to a recognized value used by the API.
     *
     * @param topologyType the type of the resource to sanitize (e.g., "building", "door", "floor");
     *                     may be {@code null}, in which case a default value is returned.
     * @return a sanitized {@code String} representing the resource type; returns "door" for {@code null} input or "door_group" for unrecognized types.
     */
    private String sanitizeResourceType(String topologyType) {
        if (topologyType == null) return "door"; // Default fallback

        return switch (topologyType.toLowerCase()) {
            case "building", "site", "floor", "group", "access" ->
                    "door_group"; // Map all structural groups to what the API wants
            case "door" -> "door";
            default -> {
                System.out.println("[UniFi ARSanitizer] Unknown resource type encountered: " + topologyType);
                yield "door_group";
            }
        };
    }

    /**
     * Determines whether the specified resource type corresponds to a group.
     *
     * @param type the resource type to check; typically represents a category such as "building",
     *             "door", or "group". May be {@code null}, in which case the default sanitized value
     *             will be used.
     * @return {@code true} if the sanitized resource type is identified as a group (e.g., "door_group");
     * {@code false} otherwise.
     */
    private boolean isResourceAGroup(String type) {
        return sanitizeResourceType(type).equals("door_group");
    }

}
