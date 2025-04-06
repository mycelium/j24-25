package ru.spbstu.telematics.httpserver;

import java.util.HashMap;
import java.util.Map;

import ru.spbstu.telematics.json.jsoninteraction.*;

public class HttpRequest {
    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private final String body;

    public HttpRequest(String method, String path, String version, Map<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest parse(String rawRequest) {
        String[] parts = rawRequest.split("\r\n\r\n", 2);
        String headerPart = parts[0];
        String body = parts.length > 1 ? parts[1] : "";

        String[] lines = headerPart.split("\r\n");
        String requestLine = lines[0];
        String[] requestLineParts = requestLine.split(" ");
        String method = requestLineParts[0];
        String path = requestLineParts[1];
        String version = requestLineParts[2];

        Map<String, String> headers = new HashMap<>();
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            int colonIndex = line.indexOf(":");
            if (colonIndex != -1) {
                String key = line.substring(0, colonIndex).trim();
                String value = line.substring(colonIndex + 1).trim();
                headers.put(key, value);
            }
        }

        return new HttpRequest(method, path, version, headers, body);
    }

    public Map<String, Object> parseJson() {
        if (!getContentType().contains("application/json")) {
            return new HashMap<>();
        }

        try {
            return JsonReader.fromJsonToMap(body);
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public String getHeader(String name) {
        return headers.getOrDefault(name, "");
    }

    public Map<String, String> getAllHeaders() {
        return headers;
    }

    public int getContentLength() {
        try {
            return Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public String getContentType() {
        return headers.getOrDefault("Content-Type", "text/plain");
    }

}
