package spbstu.test;

import spbstu.lab.HttpServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public class HttpServerTests {
    private static HttpServer server;
    private static final String TEST_HOST = "127.0.0.1";
    private static final int TEST_PORT = 8082;

    public static void main(String[] args) throws Exception {
        setupServer();
        Thread.sleep(100);
        testGetRequest();
        testPostRequest();
        testPutRequest();
        testPatchRequest();
        testDeleteRequest();
        testNotFound();
        testMultiHandlers();
        System.out.println("Все тесты успешно пройдены.");
    }


    private static void setupServer() throws IOException {
        server = new HttpServer(TEST_HOST, TEST_PORT, 4, false);

        server.addHandler("GET", "/hello", (HttpServer.HttpRequest request) -> {
            return new HttpServer.HttpResponse()
                    .setStatusCode(200)
                    .setBody("Hello World");
        });

        server.addHandler("POST", "/submit", request -> {
            String requestBody = request.bodyAsString();
            HttpServer.HttpResponse response = new HttpServer.HttpResponse()
                    .setStatusCode(200)
                    .setBody("You posted: " + requestBody);
            return response;
        });

        server.addHandler("PUT", "/item", request -> {
            String requestBody = request.bodyAsString();
            return new HttpServer.HttpResponse()
                    .setStatusCode(200)
                    .setBody("PUT body: " + requestBody);
        });

        server.addHandler("PATCH", "/user", request -> {
            String requestBody = request.bodyAsString();
            return new HttpServer.HttpResponse()
                    .setStatusCode(200)
                    .setBody("PATCH data: " + requestBody);
        });

        server.addHandler("DELETE", "/deleteMe", request -> {
            return new HttpServer.HttpResponse()
                    .setStatusCode(200)
                    .setBody("Deleted!");
        });

        server.addHandler("GET", "/headers", (HttpServer.HttpRequest request) -> {
            String agent = request.headers().getOrDefault("User-Agent", "Unknown-UA");
            return new HttpServer.HttpResponse()
                    .setStatusCode(200)
                    .setHeader("X-Server-Reply", "CustomHeaderValue")
                    .setBody("We got User-Agent: " + agent);
        });

        server.addHandler("GET", "/path1", request -> {
            return new HttpServer.HttpResponse().setBody("Path1 response");
        });

        server.addHandler("POST", "/path2", request -> {
            return new HttpServer.HttpResponse().setBody("Path2 POST response");
        });

        server.addHandler("PUT", "/path3", request -> {
            return new HttpServer.HttpResponse().setBody("Path3 PUT response");
        });

        server.start();

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
        }
    }

    /* Проверка GET-запроса.
     */
    private static void testGetRequest() throws Exception {
        String response = sendRawHttpRequest(
                "GET /hello HTTP/1.1\r\n" +
                        "Host: " + TEST_HOST + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n");

        if (!response.contains("HTTP/1.1 200 OK")) {
            throw new RuntimeException("Ожидался код 200 в ответ на GET /hello. Ответ:\n" + response);
        }
        if (!response.contains("Hello World")) {
            throw new RuntimeException("Ожидалось тело 'Hello World'. Ответ:\n" + response);
        }

        System.out.println("testGetRequest() - OK");
    }

    /**
     * Checking the POST request.
     */
    private static void testPostRequest() throws Exception {
        String bodyToSend = "Test POST body";
        String request =
                "POST /submit HTTP/1.1\r\n" +
                        "Host: " + TEST_HOST + "\r\n" +
                        "Content-Length: " + bodyToSend.getBytes().length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n" +
                        bodyToSend;

        String response = sendRawHttpRequest(request);

        if (!response.contains("HTTP/1.1 200 OK")) {
            throw new RuntimeException("Ожидался код 200 в ответ на POST /submit. Ответ:\n" + response);
        }
        if (!response.contains("You posted: " + bodyToSend)) {
            throw new RuntimeException("Ожидалось эхо-тело 'You posted: ...'. Ответ:\n" + response);
        }

        System.out.println("testPostRequest() - OK");
    }

    /**
     * Checking the PUT request.
     */
    private static void testPutRequest() throws Exception {
        String bodyToSend = "Put body data";
        String request =
                "PUT /item HTTP/1.1\r\n" +
                        "Host: " + TEST_HOST + "\r\n" +
                        "Content-Length: " + bodyToSend.getBytes().length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n" +
                        bodyToSend;

        String response = sendRawHttpRequest(request);

        if (!response.contains("HTTP/1.1 200 OK")) {
            throw new RuntimeException("Ожидался код 200 в ответ на PUT /item. Ответ:\n" + response);
        }
        if (!response.contains("PUT body: " + bodyToSend)) {
            throw new RuntimeException("Ожидалось 'PUT body: ...'. Ответ:\n" + response);
        }

        System.out.println("testPutRequest() - OK");
    }

    /**
     * Checking the PATCH request.
     */
    private static void testPatchRequest() throws Exception {
        String bodyToSend = "Patch data changes";
        String request =
                "PATCH /user HTTP/1.1\r\n" +
                        "Host: " + TEST_HOST + "\r\n" +
                        "Content-Length: " + bodyToSend.getBytes().length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n" +
                        bodyToSend;

        String response = sendRawHttpRequest(request);

        if (!response.contains("HTTP/1.1 200 OK")) {
            throw new RuntimeException("Ожидался код 200 в ответ на PATCH /user. Ответ:\n" + response);
        }
        if (!response.contains("PATCH data: " + bodyToSend)) {
            throw new RuntimeException("Ожидалось 'PATCH data: ...'. Ответ:\n" + response);
        }

        System.out.println("testPatchRequest() - OK");
    }

    /**
     * Checking the DELETE request.
     */
    private static void testDeleteRequest() throws Exception {
        String request =
                "DELETE /deleteMe HTTP/1.1\r\n" +
                        "Host: " + TEST_HOST + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n";

        String response = sendRawHttpRequest(request);

        if (!response.contains("HTTP/1.1 200 OK")) {
            throw new RuntimeException("Ожидался код 200 в ответ на DELETE /deleteMe. Ответ:\n" + response);
        }
        if (!response.contains("Deleted!")) {
            throw new RuntimeException("Ожидалось 'Deleted!'. Ответ:\n" + response);
        }

        System.out.println("testDeleteRequest() - OK");
    }

    /**
     * 404 Not Found check (if the route is not secured).
     */
    private static void testNotFound() throws Exception {
        String request =
                "GET /unknown HTTP/1.1\r\n" +
                        "Host: " + TEST_HOST + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n";

        String response = sendRawHttpRequest(request);

        if (!response.contains("HTTP/1.1 404 Not Found")) {
            throw new RuntimeException("Ожидался код 404. Ответ:\n" + response);
        }

        System.out.println("testNotFound() - OK");
    }

    /**
     * Demonstration of several different handlers on the same server.
     */
    private static void testMultiHandlers() throws Exception {
        String resp1 = sendRawHttpRequest(
                "GET /path1 HTTP/1.1\r\n" +
                        "Host: " + TEST_HOST + "\r\n" +
                        "Connection: close\r\n\r\n"
        );
        if (!resp1.contains("Path1 response")) {
            throw new RuntimeException("Ожидалась строка 'Path1 response'. Ответ:\n" + resp1);
        }

        String resp2 = sendRawHttpRequest(
                "POST /path2 HTTP/1.1\r\n" +
                        "Host: " + TEST_HOST + "\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n\r\n"
        );
        if (!resp2.contains("Path2 POST response")) {
            throw new RuntimeException("Ожидалась строка 'Path2 POST response'. Ответ:\n" + resp2);
        }

        String resp3 = sendRawHttpRequest(
                "PUT /path3 HTTP/1.1\r\n" +
                        "Host: " + TEST_HOST + "\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n\r\n"
        );
        if (!resp3.contains("Path3 PUT response")) {
            throw new RuntimeException("Ожидалась строка 'Path3 PUT response'. Ответ:\n" + resp3);
        }

        System.out.println("testMultiHandlers() - OK");
    }

    /**
     * A utility method for sending a "raw" HTTP request to the server and receiving a response in the form of a string.
     */
    private static String sendRawHttpRequest(String rawRequest) throws IOException {
        try (Socket socket = new Socket(TEST_HOST, TEST_PORT)) {
            OutputStream out = socket.getOutputStream();
            out.write(rawRequest.getBytes(StandardCharsets.UTF_8));
            out.flush();

            InputStream in = socket.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int read;
            while ((read = in.read(buff)) != -1) {
                buffer.write(buff, 0, read);
            }
            return buffer.toString(StandardCharsets.UTF_8);
        }
    }
}