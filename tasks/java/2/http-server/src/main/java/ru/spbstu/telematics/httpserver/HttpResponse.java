package ru.spbstu.telematics.httpserver;

import java.nio.charset.StandardCharsets;

public class HttpResponse {
    private int status;
    private String body;

    public void setStatus(int status) {
        this.status = status;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public byte[] toBytes() {
        String responseText = "HTTP/1.1 " + status + " OK\r\n" +
                "Content-Length: " + (body != null ? body.length() : 0) + "\r\n" +
                "\r\n" +
                (body != null ? body : "");
        return responseText.getBytes(StandardCharsets.UTF_8);
    }
}

