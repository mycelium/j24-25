package ru.spbstu.telematics.httpserver;

public interface HttpHandler {
    HttpResponse handle(HttpRequest request);
}
