package org.example;


import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer server = new HttpServer("localhost", 8081, 4, false);

        // Register a simple GET /hello route
        server.addHandler("GET", "/hello", req -> {
            String message = "Hello from GET /hello!";
            return new HttpServer.HttpResponse()
                    .setStatusCode(200)
                    .setHeader("Content-Type", "text/plain")
                    .setBody(message);
        });

        // Register a POST /echo route
        server.addHandler("POST", "/echo", req -> {
            // Echo back the request body
            return new HttpServer.HttpResponse()
                    .setStatusCode(200)
                    .setHeader("Content-Type", "text/plain")
                    .setBody(req.bodyAsString());
        });

        // Start server
        server.start();
    }
}
