package com.server;

/**
 * Functional interface representing an HTTP handler.
 */
@FunctionalInterface
public interface HttpHandler {
    void handle(HttpRequest request, HttpResponse response);
}
