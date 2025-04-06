package ru.spbstu.hsai.httpserver;

@FunctionalInterface
public interface HttpHandler {
    void handle(HttpRequest request, HttpResponse response);
}
