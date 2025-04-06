package ru.spbstu.telematics.httpserver;

import java.util.HashMap;
import java.util.Map;

import ru.spbstu.telematics.httpserver.exceptions.JsonParsingException;
import ru.spbstu.telematics.json.jsoninteraction.*;

/**
 * Represents an HTTP request. It contains the HTTP method, path, headers, and body of the request.
 * This class is responsible for parsing raw HTTP requests and extracting relevant details such as method, path,
 * headers, and body.
 */
public class HttpRequest {
    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private final String body;

    public HttpRequest(String method, String path, String version, Map<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
    }

    /**
     * Parses a raw HTTP request string into an {@link HttpRequest} object.
     *
     * @param rawRequest The raw HTTP request string to parse.
     * @return An {@link HttpRequest} object representing the parsed request.
     */
    public static HttpRequest parse(String rawRequest) {
        String[] parts = rawRequest.split("\r\n\r\n", 2);
        String headerPart = parts[0];
        String body = parts.length > 1 ? parts[1] : "";

        String[] lines = headerPart.split("\r\n");
        String requestLine = lines[0];
        String[] requestLineParts = requestLine.split(" ");
        String method = requestLineParts[0];
        String path = requestLineParts[1];
        String version = requestLineParts[2];

        Map<String, String> headers = new HashMap<>();
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            int colonIndex = line.indexOf(":");
            if (colonIndex != -1) {
                String key = line.substring(0, colonIndex).trim();
                String value = line.substring(colonIndex + 1).trim();
                headers.put(key, value);
            }
        }

        return new HttpRequest(method, path, version, headers, body);
    }

    /**
     * Tries to parse the body of the HTTP request as JSON and returns the resulting map of key-value pairs.
     * If the request body is not valid JSON or the content type is not JSON, a {@link JsonParsingException} is thrown.
     *
     * @return A map representing the parsed JSON body of the request.
     * @throws JsonParsingException If the body cannot be parsed as JSON.
     */
    public Map<String, Object> parseJson() throws JsonParsingException {
        if (!getContentType().contains("application/json")) {
            return new HashMap<>();
        }

        try {
            return JsonReader.fromJsonToMap(body);
        } catch (Exception e) {
            throw new JsonParsingException(e);
        }
    }

    /**
     * Gets the HTTP method (e.g., GET, POST) of the request.
     *
     * @return The HTTP method of the request.
     */
    public String getMethod() {
        return method;
    }

    /**
     * Gets the path (URI) of the HTTP request.
     *
     * @return The path of the HTTP request.
     */
    public String getPath() {
        return path;
    }

    /**
     * Gets all headers of the HTTP request as a map of header names to header values.
     *
     * @return A map containing all the headers of the request.
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Gets the body of the HTTP request.
     *
     * @return The body of the HTTP request.
     */
    public String getBody() {
        return body;
    }

    /**
     * Retrieves the value of a specific header by its name.
     *
     * @param name The name of the header to retrieve.
     * @return The value of the header, or an empty string if the header is not present.
     */
    public String getHeader(String name) {
        return headers.getOrDefault(name, "");
    }

    /**
     * Gets all headers of the HTTP request.
     *
     * @return A map containing all headers of the HTTP request.
     */
    public Map<String, String> getAllHeaders() {
        return headers;
    }

    /**
     * Retrieves the content length from the request headers.
     *
     * @return The content length of the request body, or 0 if the header is not present or cannot be parsed.
     */
    public int getContentLength() {
        try {
            return Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Retrieves the content type from the request headers.
     *
     * @return The content type of the request, or "text/plain" if the header is not present.
     */
    public String getContentType() {
        return headers.getOrDefault("Content-Type", "text/plain");
    }

}
