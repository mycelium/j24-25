package ru.spbstu.httpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import ru.spbstu.jsonparser.JsonParser;

public record HttpRequest(
        String method,
        String path,
        String protocol,
        Map<String, String> headers,
        String body
) {
    public static HttpRequest parse(BufferedReader reader) throws IOException {
        String startLine = reader.readLine();
        if (startLine == null) {
            throw new IOException("Empty request");
        }

        String[] startLineParts = startLine.split(" ", 3);
        String method = startLineParts[0];
        String path = startLineParts[1];
        String protocol = startLineParts[2];

        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] header = line.split(": ", 2);
            if (header.length == 2) {
                headers.put(header[0], header[1]);
            }
        }

        StringBuilder body = new StringBuilder();
        while (reader.ready()) {
            body.append((char) reader.read());
        }

        return new HttpRequest(method, path, protocol, new HashMap<>(headers), body.toString());
    }

    public Map<String, Object> parseJson() {
        if (!"application/json".equals(headers.get("Content-Type"))) {
            return Collections.emptyMap();
        }

        return JsonParser.fromJsonToMap(body.substring(1, body.length()-1));
    }

    public Map<String, String> parseFormData() {
        if (!"application/x-www-form-urlencoded".equals(headers.get("Content-Type"))) {
            return Collections.emptyMap();
        }
        Map<String, String> formData = new HashMap<>();
        for (String pair : body.split("&")) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                formData.put(keyValue[0], keyValue[1]);
            }
        }
        return formData;
    }
}