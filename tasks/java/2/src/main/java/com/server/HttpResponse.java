package com.server;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Represents an HTTP response.
 */
public class HttpResponse {
    private int statusCode = 200;
    private String reasonPhrase = "OK";
    private final Map<String, String> headers = new HashMap<>();
    private String body = "";

    /**
     * Sets the HTTP status code.
     *
     * @param statusCode the status code
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Sets the HTTP reason phrase.
     *
     * @param reasonPhrase the reason phrase
     */
    public void setReasonPhrase(String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }

    /**
     * Sets an HTTP header.
     *
     * @param key   the header name
     * @param value the header value
     */
    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    /**
     * Sets the response body.
     *
     * @param body the response body
     */
    public void setBody(String body) {
        this.body = body;
        headers.put("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
    }

    /**
     * Converts the HttpResponse to a byte array suitable for sending over a socket.
     *
     * @return the byte array of the HTTP response
     */
    public byte[] toBytes() {
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 ").append(statusCode).append(" ").append(reasonPhrase).append("\r\n");
        headers.putIfAbsent("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
        headers.putIfAbsent("Content-Type", "text/plain; charset=utf-8");
        headers.forEach((k, v) -> response.append(k).append(": ").append(v).append("\r\n"));
        response.append("\r\n");
        response.append(body);
        return response.toString().getBytes(StandardCharsets.UTF_8);
    }
}
