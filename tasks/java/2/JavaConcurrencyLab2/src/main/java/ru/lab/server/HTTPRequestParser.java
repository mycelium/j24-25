package ru.lab.server;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class HTTPRequestParser {

    /**
     * A method for extracting the HTTP method type from a query string.
     *
     * @param request The HTTP request string.
     * @return The MethodType enumeration value corresponding to the request method.
     * @throws IllegalArgumentException If the method is not supported.
     */
    static MethodType getMethodType(String request) {
        // Разделяем запрос на строки
        String[] lines = request.split("\r\n");
        if (lines.length == 0) {
            throw new IllegalArgumentException("Invalid HTTP request: empty request");
        }

        // Первая строка — стартовая строка запроса
        String startLine = lines[0];

        // Разделяем стартовую строку на части
        String[] parts = startLine.split(" ");
        if (parts.length < 1) {
            throw new IllegalArgumentException("Invalid HTTP request: no method specified");
        }

        // Извлекаем метод (первая часть стартовой строки)
        String method = parts[0].toUpperCase(); // Приводим к верхнему регистру

        // Преобразуем строку метода в значение перечисления MethodType
        try {
            return MethodType.valueOf(method);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }
    }

    /**
     * A method for extracting headers from an HTTP request.
     *
     * @param request The HTTP request string.
     * @return Map, where the key is the header name and the value is the header value.
     */
    static Map<String, String> getHeaders(String request) {
        Map<String, String> headers = new HashMap<>();

        // Разделяем запрос на строки
        String[] lines = request.split("\r\n");

        // Пропускаем стартовую строку (первая строка)
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];

            // Пустая строка означает конец заголовков и начало тела запроса
            if (line.isEmpty()) {
                break;
            }

            // Разделяем строку на ключ и значение
            String[] parts = line.split(":", 2); // Разделяем только по первому вхождению ":"
            if (parts.length == 2) {
                String key = parts[0].trim();   // Имя заголовка
                String value = parts[1].trim();  // Значение заголовка
                headers.put(key, value);
            }
        }

        return headers;
    }

    /**
     * A method for extracting the HTTP request body.
     *
     * @param request The HTTP request string.
     * @return The request body as a string. If the body is missing, an empty string is returned.
     */
    static String getBody(String request) {
        // Разделяем запрос на части по пустой строке (два перевода строки)
        String[] parts = request.split("\r\n\r\n", 2); // Ограничиваем split до 2 частей

        // Если запрос содержит тело, возвращаем его
        if (parts.length == 2) {
            return parts[1].trim(); // Убираем лишние пробелы и возвращаем тело
        }

        // Если тело отсутствует, возвращаем пустую строку
        return "";
    }

    /**
     * A method for extracting the path from an HTTP request.
     *
     * @param request The HTTP request string.
     * @return The path from the request (for example, "/example").
     * @throws IllegalArgumentException If the request is incorrect or the path is missing.
     */
    static String getPath(String request) {
        // Разделяем запрос на строки
        String[] lines = request.split("\r\n");
        if (lines.length == 0) {
            throw new IllegalArgumentException("Invalid HTTP request: empty request");
        }

        // Первая строка — стартовая строка запроса
        String startLine = lines[0];

        // Разделяем стартовую строку на части
        String[] parts = startLine.split(" ");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid HTTP request: no path specified");
        }

        // Вторая часть — это путь
        return parts[1];
    }

    /**
     * A method for extracting Path Variables from a request path based on a path template.
     *
     * @param pathTemplate A path template with variables (for example, "/users/{id}").
     * @param requestPath  The path from the request (for example, "/users/123").
     * @return Map, where the key is the name of the variable, and the value is the value of the variable.
     * @throws IllegalArgumentException If the request path does not match the template.
     */
    static Map<String, String> extractPathVariables(String pathTemplate, String requestPath) {
        Map<String, String> pathVariables = new HashMap<>();

        // Преобразуем шаблон пути в регулярное выражение
        String regex = pathTemplate.replaceAll("\\{([^}]+)\\}", "(?<$1>[^/]+)");
        regex = "^" + regex + "$"; // Добавляем начало и конец строки

        // Компилируем регулярное выражение
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(requestPath);

        // Проверяем, соответствует ли путь запроса шаблону
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Request path does not match the template");
        }

        // Извлекаем имена переменных и их значения
        for (String variableName : matcher.namedGroups().keySet()) {
            String value = matcher.group(variableName);
            pathVariables.put(variableName, value);
        }

        return pathVariables;
    }

    /**
     * A method for comparing a path with a template
     *
     * @param pathTemplate A path template with variables (for example, "/users/{id}").
     * @param requestPath The path from the request (for example, "/users/123").
     * @return true, if the path matches the pattern, otherwise false
     */
    static boolean comparePathWithPathTemplate(String pathTemplate, String requestPath){
        // Преобразуем шаблон пути в регулярное выражение
        String regex = pathTemplate.replaceAll("\\{([^}]+)\\}", "(?<$1>[^/]+)");
        regex = "^" + regex + "$"; // Добавляем начало и конец строки

        // Компилируем регулярное выражение
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(requestPath);

        // Проверяем, соответствует ли путь запроса шаблону
        return matcher.matches();
    }

}
