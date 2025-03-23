package ru.spbstu.httpserver;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HttpServerTest {

    @Test
    public void testGetRequest() throws IOException {
        // Запуск сервера
        HttpServer server = new HttpServer();
        server.addRoute("GET", "/", (request, response) -> {
            response.setStatus(200, "OK");
            response.setBody("<h1>Home Page</h1>");
            response.addHeader("Content-Type", "text/html");
        });

        new Thread(() -> {
            try {
                server.start("localhost", 8080, 1, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // Даем серверу время на запуск
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Отправка GET-запроса
        URL url = new URL("http://localhost:8080/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // Проверка ответа
        assertEquals(200, connection.getResponseCode());
        assertEquals("text/html", connection.getHeaderField("Content-Type"));

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String responseBody = reader.readLine();
        assertEquals("<h1>Home Page</h1>", responseBody);

        // Остановка сервера
        server.stop();
    }


    @Test
    public void testPostRequestWithJson() throws IOException {
        // Запуск сервера
        HttpServer server = new HttpServer();
        server.addRoute("POST", "/data", (request, response) -> {
            Map<String, Object> jsonData = request.parseJson();
            if (!jsonData.isEmpty()) {
                response.setStatus(200, "OK");
                response.setBody("Data stored");
            } else {
                response.setStatus(400, "Bad Request");
                response.setBody("Invalid JSON");
            }
            response.addHeader("Content-Type", "application/json");
        });

        new Thread(() -> {
            try {
                server.start("localhost", 8080, 1, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // Даем серверу время на запуск
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Отправка POST-запроса с JSON
        URL url = new URL("http://localhost:8080/data");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        String jsonInputString = "{\"key\": \"value\"}";
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Проверка ответа
        assertEquals(200, connection.getResponseCode());
        assertEquals("application/json", connection.getHeaderField("Content-Type"));

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String responseBody = reader.readLine();
        assertEquals("Data stored", responseBody);

        // Остановка сервера
        server.stop();
    }


    @Test
    public void testDeleteRequest() throws IOException {
        // Запуск сервера
        HttpServer server = new HttpServer();
        server.addRoute("DELETE", "/data/{id}", (request, response) -> {
            String id = request.headers().get("X-Resource-ID");
            if (id != null) {
                response.setStatus(200, "OK");
                response.setBody("Data deleted for ID: " + id);
            } else {
                response.setStatus(400, "Bad Request");
                response.setBody("ID not provided");
            }
            response.addHeader("Content-Type", "application/json");
        });

        new Thread(() -> {
            try {
                server.start("localhost", 8080, 1, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // Даем серверу время на запуск
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Отправка DELETE-запроса
        URL url = new URL("http://localhost:8080/data/123");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");

        // Проверка ответа
        assertEquals(200, connection.getResponseCode());
        assertEquals("application/json", connection.getHeaderField("Content-Type"));

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String responseBody = reader.readLine();
        assertEquals("Data deleted for ID: 123", responseBody);

        // Остановка сервера
        server.stop();
    }


    @Test
    public void testNotFoundRoute() throws IOException {
        // Запуск сервера
        HttpServer server = new HttpServer();
        server.addRoute("GET", "/", (request, response) -> {
            response.setStatus(200, "OK");
            response.setBody("<h1>Home Page</h1>");
            response.addHeader("Content-Type", "text/html");
        });

        new Thread(() -> {
            try {
                server.start("localhost", 8080, 1, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // Даем серверу время на запуск
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Отправка GET-запроса на несуществующий маршрут
        URL url = new URL("http://localhost:8080/unknown");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // Проверка статуса ответа
        assertEquals(404, connection.getResponseCode());
        assertEquals("text/html", connection.getHeaderField("Content-Type"));

        // Чтение тела ответа из потока ошибок
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        String responseBody = reader.readLine();
        assertEquals("<h1>404 Not Found</h1>", responseBody);

        // Остановка сервера
        server.stop();
    }
}