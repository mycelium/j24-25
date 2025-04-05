package ru.spbstu.telematics.httpserver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class ServerTest {

    @Test
    void testRequest() {
        String rawRequest =
                "POST /submit HTTP/1.1\r\n" +
                        "Host: localhost\r\n" +
                        "Content-Type: text/plain\r\n" +
                        "Content-Length: 11\r\n" +
                        "\r\n" +
                        "Hello World";

        HttpRequest request = HttpRequest.parse(rawRequest);
        assertEquals("POST", request.getMethod());
        assertEquals("Hello World", request.getBody());
    }

    private static final int TEST_PORT = 8081;
    private static final String TEST_HOST = "localhost";
    private Server server;
    private Thread serverThread;

    @BeforeEach
    public void setUp() {
        server = new Server(TEST_HOST, TEST_PORT, 1, true);
    }

    private void startServer() throws InterruptedException {
        serverThread = new Thread(() -> {
            try {
                server.start();
            } catch (IOException e) {
                fail("Сервер не удалось запустить: " + e.getMessage());
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

    // Тест для GET запроса
    @Test
    public void testGetRequest() throws Exception {
        server.addHandler("GET", "/get", request -> {
            HttpResponse res = new HttpResponse();
            res.setStatus(200);
            res.setBody("GET response");
            res.setHeader("Content-Type", "text/plain");
            return res;
        });
        startServer();

        URL url = new URL("http://" + TEST_HOST + ":" + TEST_PORT + "/get");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        assertEquals(200, conn.getResponseCode());
        assertEquals("text/plain", conn.getContentType());

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            assertEquals("GET response", reader.readLine());
        }
    }

    // Тест для POST запроса
    @Test
    public void testPostRequest() throws Exception {
        server.addHandler("POST", "/post", request -> {
            HttpResponse res = new HttpResponse();
            res.setStatus(200);
            // Возвращаем тело запроса в ответ
            res.setBody("POST received: " + request.getBody());
            res.setHeader("Content-Type", "text/plain");
            return res;
        });
        startServer();

        URL url = new URL("http://" + TEST_HOST + ":" + TEST_PORT + "/post");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        String postBody = "data123";
        conn.setRequestProperty("Content-Type", "text/plain");
        conn.setRequestProperty("Content-Length", String.valueOf(postBody.getBytes(StandardCharsets.UTF_8).length));

        try (OutputStream os = conn.getOutputStream()) {
            os.write(postBody.getBytes(StandardCharsets.UTF_8));
        }

        assertEquals(200, conn.getResponseCode());
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String expected = "POST received: " + postBody;
            assertEquals(expected, reader.readLine());
        }
    }

    // Тест для PUT запроса
    @Test
    public void testPutRequest() throws Exception {
        server.addHandler("PUT", "/put", request -> {
            HttpResponse res = new HttpResponse();
            res.setStatus(200);
            res.setBody("PUT received: " + request.getBody());
            res.setHeader("Content-Type", "text/plain");
            return res;
        });
        startServer();

        URL url = new URL("http://" + TEST_HOST + ":" + TEST_PORT + "/put");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setDoOutput(true);

        String putBody = "putData";
        conn.setRequestProperty("Content-Type", "text/plain");
        conn.setRequestProperty("Content-Length", String.valueOf(putBody.getBytes(StandardCharsets.UTF_8).length));

        try (OutputStream os = conn.getOutputStream()) {
            os.write(putBody.getBytes(StandardCharsets.UTF_8));
        }

        assertEquals(200, conn.getResponseCode());
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String expected = "PUT received: " + putBody;
            assertEquals(expected, reader.readLine());
        }
    }

    // Тест для DELETE запроса
    @Test
    public void testDeleteRequest() throws Exception {
        server.addHandler("DELETE", "/delete", request -> {
            HttpResponse res = new HttpResponse();
            res.setStatus(200);
            res.setBody("DELETE received");
            res.setHeader("Content-Type", "text/plain");
            return res;
        });
        startServer();

        URL url = new URL("http://" + TEST_HOST + ":" + TEST_PORT + "/delete");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("DELETE");

        assertEquals(200, conn.getResponseCode());
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            assertEquals("DELETE received", reader.readLine());
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

        // Добавляем обработчик для GET запроса по пути /binary
        server.addHandler("GET", "/binary", request -> {
            HttpResponse res = new HttpResponse();
            res.setBody(binaryFile, "application/octet-stream");
            res.setStatus(200);
            return res;
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

}
