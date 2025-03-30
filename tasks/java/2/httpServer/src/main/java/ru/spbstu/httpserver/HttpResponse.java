package ru.spbstu.httpserver;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private String status = "200 OK";
    private final Map<String, String> headers = new HashMap<>();
    private byte[] body;
    private File fileBody;
    private boolean isFile;

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

    public void setBody(String body) {
        this.body = body.getBytes(StandardCharsets.UTF_8);
        this.isFile = false;
        headers.put("Content-Length", String.valueOf(this.body.length));
    }

    public void setBody(byte[] body) {
        this.body = body;
        this.isFile = false;
        headers.put("Content-Length", String.valueOf(body.length));
    }

    public void setBody(File file, String contentType) throws IOException {
        this.fileBody = file;
        this.isFile = true;
        headers.put("Content-Type", contentType);
        headers.put("Content-Length", String.valueOf(file.length()));
    }

    public boolean isFile() {
        return isFile;
    }

    public File getFileBody() {
        return fileBody;
    }

    public byte[] getBody() {
        return body;
    }
}