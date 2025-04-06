package ru.spbstu.telematics.httpserver;

/**
 * This functional interface is responsible for handling HTTP requests and generating appropriate HTTP responses.
 * It provides a single method that takes an {@link HttpRequest} and returns an {@link HttpResponse}.
 * The {@code handle} method can be implemented to process the request and return a response,
 * including setting the status, headers, and body of the HTTP response.
 */
public interface HttpHandler {
    /**
     * Processes the given HTTP request and generates an HTTP response.
     *
     * @param request The HTTP request to be processed.
     * @return An HTTP response based on the request.
     */
    HttpResponse handle(HttpRequest request);
}
