package ru.spbstu.httpserver;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private String status = "200 OK";
    private final Map<String, String> headers = new HashMap<>();
    private String body = "";

    public String getStatus() {
        return status;
    }

    public void setStatus(int code, String message) {
        this.status = code + " " + message;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
        headers.put("Content-Length", String.valueOf(body.length()));
    }
}