package ru.spbstu.httpserver;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        HttpServer server = new HttpServer();

        // Добавляем маршруты
        server.addRoute("GET", "/", (request, response) -> {
            response.setStatus(200, "OK");
            response.setBody("<h1>Home Page</h1>");
            response.addHeader("Content-Type", "text/html");
        });

        server.addRoute("GET", "/hello", (request, response) -> {
            response.setStatus(200, "OK");
            response.setBody("<h1>Hello there!</h1>");
            response.addHeader("Content-Type", "text/html");
        });

        server.addRoute("GET", "/data", (request, response) -> {
            response.setStatus(200, "OK");
            response.setBody("{\"message\": \"JSON data\"}");
            response.addHeader("Content-Type", "application/json");
        });

        // Запуск сервера
        try {
            server.start("localhost", 8080, 4, true);
            System.out.println("Server is running on http://localhost:8080");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}