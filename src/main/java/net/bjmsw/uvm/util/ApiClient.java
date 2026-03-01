package net.bjmsw.uvm.util;

import net.bjmsw.uvm.model.Visitor;
import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.ssl.SSLContexts;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This is a Simple REST API Client for the UniFi API
 */
public class ApiClient {

    private static final boolean DEBUG = true;
    private final String baseURL;
    private final String token;

    private CloseableHttpClient httpClient;

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
     * Make a request to the API /api/v1/developer/visitors
     * @return List of Visitors
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

    public Visitor getVisitor(String id) {
        if (DEBUG)
            System.out.println("[UniFi /getVisitors] Making call to: " + baseURL + "/api/v1/developer/visitors/" + id);
        HttpGet get = new HttpGet(baseURL + "/api/v1/developer/visitors/" + id);
        get.setHeader("Authorization", "Bearer " + token);
        get.setHeader("Accept", "application/json");

        try (CloseableHttpResponse resp = httpClient.execute(get)) {
            String s = IOUtils.toString(resp.getEntity().getContent(), StandardCharsets.UTF_8);
            JSONObject root = new JSONObject(s);
            JSONObject visitor = root.getJSONObject("data");
            return new Visitor(visitor);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static CloseableHttpClient createInsecureClient() throws Exception {
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

    public Visitor createVisitor(String firstName, String lastName, String email, long startTime, long endTime, String remarks) {
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

        String allGroupsId = getAllDoorsGroupId();
        JSONArray resources = new JSONArray();
        JSONObject resource = new JSONObject();
        resource.put("id", allGroupsId);
        resource.put("type", "door_group");
        resources.put(resource);
        body.put("resources", resources);

        post.setEntity(new StringEntity(body.toString(), StandardCharsets.UTF_8));

        try (CloseableHttpResponse resp = httpClient.execute(post)) {
            String s = IOUtils.toString(resp.getEntity().getContent(), StandardCharsets.UTF_8);
            JSONObject root = new JSONObject(s);
            JSONObject visitor = root.getJSONObject("data");
            return new Visitor(visitor);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean assignQR(Visitor visitor) {
        if (DEBUG)
            System.out.println("[UniFi /assignQR] Making call to: " + baseURL + "/api/v1/developer/visitors/" + visitor.getId() + "/qr_codes");

        HttpPut put = new HttpPut(baseURL + "/api/v1/developer/visitors/" + visitor.getId() + "/qr_codes");
        return simpleNoDataResponse(put);
    }

    public boolean unAssignQR(Visitor visitor) {
        if (DEBUG)
            System.out.println("[UniFi /assignQR] Making call to: " + baseURL + "/api/v1/developer/visitors/" + visitor.getId() + "/qr_codes");

        HttpDelete delete = new HttpDelete(baseURL + "/api/v1/developer/visitors/" + visitor.getId() + "/qr_codes");
        return simpleNoDataResponse(delete);
    }

    public String getAllDoorsGroupId() {
        if (DEBUG)
            System.out.println("[UniFi /getAllDoorsGroupId] Making call to: " + baseURL + "/api/v1/developer/door_groups/topology");

        HttpGet get = new HttpGet(baseURL + "/api/v1/developer/door_groups/topology");
        get.setHeader("Authorization", "Bearer " + token);
        get.setHeader("Accept", "application/json");

        try (CloseableHttpResponse resp = httpClient.execute(get)) {
            String s = IOUtils.toString(resp.getEntity().getContent(), StandardCharsets.UTF_8);
            JSONObject root = new JSONObject(s);
            for (Object doorGroup : root.getJSONArray("data")) {
                JSONObject doorGroupData = (JSONObject) doorGroup;
                if (Objects.equals(doorGroupData.getString("name"), "All Doors")) {
                    return doorGroupData.getString("id");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean simpleNoDataResponse(HttpUriRequestBase base) {
        base.setHeader("Authorization", "Bearer " + token);
        base.setHeader("Accept", "application/json");

        try (CloseableHttpResponse resp = httpClient.execute(base)) {
            String s = IOUtils.toString(resp.getEntity().getContent(), StandardCharsets.UTF_8);
            JSONObject root = new JSONObject(s);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void downloadQR(Visitor visitor) {
        if (DEBUG) {
            System.out.println("[UniFi /downloadQR] Making call to: " + baseURL + "/api/v1/developer/credentials/qr_codes/download/" + visitor.getId());
        }

        HttpGet get = new HttpGet(baseURL + "/api/v1/developer/credentials/qr_codes/download/" + visitor.getId());
        get.setHeader("Authorization", "Bearer " + token);

        Path out = Paths.get(visitor.getId() + ".png");

        // Put BOTH the network connection and the file stream inside the parentheses!
        try (CloseableHttpResponse resp = httpClient.execute(get);
             java.io.OutputStream outStream = java.nio.file.Files.newOutputStream(out)) {

            // Copy from the network stream directly into the file stream
            IOUtils.copy(resp.getEntity().getContent(), outStream);
            System.out.println("QR Code saved to: " + out.toAbsolutePath());

        } catch (IOException e) {
            System.err.println("[UniFi /downloadQR] Failed to download QR for " + visitor.getId());
            e.printStackTrace();
        }
    }
}
