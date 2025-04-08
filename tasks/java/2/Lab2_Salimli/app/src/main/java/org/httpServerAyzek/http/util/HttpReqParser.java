package org.httpServerAyzek.http.util;

import java.io.*;
import java.util.*;

public class HttpReqParser {
    private String method;
    private String path;
    private String httpVersion;
    private Map<String, String> headers;
    private String body;

    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getHttpVersion() {
        return httpVersion;
    }
    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }
    public Map<String, String> getHeaders() {
        return headers;
    }
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }
    public static HttpReqParser parse(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        HttpReqParser req = new HttpReqParser();

        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IOException("Empty Request");
        }
        String[] requestLineParts = requestLine.split(" ");
        if (requestLineParts.length < 3) {
            throw new IOException("Invalid Request line: " + requestLine);
        }
        req.setMethod(requestLineParts[0]);
        req.setPath(requestLineParts[1]);
        req.setHttpVersion(requestLineParts[2]);

        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            int colonIndex = line.indexOf(":");
            if (colonIndex != -1) {
                String headerName = line.substring(0, colonIndex).trim();
                String headerValue = line.substring(colonIndex + 1).trim();
                headers.put(headerName, headerValue);
            }
        }
        req.setHeaders(headers);

        if (headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length").trim());
            char[] bodyChars = new char[contentLength];
            int read = reader.read(bodyChars);
            if (read > 0) {
                req.setBody(new String(bodyChars, 0, read));
            }
        }
        return req;
    }
}
