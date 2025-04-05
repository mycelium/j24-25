package ru.spbstu.telematics.httpserver;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private int status;
    private String statusMessage;
    private final Map<String, String> headers = new HashMap<>();
    private byte[] bodyBytes; // Храним тело ответа как массив байт

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
            case 500:
                statusMessage = "Internal Server Error";
                break;
            default:
                statusMessage = "";
        }
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    // Устанавливает тело как строку (преобразуя в байты)
    public void setBody(String body) {
        if (body != null) {
            this.bodyBytes = body.getBytes(StandardCharsets.UTF_8);
        } else {
            this.bodyBytes = new byte[0];
        }
        setHeader("Content-Length", String.valueOf(bodyBytes.length));
    }

    // Устанавливает тело как массив байт
    public void setBody(byte[] body) {
        if (body != null) {
            this.bodyBytes = body;
        } else {
            this.bodyBytes = new byte[0];
        }
        setHeader("Content-Length", String.valueOf(bodyBytes.length));
    }

    // Устанавливает тело как содержимое файла с указанным Content-Type
    public void setBody(File file, String contentType) {
        try {
            this.bodyBytes = Files.readAllBytes(file.toPath());
            setHeader("Content-Type", contentType);
            setHeader("Content-Length", String.valueOf(bodyBytes.length));
        } catch (IOException e) {
            this.bodyBytes = ("Error reading file: " + e.getMessage()).getBytes(StandardCharsets.UTF_8);
            setStatus(500);
            setHeader("Content-Length", String.valueOf(bodyBytes.length));
        }
    }

    public byte[] toBytes() {
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("HTTP/1.1 ").append(status).append(" ").append(statusMessage).append("\r\n");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            responseBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }
        responseBuilder.append("\r\n");
        byte[] headerBytes = responseBuilder.toString().getBytes(StandardCharsets.UTF_8);
        if (bodyBytes == null) {
            bodyBytes = new byte[0];
        }
        byte[] fullResponse = new byte[headerBytes.length + bodyBytes.length];
        System.arraycopy(headerBytes, 0, fullResponse, 0, headerBytes.length);
        System.arraycopy(bodyBytes, 0, fullResponse, headerBytes.length, bodyBytes.length);
        return fullResponse;
    }
}
