package ru.spbstu.telematics.httpserver;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents an HTTP response. It allows setting the status code, status message, headers, and body of the response.
 * This class can generate a complete HTTP response as a byte array to be sent back to the client.
 * The body of the response can be set in multiple formats: as a string, a byte array, or a file.
 */
public class HttpResponse {
    private int status;
    private String statusMessage;
    private final Map<String, String> headers = new HashMap<>();
    private byte[] bodyBytes; // Храним тело ответа как массив байт

    /**
     * Initializes a new {@link HttpResponse} object with default headers.
     * The "Server" header is set to "Java HTTP Server" by default.
     */
    public HttpResponse() {
        headers.put("Server", "Java HTTP Server");
    }

    /**
     * Sets the status code and its corresponding status message for the HTTP response.
     *
     * @param status The HTTP status code (e.g., 200 for OK, 404 for Not Found).
     */
    public void setStatus(int status) {
        this.status = status;
        switch (status) {
            case 200:
                statusMessage = "OK";
                break;
            case 400:
                statusMessage = "Bad Request";
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

    /**
     * Sets a custom header for the HTTP response.
     *
     * @param key The name of the header.
     * @param value The value of the header.
     */
    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    /**
     * Sets the body of the response as a string. The string is automatically converted to bytes.
     * The "Content-Length" header is set based on the length of the string.
     *
     * @param body The string body of the response.
     */
    public void setBody(String body) {
        if (body != null) {
            this.bodyBytes = body.getBytes(StandardCharsets.UTF_8);
        } else {
            this.bodyBytes = new byte[0];
        }
        setHeader("Content-Length", String.valueOf(bodyBytes.length));
    }


    /**
     * Sets the body of the response as a byte array. The "Content-Length" header is set based on the length of the byte array.
     *
     * @param body The byte array body of the response.
     */
    public void setBody(byte[] body) {
        if (body != null) {
            this.bodyBytes = body;
        } else {
            this.bodyBytes = new byte[0];
        }
        setHeader("Content-Length", String.valueOf(bodyBytes.length));
    }

    /**
     * Sets the body of the response from a file. The file content is read as a byte array,
     * and the "Content-Type" and "Content-Length" headers are set accordingly.
     *
     * @param file The file to be used as the body of the response.
     * @param contentType The content type of the file (e.g., "text/html", "application/json").
     */
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

    /**
     * Converts the HTTP response to a byte array that can be sent over the network.
     * This byte array includes both the response headers and the body.
     *
     * @return A byte array representing the entire HTTP response (headers + body).
     */
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
