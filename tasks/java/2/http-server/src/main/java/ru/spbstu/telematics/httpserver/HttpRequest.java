package ru.spbstu.telematics.httpserver;

public class HttpRequest {
    private final String rawRequest;

    public HttpRequest(String rawRequest) {
        this.rawRequest = rawRequest;
    }

    public String getRawRequest() {
        return rawRequest;
    }
}
