package main.java.ru.spbstu;

import java.util.HashMap;
import java.util.Map;

public class JsonParser {

    public Map<String, Object> fromJsonToMap(String json) {
        return parseJson(json);
    }

    private Map<String, Object> parseJson(String json) {
        if (json.startsWith("{") && json.endsWith("}")) {
            json = json.substring(1, json.length() - 1);
            Map<String, Object> map = new HashMap<>();

            String[] pairs = json.split(",");
            for (String pair : pairs) {
                String[] keyValue = pair.split(":", 2); // Разбиваем на ключ и значение

                String key = keyValue[0].replace("\"", "");
                String value = keyValue[1];
                map.put(key, extractValue(value));
            }

            return map;
        }
        throw new IllegalArgumentException("Invalid JSON");
    }


    private Object extractValue(String value) {
        value = value.trim();
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1); // Строка
        } else if (value.equals("null")) {
            return null; // null
        } else if (value.equals("true") || value.equals("false")) {
            return Boolean.parseBoolean(value); // Булево значение
        } else if (value.contains(".")) {
            return Double.parseDouble(value); // Число с плавающей точкой
        } else {
            return Integer.parseInt(value); // Целое число
        }
    }
}