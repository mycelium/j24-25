package ru.spbstu.telematics.httpserver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;

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

}
