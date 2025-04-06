package ru.spbstu.hsai.httpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final String method;
    private final String path;
    private final String protocol;
    private final Map<String, String> headers = new HashMap<>();
    private final String body;

    private HttpRequest(String method, String path, String protocol, Map<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.protocol = protocol;
        this.headers.putAll(headers);
        this.body = body;
    }

    public static HttpRequest parse(BufferedReader request) throws IOException {
        String data = request.readLine();
        if (data == null || data.isEmpty()) {
            throw new IOException("Empty request");
        }

        String[] dataParts = data.split(" ");
        if (dataParts.length != 3) {
            throw new IOException("Invalid request line");
        }

        String method = dataParts[0];
        String path = dataParts[1];
        String protocol = dataParts[2];

        Map<String, String> headers = new HashMap<>();
        String headerLine = request.readLine();
        while (headerLine != null && !headerLine.isEmpty()) {
            int colonInd = headerLine.indexOf(':');
            if (colonInd > 0) {
                String key = headerLine.substring(0, colonInd).trim();
                String value = headerLine.substring(colonInd + 1).trim();
                headers.put(key, value);
            }
            headerLine = request.readLine();
        }

        String body = null;
        if (headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            char[] bodyChars = new char[contentLength];
            request.read(bodyChars, 0, contentLength);
            body = new String(bodyChars);
        }

        return new HttpRequest(method, path, protocol, headers, body);
    }

    public String getMethod() { return method; }
    public String getPath() { return path; }
    public String getProtocol() { return protocol; }
    public Map<String, String> getHeaders() { return headers; }
    public String getBody() { return body; }
}
