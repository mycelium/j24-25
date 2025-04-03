package ru.spbstu.telematics.java;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Модель HTTP-запроса:
 * Содержит: метод, путь, протокол, заголовки, тело.
 */
public final class LiHttpRequest {
    private final String method;
    private final String path;
    private final String protocol;
    private Map<String, String> requestHeaders = new HashMap<>();
    private final String body;
    private static final Set<String> ALLOWED_METHODS = Set.of(
            "GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS"
    );

    //конструктор с параметрами
    public LiHttpRequest(String method, String path, String protocol, Map<String, String> headers, String body) {
        checkProtocol(protocol);
        this.method = checkMethod(method);
        this.path = checkPath(path);
        this.protocol = protocol;
        this.requestHeaders = checkHeaders(headers);
        this.body = body;
    }

    //метод для проверки на соответствие переданного метода одному из доступных
    private String checkMethod(String method) {
        if (method == null){
            throw new IllegalArgumentException("Недопустимый HTTP-метод: " + method);
        }
        String upperCaseMethod = method.toUpperCase();
        if (!ALLOWED_METHODS.contains(upperCaseMethod)) {
            throw new IllegalArgumentException("Недопустимый HTTP-метод: " + method);
        }
        return upperCaseMethod;
    }

    //метод для проверки корректности пути
    private String checkPath(String path) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path не может быть null или empty");
        }
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("Path должен начинаться с '/'");
        }
        if (path.contains(" ")) {
            throw new IllegalArgumentException("Path не может содержать пробелы");
        }
        path = path.replace("/+","/"); //удаляем повторяющиеся слеши
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    //метод для проверки корректности протокола
    private void checkProtocol(String protocol) {
        if (!"HTTP/1.1".equals(protocol)) {
            throw new IllegalArgumentException("Только HTTP/1.1 поддерживается");
        }
    }

    //метод для проверки корректности заголовков
    private Map<String, String> checkHeaders(Map<String, String> headers) {
        if (headers == null) {
            throw new IllegalArgumentException("Заголовки не могут быть null");
        }
        Map<String, String> normalizedHeaders = new HashMap<>();
        headers.forEach((k, v) -> {
            if (k == null || k.isEmpty()) {
                throw new IllegalArgumentException("Заголовок не может быть null или empty");
            }
            if (v == null) {
                throw new IllegalArgumentException("Значение заголовка не может быть null");
            }
            String key = k.trim().toLowerCase();
            String value = v.trim().toLowerCase();
            normalizedHeaders.put(key,value);
        });
        return Map.copyOf(normalizedHeaders);
    }

    //геттеры
    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocol() {
        return protocol;
    }

    public Map<String, String> getHeaders() {
        return requestHeaders;
    }

    //получить заголовок по имени
    public String getHeader(String name){
        return requestHeaders.get(name.toLowerCase());
    }

    public String getBody() {
        return body;
    }

    //метод для обработки json
    public <T> T parseJson(Class<T> valueType) throws IOException {
        String contentType = this.getHeader("Content-Type");
        if (contentType == null || !contentType.startsWith("application/json")) {
            throw new IllegalStateException("Content-Type должен быть application/json");
        }
        if (this.body == null || this.body.isEmpty()) {
            throw new IllegalStateException("Тело запроса пустое");
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(this.body, valueType);
        } catch (JsonProcessingException e) {
            throw new IOException("Не удалось распарсить JSON: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            throw new IOException("Неправильный формат JSON", e);
        }
    }
}
