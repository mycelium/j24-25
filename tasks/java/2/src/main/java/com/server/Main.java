package com.server;

/**
 * The entry point of the application for demonstration purposes.
 */
public class Main {
    public static void main(String[] args) {
        HttpServer server = new HttpServer("localhost", 8080);

        server.addHandler("GET", "/", (req, res) -> {
            res.setBody("Welcome to the HTTP Server!");
        });

        server.addHandler("POST", "/echo", (req, res) -> {
            res.setBody("You posted: " + req.getBody());
        });

        new Thread(server::start).start();

        // Add shutdown hook to stop the server gracefully
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
    }
}
