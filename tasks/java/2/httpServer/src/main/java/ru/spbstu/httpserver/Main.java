package ru.spbstu.httpserver;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        HttpServer server = new HttpServer();

        // GET-запросы
        server.addRoute("GET", "/", (request, response) -> {
            response.setStatus(200, "OK");
            response.setBody("<h1>Home Page</h1>");
            response.addHeader("Content-Type", "text/html");
        });

        // POST-запросы с JSON
        server.addRoute("POST", "/data", (request, response) -> {
            Map<String, Object> jsonData = request.parseJson();
            if (!jsonData.isEmpty()) {
                String id = UUID.randomUUID().toString(); // Генерация уникального ID
                server.getDataStore().put(id, jsonData);
                response.setStatus(200, "OK");
                response.setBody("Data stored with ID: " + id);
            } else {
                response.setStatus(400, "Bad Request");
                response.setBody("Invalid or unsupported JSON data");
            }
            response.addHeader("Content-Type", "application/json");
        });

        // GET-запросы для получения данных
        server.addRoute("GET", "/data/{id}", (request, response) -> {
            String id = request.headers().get("X-Resource-ID"); // Извлекаем ID из заголовка
            if (id == null) {
                response.setStatus(400, "Bad Request");
                response.setBody("ID not provided");
                return;
            }

            Map<String, Object> data = server.getDataStore().get(id);
            if (data != null) {
                response.setStatus(200, "OK");
                response.setBody("Data for ID " + id + ": " + data);
            } else {
                response.setStatus(404, "Not Found");
                response.setBody("No data found for ID " + id);
            }
            response.addHeader("Content-Type", "application/json");
        });


        // PUT-запросы для обновления данных
        server.addRoute("PUT", "/data/{id}", (request, response) -> {
            String id = request.headers().get("X-Resource-ID");
            if (id == null) {
                response.setStatus(400, "Bad Request");
                response.setBody("ID not provided");
                return;
            }

            Map<String, Object> jsonData = request.parseJson();
            if (!jsonData.isEmpty()) {
                server.getDataStore().put(id, jsonData);
                response.setStatus(200, "OK");
                response.setBody("Data updated for ID: " + id);
            } else {
                response.setStatus(400, "Bad Request");
                response.setBody("Invalid or unsupported JSON data");
            }
            response.addHeader("Content-Type", "application/json");
        });

        // PATCH-запросы для частичного обновления данных
        server.addRoute("PATCH", "/data/{id}", (request, response) -> {
            String id = request.headers().get("X-Resource-ID");
            if (id == null) {
                response.setStatus(400, "Bad Request");
                response.setBody("ID not provided");
                return;
            }

            Map<String, Object> existingData = server.getDataStore().get(id);
            if (existingData == null) {
                response.setStatus(404, "Not Found");
                response.setBody("No data found for ID " + id);
                return;
            }

            Map<String, Object> jsonData = request.parseJson();
            if (!jsonData.isEmpty()) {
                existingData.putAll(jsonData); // Обновляем только переданные поля
                server.getDataStore().put(id, existingData);
                response.setStatus(200, "OK");
                response.setBody("Data partially updated for ID: " + id);
            } else {
                response.setStatus(400, "Bad Request");
                response.setBody("Invalid or unsupported JSON data");
            }
            response.addHeader("Content-Type", "application/json");
        });

        // DELETE-запросы для удаления данных
        server.addRoute("DELETE", "/data/{id}", (request, response) -> {
            String id = request.headers().get("X-Resource-ID");
            if (id == null) {
                response.setStatus(400, "Bad Request");
                response.setBody("ID not provided");
                return;
            }

            Map<String, Object> removedData = server.getDataStore().remove(id);
            if (removedData != null) {
                response.setStatus(200, "OK");
                response.setBody("Data deleted for ID: " + id);
            } else {
                response.setStatus(404, "Not Found");
                response.setBody("No data found for ID " + id);
            }
            response.addHeader("Content-Type", "application/json");
        });



        // Запуск сервера
        try {
            server.start("localhost", 8080, 4, true); // Используем виртуальные потоки
            System.out.println("Server is running on http://localhost:8080");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}