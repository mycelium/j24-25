package ru.spbstu.telematics.httpserver;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private int status;
    private String statusMessage;
    private final Map<String, String> headers = new HashMap<>();
    private String body;

    public HttpResponse() {
        headers.put("Server", "Java HTTP Server");
    }

    public void setStatus(int status) {
        this.status = status;
        switch (status) {
            case 200:
                statusMessage = "OK";
                break;
            case 404:
                statusMessage = "Not Found";
                break;
            default:
                statusMessage = "";
        }
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    public void setBody(String body) {
        this.body = body;
        setHeader("Content-Length", String.valueOf(body != null ? body.getBytes(StandardCharsets.UTF_8).length : 0));
    }

    public byte[] toBytes() {
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("HTTP/1.1 ").append(status).append(" ").append(statusMessage).append("\r\n");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            responseBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }
        responseBuilder.append("\r\n");
        if (body != null) {
            responseBuilder.append(body);
        }
        return responseBuilder.toString().getBytes(StandardCharsets.UTF_8);
    }
}
