package ru.spbstu.telematics.java;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Модель HTTP-ответа сервера:
 * Содержит: статус-код, заголовки, тело ответа.
 */
public class LiHttpResponse {
    private int statusCode;
    private final byte[] body;
    private Map<String,String> responseHeaders;
    private static final String CRLF = "\r\n";

    //конструктор с параметрами для бинарных данных
    public LiHttpResponse(int statusCode, byte[] body){
        this.statusCode = statusCode;
        this.body = body != null ? body : new byte[0];
        this.responseHeaders = new LinkedHashMap<>();
        setDefaultResponseHeaders();
    }

    //конструктор с параметрами для текстовых данных
    public LiHttpResponse(int statusCode, String body){
        this.statusCode = statusCode;
        this.body = body != null ? body.getBytes() : new byte[0];
        this.responseHeaders = new LinkedHashMap<>();
        setDefaultResponseHeaders();
        if (!hasHeader("Content-Type")) {
            setHeader("Content-Type", "text/plain; charset=UTF-8");
        }
    }

    //метод для установки заголовков по умолчанию
    private void setDefaultResponseHeaders(){
        setHeader("Server","Java Http Server");
        if (body != null && body.length > 0){
            setHeader("Content-Length", String.valueOf(body.length));
        }
    }

    public void setHeader(String name, String value) {
        Objects.requireNonNull(name, "Имя заголовка не может быть null");
        Objects.requireNonNull(value, "Значение заголовка не может быть null");
        responseHeaders.put(name, value);
    }

    public boolean hasHeader(String name) {
        return responseHeaders.containsKey(name);
    }

    //метод для преобразования LiHttpResponse в массив байтов
    public byte[] LiHttpResponseToByteArray() {
        StringBuilder headersBuilder = new StringBuilder()
                .append("HTTP/1.1 ").append(statusCode).append(" ")
                .append(getStatusMessage()).append(CRLF);

        responseHeaders.forEach((name, value) ->
                headersBuilder.append(name).append(": ").append(value).append(CRLF)
        );

        byte[] headers = headersBuilder.append(CRLF).toString().getBytes();

        if (body.length == 0) {
            return headers;
        }

        byte[] response = new byte[headers.length + body.length];
        System.arraycopy(headers, 0, response, 0, headers.length);
        System.arraycopy(body, 0, response, headers.length, body.length);
        return response;
    }

    //метод для преобразования json в LiHttpResponse
    public static LiHttpResponse json(String json){
        Objects.requireNonNull(json, "json-строка не может быть null");
        LiHttpResponse response = new LiHttpResponse(200,json.getBytes());
        response.setHeader("Content-Type", "application/json; charset=UTF-8");
        return response;
    }

    //метод для преобразования файла в LiHttpResponse
    public static LiHttpResponse file(Path filePath) throws IOException{
        Objects.requireNonNull(filePath, "Путь к файлу не может быть null");
        byte[] fileContent = Files.readAllBytes(filePath);
        String contentType = Files.probeContentType(filePath);

        LiHttpResponse response = new LiHttpResponse(200,fileContent);
        response.setHeader("Content-Type",
                contentType != null ? contentType : "application/octet-stream");
        return response;
    }

    private String getStatusMessage() {
        return switch (statusCode) {
            case 200 -> "OK";
            case 201 -> "Created";
            case 400 -> "Bad Request";
            case 404 -> "Not Found";
            case 500 -> "Internal Server Error";
            default -> "";
        };
    }

    public LiHttpResponse setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public byte[] getBody() {
        return body;
    }

    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    public String getResponseHeader(String name) {
        return responseHeaders.get(name);
    }

    public String getHeader(String s) {
        return responseHeaders.get(s);
    }
}
