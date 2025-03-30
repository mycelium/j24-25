package ru.spbstu.httpserver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;

public class HttpServerTest {
    private static final int TEST_PORT = 8081;
    private static final String TEST_HOST = "localhost";
    private HttpServer server;
    private Thread serverThread;

    @BeforeEach
    public void setUp() {
        server = new HttpServer();
    }

    private void startServer() throws InterruptedException {
        serverThread = new Thread(() -> {
            try {
                server.start(TEST_HOST, TEST_PORT, 1, false);
            } catch (IOException e) {
                fail("Server failed to start: " + e.getMessage());
            }
        });
        serverThread.start();
        Thread.sleep(500); // Даем серверу время на запуск
    }

    @AfterEach
    public void tearDown() {
        if (server != null) {
            server.stop();
        }
        if (serverThread != null) {
            try {
                serverThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Test
    public void testTextResponse() throws Exception {
        server.addRoute("GET", "/text", (req, res) -> {
            res.setStatus(200, "OK");
            res.setBody("Simple text response");
            res.addHeader("Content-Type", "text/plain");
        });
        startServer();

        URL url = new URL("http://" + TEST_HOST + ":" + TEST_PORT + "/text");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        assertEquals(200, conn.getResponseCode());
        assertEquals("text/plain", conn.getContentType());

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            assertEquals("Simple text response", reader.readLine());
        }
    }

    @Test
    public void testBinaryResponse() throws Exception {
        // Создаем временный бинарный файл
        File binaryFile = File.createTempFile("test", ".bin");
        try (FileOutputStream fos = new FileOutputStream(binaryFile)) {
            byte[] testData = new byte[256];
            for (int i = 0; i < 256; i++) {
                testData[i] = (byte) i;
            }
            fos.write(testData);
        }

        server.addRoute("GET", "/binary", (req, res) -> {
            try {
                res.setBody(binaryFile, "application/octet-stream");
                res.setStatus(200, "OK");
            } catch (IOException e) {
                res.setStatus(500, "Internal Server Error");
            }
        });
        startServer();

        try {
            URL url = new URL("http://" + TEST_HOST + ":" + TEST_PORT + "/binary");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            assertEquals(200, conn.getResponseCode());
            assertEquals("application/octet-stream", conn.getContentType());
            assertEquals(String.valueOf(binaryFile.length()), conn.getHeaderField("Content-Length"));

            try (InputStream is = conn.getInputStream()) {
                byte[] responseData = is.readAllBytes();
                assertArrayEquals(Files.readAllBytes(binaryFile.toPath()), responseData);
            }
        } finally {
            binaryFile.delete();
        }
    }

    @Test
    public void testMixedContentTypes() throws Exception {
        // Создаем тестовые файлы
        File textFile = File.createTempFile("text", ".txt");
        Files.writeString(textFile.toPath(), "Text content");

        File imageFile = File.createTempFile("image", ".png");
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            byte[] imageData = new byte[100];
            new java.util.Random().nextBytes(imageData);
            fos.write(imageData);
        }

        server.addRoute("GET", "/text-file", (req, res) -> {
            try {
                res.setBody(textFile, "text/plain");
                res.setStatus(200, "OK");
            } catch (IOException e) {
                res.setStatus(500, "Internal Server Error");
            }
        });

        server.addRoute("GET", "/image", (req, res) -> {
            try {
                res.setBody(imageFile, "image/png");
                res.setStatus(200, "OK");
            } catch (IOException e) {
                res.setStatus(500, "Internal Server Error");
            }
        });
        startServer();

        try {
            // Тестируем текстовый файл
            HttpURLConnection textConn = (HttpURLConnection)
                    new URL("http://" + TEST_HOST + ":" + TEST_PORT + "/text-file").openConnection();
            assertEquals(200, textConn.getResponseCode());
            assertEquals("text/plain", textConn.getContentType());
            assertEquals(new String(Files.readAllBytes(textFile.toPath())),
                    new String(textConn.getInputStream().readAllBytes()));

            // Тестируем бинарный файл
            HttpURLConnection imageConn = (HttpURLConnection)
                    new URL("http://" + TEST_HOST + ":" + TEST_PORT + "/image").openConnection();
            assertEquals(200, imageConn.getResponseCode());
            assertEquals("image/png", imageConn.getContentType());
            assertArrayEquals(Files.readAllBytes(imageFile.toPath()),
                    imageConn.getInputStream().readAllBytes());
        } finally {
            textFile.delete();
            imageFile.delete();
        }
    }

    @Test
    public void testLargeBinaryFile() throws Exception {
        File largeFile = File.createTempFile("large", ".bin");
        byte[] largeData = new byte[1024 * 1024];
        new java.util.Random().nextBytes(largeData);
        Files.write(largeFile.toPath(), largeData);

        server.addRoute("GET", "/large", (req, res) -> {
            try {
                res.setBody(largeFile, "application/octet-stream");
                res.setStatus(200, "OK");
            } catch (IOException e) {
                res.setStatus(500, "Internal Server Error");
            }
        });
        startServer();

        try {
            URL url = new URL("http://" + TEST_HOST + ":" + TEST_PORT + "/large");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            assertEquals(200, conn.getResponseCode());
            assertEquals(String.valueOf(largeFile.length()), conn.getHeaderField("Content-Length"));

            try (InputStream is = conn.getInputStream()) {
                byte[] responseData = is.readAllBytes();
                assertArrayEquals(largeData, responseData);
            }
        } finally {
            largeFile.delete();
        }
    }

    @Test
    public void testJsonRequestHandling() throws Exception {
        server.addRoute("POST", "/json", (req, res) -> {
            Map<String, Object> jsonData = req.parseJson();
            if (!jsonData.isEmpty()) {
                res.setStatus(200, "OK");
                res.setBody("Received JSON with " + jsonData.size() + " fields");
                res.addHeader("Content-Type", "application/json");
            } else {
                res.setStatus(400, "Bad Request");
                res.setBody("Invalid JSON");
            }
        });
        startServer();

        // Отправляем JSON
        URL url = new URL("http://" + TEST_HOST + ":" + TEST_PORT + "/json");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String jsonInput = "{\"name\":\"Mark\", \"age\":22}";
        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonInput.getBytes(StandardCharsets.UTF_8));
        }

        assertEquals(200, conn.getResponseCode());
        assertEquals("application/json", conn.getContentType());

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            assertEquals("Received JSON with 2 fields", reader.readLine());
        }
    }

    @Test
    public void testDuplicateRouteHandlers() throws InterruptedException {
        HttpServer.RequestHandler handler1 = (req, res) -> res.setBody("Handler 1");
        HttpServer.RequestHandler handler2 = (req, res) -> res.setBody("Handler 2");

        server.addRoute("GET", "/duplicate", handler1);

        // Попытка добавить второй обработчик для того же маршрута
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> server.addRoute("GET", "/duplicate", handler2));

        assertEquals("Route already exists: GET /duplicate", exception.getMessage());

        // Проверяем, что первый обработчик остался неизменным
        startServer();

        try {
            URL url = new URL("http://" + TEST_HOST + ":" + TEST_PORT + "/duplicate");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                assertEquals("Handler 1", reader.readLine());
            }
        } catch (Exception e) {
            fail("Test failed: " + e.getMessage());
        }
    }


    @Test
    public void testJsonCrudOperations() throws Exception {
        // Setup routes
        server.addRoute("POST", "/data", (req, res) -> {
            Map<String, Object> jsonData = req.parseJson();
            String id = UUID.randomUUID().toString();
            server.getDataStore().put(id, jsonData);
            res.setStatus(200, "OK");
            res.setBody("{\"id\":\"" + id + "\"}");
            res.addHeader("Content-Type", "application/json");
        });

        server.addRoute("GET", "/data/{id}", (req, res) -> {
            String id = req.headers().get("X-Resource-ID");
            Map<String, Object> data = server.getDataStore().get(id);
            if (data != null) {
                res.setStatus(200, "OK");
                res.setBody(data.toString());
            } else {
                res.setStatus(404, "Not Found");
                res.setBody("No data found");
            }
            res.addHeader("Content-Type", "application/json");
        });

        server.addRoute("DELETE", "/data/{id}", (req, res) -> {
            String id = req.headers().get("X-Resource-ID");
            server.getDataStore().remove(id);
            res.setStatus(200, "OK");
            res.setBody("{\"status\":\"deleted\"}");
            res.addHeader("Content-Type", "application/json");
        });

        startServer();

        // Test POST
        String testJson = "{\"name\":\"test\",\"value\":123}";
        URL postUrl = new URL("http://" + TEST_HOST + ":" + TEST_PORT + "/data");
        HttpURLConnection postConn = (HttpURLConnection) postUrl.openConnection();
        postConn.setRequestMethod("POST");
        postConn.setRequestProperty("Content-Type", "application/json");
        postConn.setDoOutput(true);

        try (OutputStream os = postConn.getOutputStream()) {
            os.write(testJson.getBytes(StandardCharsets.UTF_8));
        }

        String id;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(postConn.getInputStream(), StandardCharsets.UTF_8))) {
            String response = reader.readLine();
            id = response.split("\"")[3]; // Extract ID from {"id":"..."}
        }

        // Test GET after POST
        URL getUrl = new URL("http://" + TEST_HOST + ":" + TEST_PORT + "/data/" + id);
        HttpURLConnection getConn = (HttpURLConnection) getUrl.openConnection();
        getConn.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getConn.getInputStream(), StandardCharsets.UTF_8))) {
            String response = reader.readLine();
            assertTrue(response.contains("name=test"));
            assertTrue(response.contains("value=123"));
        }

        // DELETE
        URL deleteUrl = new URL("http://" + TEST_HOST + ":" + TEST_PORT + "/data/" + id);
        HttpURLConnection deleteConn = (HttpURLConnection) deleteUrl.openConnection();
        deleteConn.setRequestMethod("DELETE");
        assertEquals(200, deleteConn.getResponseCode());

        // Test GET after DELETE
        HttpURLConnection getAfterDeleteConn = (HttpURLConnection) getUrl.openConnection();
        getAfterDeleteConn.setRequestMethod("GET");
        assertEquals(404, getAfterDeleteConn.getResponseCode());
    }
}