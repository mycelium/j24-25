package org.httpServerAyzek.http;

import java.io.*;
import java.util.*;

public class HttpRes {
    private int statusCode;
    private String reasonPhrase;
    private Map<String, String> headers;
    private String body;

    public HttpRes() {
        this.statusCode = 200;
        this.headers = new HashMap<>();
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public void setReasonPhrase(String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }

    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return this.body;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public void send(OutputStream output) throws IOException {
        String statusLine = "HTTP/1.1 " + statusCode + " " + reasonPhrase + "\r\n";
        byte[] bodyBytes = (body != null ? body.getBytes() : new byte[0]);
        if (body != null && !headers.containsKey("Content-Length")) {
            headers.put("Content-Length", String.valueOf(bodyBytes.length));
        }
        StringBuilder headerBuilder = new StringBuilder();
        headerBuilder.append(statusLine);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            headerBuilder.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append("\r\n");
        }
        headerBuilder.append("\r\n");
        output.write(headerBuilder.toString().getBytes());
        output.write(bodyBytes);
        output.flush();
    }
}
