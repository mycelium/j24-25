package ru.spbstu.telematics.httpserver;

public class Main {
    public static void main(String[] args) {
        Server server = new Server("localhost", 8080, 4, false);
        server.start();
    }
}
