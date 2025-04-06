package ru.spbstu.hsai.httpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.net.URLDecoder;

public class HttpRequest {
    private final String method;
    private final String path;
    private final String protocol;
    private final Map<String, String> headers = new HashMap<>();
    private final String body;
    private final Map<String, String> queryParams;
    private Map<String, String> pathParams = new HashMap<>(); // <-- добавим


    public HttpRequest(String method, String path, String protocol,
                       Map<String, String> headers, String body) {
        this.method = method;
        this.protocol = protocol;
        this.headers.putAll(headers);
        this.body = body;

        int idx = path.indexOf('?');
        if (idx >= 0) {
            this.path = path.substring(0, idx);
            this.queryParams = parseQueryParams(path.substring(idx + 1));
        } else {
            this.path = path;
            this.queryParams = Collections.emptyMap();
        }
    }

    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> map = new HashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] onePart = pair.split("=");
            if (onePart.length == 2) {
                map.put(URLDecoder.decode(onePart[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(onePart[1], StandardCharsets.UTF_8));
            }
        }
        return map;
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

    public String getQueryParam(String name) {
        return queryParams.get(name);
    }

    public String getPathParam(String name) {
        return pathParams.get(name);
    }

    public void setPathParams(Map<String, String> pathParams) {
        this.pathParams = pathParams;
    }
}
