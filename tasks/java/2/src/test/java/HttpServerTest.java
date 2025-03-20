import com.server.HttpServer;
import org.junit.jupiter.api.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HttpServerTest {

    private static HttpServer server;

    @BeforeAll
    public static void setup() {
        server = new HttpServer("localhost", 8080);

        server.addHandler("GET", "/", (req, res) -> {
            res.setBody("Welcome to the HTTP Server!");
        });

        server.addHandler("POST", "/echo", (req, res) -> {
            res.setBody("You posted: " + req.getBody());
        });

        server.addHandler("PUT", "/update", (req, res) -> {
            res.setBody("Updated with: " + req.getBody());
        });

        server.addHandler("DELETE", "/delete", (req, res) -> {
            res.setBody("Deleted");
        });

        server.addHandler("PATCH", "/modify", (req, res) -> {
            res.setBody("Patched with: " + req.getBody());
        });

        server.addHandler("POST", "/headers", (req, res) -> {
            // Access headers
            String userAgent = req.getHeaders().getOrDefault("User-Agent", "Unknown");
            res.setBody("User-Agent: " + userAgent);
        });

        server.addHandler("POST", "/params", (req, res) -> {
            // Access request parameters
            String method = req.getMethod();
            String path = req.getPath();
            Map<String, String> headers = req.getHeaders();
            String body = req.getBody();

            res.setBody("Method: " + method + ", Path: " + path + ", Headers: " + headers + ", Body: " + body);
        });

        server.addHandler("POST", "/multipart", (req, res) -> {
            // Handle multipart form data
            if (req.getHeaders().getOrDefault("Content-Type", "").startsWith("multipart/form-data")) {
                // Simulate parsing multipart data
                res.setBody("Multipart data received");
            } else {
                res.setStatusCode(400);
                res.setBody("Bad Request: Expected multipart/form-data");
            }
        });

        new Thread(server::start).start();

        // Add shutdown hook to stop the server gracefully
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
    }

    @AfterAll
    public static void tearDown() {
        server.stop();
    }

    @Test
    @Order(1)
    public void testGetRoot() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8080/").openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        String response = readResponse(connection);

        assertEquals(200, responseCode);
        assertEquals("Welcome to the HTTP Server!", response);
    }

    @Test
    @Order(2)
    public void testPostEcho() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8080/echo").openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        String postData = "Hello, Server!";
        try (OutputStream os = connection.getOutputStream()) {
            os.write(postData.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = connection.getResponseCode();
        String response = readResponse(connection);

        assertEquals(200, responseCode);
        assertEquals("You posted: " + postData, response);
    }

    @Test
    @Order(3)
    public void testPutUpdate() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8080/update").openConnection();
        connection.setRequestMethod("PUT");
        connection.setDoOutput(true);

        String putData = "Update Data";
        try (OutputStream os = connection.getOutputStream()) {
            os.write(putData.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = connection.getResponseCode();
        String response = readResponse(connection);

        assertEquals(200, responseCode);
        assertEquals("Updated with: " + putData, response);
    }

    @Test
    @Order(4)
    public void testDelete() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8080/delete").openConnection();
        connection.setRequestMethod("DELETE");

        int responseCode = connection.getResponseCode();
        String response = readResponse(connection);

        assertEquals(200, responseCode);
        assertEquals("Deleted", response);
    }

    @Test
    @Order(5)
    public void testPatchMethod() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8080/modify").openConnection();
        connection.setRequestMethod("POST");  // Use POST as the base method
        connection.setRequestProperty("X-HTTP-Method-Override", "PATCH");  // Add custom header for PATCH
        connection.setDoOutput(true);

        String patchData = "Patch Data";
        try (OutputStream os = connection.getOutputStream()) {
            os.write(patchData.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = connection.getResponseCode();
        String response = readResponse(connection);

        assertEquals(200, responseCode);
        assertEquals("Patched with: " + patchData, response);
    }

    @Test
    @Order(6)
    public void testAccessRequestParameters() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8080/params").openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        String requestBody = "Test Body";
        connection.setRequestProperty("Custom-Header", "HeaderValue");
        try (OutputStream os = connection.getOutputStream()) {
            os.write(requestBody.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = connection.getResponseCode();
        String response = readResponse(connection);

        assertEquals(200, responseCode);

        assertTrue(response.contains("Method: POST"));
        assertTrue(response.contains("Path: /params"));
        assertTrue(response.contains("Headers:"));
        assertTrue(response.contains("Custom-Header=HeaderValue"));
        assertTrue(response.contains("Body: " + requestBody));
    }

    @Test
    @Order(7)
    public void testHeadersMap() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8080/headers").openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", "JUnit Test");
        connection.setDoOutput(true);

        int responseCode = connection.getResponseCode();
        String response = readResponse(connection);

        assertEquals(200, responseCode);
        assertEquals("User-Agent: JUnit Test", response);
    }

    @Test
    @Order(8)
    public void testResponseCreation() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8080/custom-response").openConnection();
        connection.setRequestMethod("GET");

        // Add a handler that sets custom status code and headers
        server.addHandler("GET", "/custom-response", (req, res) -> {
            res.setStatusCode(201);
            res.setReasonPhrase("Created");
            res.setHeader("Custom-Header", "CustomValue");
            res.setBody("Custom Response Body");
        });

        int responseCode = connection.getResponseCode();
        String response = readResponse(connection);
        String customHeader = connection.getHeaderField("Custom-Header");

        assertEquals(201, responseCode);
        assertEquals("CustomValue", customHeader);
        assertEquals("Custom Response Body", response);
    }

    @Test
    @Order(9)
    public void testMultipartFormData() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8080/multipart").openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        String multipartData = "--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"field1\"\r\n\r\n"
                + "value1\r\n"
                + "--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"field2\"\r\n\r\n"
                + "value2\r\n"
                + "--" + boundary + "--\r\n";

        try (OutputStream os = connection.getOutputStream()) {
            os.write(multipartData.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = connection.getResponseCode();
        String response = readResponse(connection);

        assertEquals(200, responseCode);
        assertEquals("Multipart data received", response);
    }

    @Test
    @Order(10)
    public void testNotFound() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8080/unknown").openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        String response = readResponse(connection);

        assertEquals(404, responseCode);
        assertEquals("404 Not Found", response);
    }

    private String readResponse(HttpURLConnection connection) throws IOException {
        try (InputStream is = connection.getResponseCode() >= 400
                ? connection.getErrorStream()
                : connection.getInputStream()) {
            return new BufferedReader(new InputStreamReader(is))
                    .lines()
                    .reduce("", (acc, line) -> acc + line);
        }
    }
}
