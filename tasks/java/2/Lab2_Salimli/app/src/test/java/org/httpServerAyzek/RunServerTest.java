package org.httpServerAyzek;

import static org.junit.jupiter.api.Assertions.*;

import org.httpServerAyzek.http.handler.*;
import org.httpServerAyzek.http.util.*;
import org.httpServerAyzek.http.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class RunServerTest {
    private static final String HOST = "localhost";
    private static final int PORT = 8081;
    private static HttpServer server;
    private static Thread serverThread;

    @BeforeAll
    public static void setUp() throws Exception {
        server = new HttpServer(HOST, PORT, 4, false);
        server.addListener("/test1", "GET", new HttpHandler() {
            @Override
            public void handle(HttpReqParser request, HttpRes response) {
                response.setBody("GET: Тест пройден");
            }
        });
        server.addListener("/test2", "POST", new HttpHandler() {
            @Override
            public void handle(HttpReqParser request, HttpRes response) {
                String body = request.getBody();
                response.setBody("POST: Тест пройден. Полученное тело: " + (body != null ? body : "пусто"));
            }
        });
        server.addListener("/test3", "PUT", new HttpHandler() {
            @Override
            public void handle(HttpReqParser request, HttpRes response) {
                String body = request.getBody();
                response.setBody("PUT: Тест пройден. Полученное тело: " + (body != null ? body : "пусто"));
            }
        });
        server.addListener("/test4", "PATCH", new HttpHandler() {
            @Override
            public void handle(HttpReqParser request, HttpRes response) {
                String body = request.getBody();
                response.setBody("PATCH: Тест пройден. Полученное тело: " + (body != null ? body : "пусто"));
            }
        });
        server.addListener("/test5", "DELETE", new HttpHandler() {
            @Override
            public void handle(HttpReqParser request, HttpRes response) {
                response.setBody("DELETE: Тест пройден");
            }
        });
        serverThread = new Thread(() -> {
            try {
                server.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();
        TimeUnit.MILLISECONDS.sleep(500);
    }

    @AfterAll
    public static void tearDown() {
        // Сервер работает в демон‑потоке, поэтому при завершении тестов JVM завершится.
    }

    private String sendRequest(String requestText) throws IOException {
        try (Socket socket = new Socket(HOST, PORT)) {
            socket.setSoTimeout(2000);
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();
            out.write(requestText.getBytes(StandardCharsets.UTF_8));
            out.flush();

            ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                responseBuffer.write(buffer, 0, bytesRead);
                if (bytesRead < buffer.length) { // краткая оптимизация – если ответ закончился
                    break;
                }
            }
            return responseBuffer.toString(StandardCharsets.UTF_8.name());
        }
    }

    @Test
    public void testGET() throws IOException {
        String request = "GET /test1 HTTP/1.1\r\n" +
                "Host: " + HOST + "\r\n" +
                "\r\n";
        String response = sendRequest(request);
        assertTrue(response.contains("GET: Тест пройден"),
                "Ожидается, что ответ содержит 'GET: Тест пройден', а получен: " + response);
    }

    @Test
    public void testPOST() throws IOException {
        String body = "hello";
        String request = "POST /test2 HTTP/1.1\r\n" +
                "Host: " + HOST + "\r\n" +
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                "\r\n" +
                body;
        String response = sendRequest(request);
        assertTrue(response.contains("POST: Тест пройден. Полученное тело: hello"),
                "Ожидается корректный ответ для POST, а получен: " + response);
    }

    @Test
    public void testPUT() throws IOException {
        String body = "update";
        String request = "PUT /test3 HTTP/1.1\r\n" +
                "Host: " + HOST + "\r\n" +
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                "\r\n" +
                body;
        String response = sendRequest(request);
        assertTrue(response.contains("PUT: Тест пройден. Полученное тело: update"),
                "Ожидается корректный ответ для PUT, а получен: " + response);
    }

    @Test
    public void testPATCH() throws IOException {
        String body = "partial";
        String request = "PATCH /test4 HTTP/1.1\r\n" +
                "Host: " + HOST + "\r\n" +
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                "\r\n" +
                body;
        String response = sendRequest(request);
        assertTrue(response.contains("PATCH: Тест пройден. Полученное тело: partial"),
                "Ожидается корректный ответ для PATCH, а получен: " + response);
    }

    @Test
    public void testDELETE() throws IOException {
        String request = "DELETE /test5 HTTP/1.1\r\n" +
                "Host: " + HOST + "\r\n" +
                "\r\n";
        String response = sendRequest(request);
        assertTrue(response.contains("DELETE: Тест пройден"),
                "Ожидается корректный ответ для DELETE, а получен: " + response);
    }

    @Test
    public void testNotFound() throws IOException {
        String request = "GET /nonexistent HTTP/1.1\r\n" +
                "Host: " + HOST + "\r\n" +
                "\r\n";
        String response = sendRequest(request);
        assertTrue(response.contains("404") || response.contains("Not Found"),
                "Ожидается ответ 404 Not Found, а получен: " + response);
    }

    @Test
    public void testBadRequest() throws IOException {
        String request = "\r\n";
        String response = sendRequest(request);
        assertTrue(response.contains("400") || response.contains("Bad Request"),
                "Ожидается ответ 400 Bad Request, а получен: " + response);
    }
}
