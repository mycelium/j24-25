package com.server;

import java.util.*;

/**
 * Represents an HTTP request.
 */
public class HttpRequest {
    private String method;
    private String path;
    private final Map<String, String> headers = new HashMap<>();
    private String body;

    /**
     * Parses raw HTTP request text into an HttpRequest object.
     *
     * @param requestText the raw HTTP request text
     * @return the HttpRequest object
     */
    public static HttpRequest parse(String requestText) {
        HttpRequest request = new HttpRequest();
        String[] lines = requestText.split("\r\n");
        String[] requestLine = lines[0].split(" ");
        request.method = requestLine[0];
        request.path = requestLine[1];
        int i = 1;
        while (i < lines.length && !lines[i].isEmpty()) {
            String[] header = lines[i].split(": ");
            if (header.length == 2) {
                request.headers.put(header[0], header[1]);
            }
            i++;
        }

        if (request.headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(request.headers.get("Content-Length"));
            StringBuilder bodyBuilder = new StringBuilder();
            for (int j = i + 1; j < lines.length; j++) {
                bodyBuilder.append(lines[j]).append("\r\n");
            }
            request.body = bodyBuilder.toString().trim();
            if (request.body.length() > contentLength) {
                request.body = request.body.substring(0, contentLength);
            }
        }
        return request;
    }

    // Getters
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

    // Setters
    // Only one needed - for testing PATCH requests: cant send them due to Java21 changes
    public void setMethod(String method) {
        this.method = method;
    }
}
