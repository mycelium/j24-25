package ru.spbstu.hsai.httpserver;

import ru.spbstu.hsai.httpserver.common.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private HttpStatus status;
    private final Map<String, String> headers = new HashMap<>();
    private byte[] body;

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void setBody(String body) {
        this.body = body.getBytes();
    }
    public void setBody(byte[] body){
        this.body = body;
    }
    public void addHeader(String key, String value) { headers.put(key, value); }

    public byte[] toBytes() {
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 ").append(status.getCode()).append(" ").append(status.getInfo()).append("\r\n");
        if (!headers.containsKey("Content-Length")) {
            headers.put("Content-Length", String.valueOf(body.length));
        }
        headers.forEach((k, v) -> response.append(k).append(": ").append(v).append("\r\n"));
        response.append("\r\n").append(body);
        return response.toString().getBytes(StandardCharsets.UTF_8);
    }

}
