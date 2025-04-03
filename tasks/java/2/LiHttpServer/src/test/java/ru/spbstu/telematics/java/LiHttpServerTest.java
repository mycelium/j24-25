package ru.spbstu.telematics.java;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class LiHttpServerTest {
    private static final int TEST_PORT = 8081;
    private static final String TEST_HOST = "localhost";
    private LiHttpServer server;
    private Thread serverThread;

    @BeforeEach
    public void setUp() {
        Router router = new Router();
        router.addDefaultRoutes();
        LiServerConfig config = new LiServerConfig(TEST_HOST, TEST_PORT, 1, true);
        server = new LiHttpServer(config, router);
    }

    private void testStartServer() throws InterruptedException {
        serverThread = new Thread(() -> {
            try {
                server.start();
            } catch (IOException e) {
                fail("Server failed to start: " + e.getMessage());
            }
        });
        serverThread.start();
        Thread.sleep(1000);
    }

    @AfterEach
    public void testCloseServer() {
        if (server != null) {
            server.closeServer();
        }
        if (serverThread != null) {
            try {
                serverThread.join(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }


    @Test
    public void testPingRoute() throws Exception {
        testStartServer();

        URL url = new URL("http://" + TEST_HOST + ":" + TEST_PORT + "/ping");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        assertEquals(200, conn.getResponseCode());
        assertTrue(conn.getContentType().startsWith("text/plain"));

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            assertEquals("Сервер запущен", reader.readLine());
        }
    }

    @Test
    public void testJsonResponse() throws Exception {
        testStartServer();

        URL url = new URL("http://" + TEST_HOST + ":" + TEST_PORT + "/health");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        assertEquals(200, conn.getResponseCode());
        assertTrue(conn.getContentType().startsWith("application/json"));

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String response = reader.readLine();
            assertTrue(response.contains("\"status\":\"OK\""));
            assertTrue(response.contains("\"timestamp\":"));
        }
    }

    @Test
    public void testNotFoundRoute() throws Exception {
        testStartServer();

        URL url = new URL("http://" + TEST_HOST + ":" + TEST_PORT + "/nonexistent");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        assertEquals(404, conn.getResponseCode());

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
            assertEquals("Not found", reader.readLine());
        }
    }

    @Test
    public void testPostUsers() throws Exception {
        testStartServer();

        URL url = new URL("http://" + TEST_HOST + ":" + TEST_PORT + "/users");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write("{}".getBytes(StandardCharsets.UTF_8));
        }

        assertEquals(201, conn.getResponseCode());

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            assertEquals("Пользователь создан", reader.readLine());
        }
    }

    @Test
    public void testGetUsers() throws Exception {
        testStartServer();

        URL url = new URL("http://" + TEST_HOST + ":" + TEST_PORT + "/users");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        assertEquals(200, conn.getResponseCode());
        assertTrue(conn.getContentType().startsWith("application/json"));

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String response = reader.readLine();
            assertEquals("[{\"id\": 1, \"name\": \"Frank\"}]", response);
        }
    }

    @Test
    public void testStaticFileServing() throws Exception {
        // Создаем временный файл для теста
        Path tempDir = Files.createTempDirectory("static_test");
        Path testFile = tempDir.resolve("test.txt");
        Files.write(testFile, "Hello from static file".getBytes(StandardCharsets.UTF_8));

        // Переопределяем стандартные маршруты
        Router router = new Router();
        router.addRoute("GET", "/static/*", req -> {
            String requestedFile = req.getPath().substring("/static/".length());
            Path filePath = tempDir.resolve(requestedFile).normalize();
            try {
                return LiHttpResponse.file(filePath);
            } catch (IOException e) {
                return new LiHttpResponse(404, "File not found");
            }
        });

        LiServerConfig config = new LiServerConfig(TEST_HOST, TEST_PORT, 1, true);
        server = new LiHttpServer(config, router);
        testStartServer();

        try {
            URL url = new URL("http://" + TEST_HOST + ":" + TEST_PORT + "/static/test.txt");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            assertEquals(200, conn.getResponseCode());
            assertTrue(conn.getContentType().startsWith("text/plain"));

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                assertEquals("Hello from static file", reader.readLine());
            }
        } finally {
            Files.deleteIfExists(testFile);
            Files.deleteIfExists(tempDir);
        }
    }

    @Test
    public void testBinaryFileUpload() throws Exception {
        byte[] testData = new byte[256];
        for (int i = 0; i < 256; i++) {
            testData[i] = (byte) i;
        }

        Router router = new Router();
        router.addRoute("POST", "/upload", req -> {
            try {
                String contentLength = req.getHeader("Content-Length");
                if (contentLength != null && Integer.parseInt(contentLength) > 0) {
                    return new LiHttpResponse(200, "File uploaded, size: " + contentLength);
                }
                return new LiHttpResponse(400, "Empty file");
            } catch (NumberFormatException e) {
                return new LiHttpResponse(400, "Invalid Content-Length");
            }
        });

        LiServerConfig config = new LiServerConfig(TEST_HOST, TEST_PORT, 1, true);
        server = new LiHttpServer(config, router);
        testStartServer();

        URL url = new URL("http://" + TEST_HOST + ":" + TEST_PORT + "/upload");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/octet-stream");
        conn.setRequestProperty("Content-Length", String.valueOf(testData.length));
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(testData);
        }

        assertEquals(200, conn.getResponseCode());

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            assertEquals("File uploaded, size: 256", reader.readLine());
        }
    }

    @Test
    public void testJsonParsing() throws Exception {
        Router router = new Router();
        router.addRoute("POST", "/json-test", req -> {
            try {
                TestData data = req.parseJson(TestData.class);
                return LiHttpResponse.json("{\"received\":\"" + data.name + "\",\"age\":" + data.age + "}");
            } catch (IOException e) {
                return new LiHttpResponse(400, "Invalid JSON");
            }
        });

        LiServerConfig config = new LiServerConfig(TEST_HOST, TEST_PORT, 1, true);
        server = new LiHttpServer(config, router);
        testStartServer();


        String jsonInput = "{\"name\":\"Alice\",\"age\":25}";

        URL url = new URL("http://" + TEST_HOST + ":" + TEST_PORT + "/json-test");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonInput.getBytes(StandardCharsets.UTF_8));
        }

        assertEquals(200, conn.getResponseCode());
        assertTrue(conn.getContentType().startsWith("application/json"));

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String response = reader.readLine();
            assertTrue(response.contains("\"received\":\"Alice\""));
            assertTrue(response.contains("\"age\":25"));
        }
    }

    @Test
    public void testInvalidJsonHandling() throws Exception {
        Router router = new Router();
        router.addRoute("POST", "/api/echo", req -> {
            try {
                String jsonBody = req.getBody();
                new ObjectMapper().readTree(jsonBody);
                return LiHttpResponse.json("{\"received\":" + jsonBody + "}");
            } catch (Exception e) {
                return LiHttpResponse.json("{\"error\":\"Invalid JSON\"}").setStatusCode(400);
            }
        });

        LiServerConfig config = new LiServerConfig(TEST_HOST, TEST_PORT, 1, true);
        server = new LiHttpServer(config, router);
        testStartServer();

        String invalidJson = "{name: Alice}";

        URL url = new URL("http://" + TEST_HOST + ":" + TEST_PORT + "/api/echo");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(invalidJson.getBytes(StandardCharsets.UTF_8));
        }

        assertEquals(400, conn.getResponseCode());

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
            String response = reader.readLine();
            assertTrue(response.contains("\"error\":\"Invalid JSON\""));
        }
    }

    @Test
    public void testLargeJsonPayload() throws Exception {
        StringBuilder largeJson = new StringBuilder("{");
        for (int i = 0; i < 1000; i++) {
            if (i > 0) largeJson.append(",");
            largeJson.append("\"field").append(i).append("\":\"value").append(i).append("\"");
        }
        largeJson.append("}");

        Router router = new Router();
        router.addRoute("POST", "/large-json", req -> {
            try {
                Map<?, ?> data = req.parseJson(Map.class);
                return new LiHttpResponse(200, "Received fields: " + data.size());
            } catch (IOException e) {
                return new LiHttpResponse(400, "Invalid JSON");
            }
        });

        LiServerConfig config = new LiServerConfig(TEST_HOST, TEST_PORT, 1, true);
        server = new LiHttpServer(config, router);
        testStartServer();

        URL url = new URL("http://" + TEST_HOST + ":" + TEST_PORT + "/large-json");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(largeJson.toString().getBytes(StandardCharsets.UTF_8));
        }

        assertEquals(200, conn.getResponseCode());

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            assertEquals("Received fields: 1000", reader.readLine());
        }
    }

    static class TestData {
        public String name;
        public int age;
    }
}