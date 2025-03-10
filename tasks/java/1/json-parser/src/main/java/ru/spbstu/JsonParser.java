package main.java.ru.spbstu;

import java.util.*;
import java.util.stream.Collectors;

public class JsonParser {

    public Map<String, Object> fromJsonToMap(String json) {
        return parseJson(json);
    }

    private Map<String, Object> parseJson(String json) {
        json = json.trim();
        if (json.startsWith("{") && json.endsWith("}")) {
            json = json.substring(1, json.length() - 1).trim();
            Map<String, Object> map = new HashMap<>();
            int braceCount = 0;
            int bracketCount = 0;
            StringBuilder keyBuilder = new StringBuilder();
            StringBuilder valueBuilder = new StringBuilder();
            String key = null;
            boolean isKey = true;

            for (char ch : json.toCharArray()) {
                if (ch == '{') braceCount++;
                if (ch == '}') braceCount--;
                if (ch == '[') bracketCount++;
                if (ch == ']') bracketCount--;

                if (ch == ':' && braceCount == 0 && bracketCount == 0) {
                    key = keyBuilder.toString().trim().replace("\"", "");
                    keyBuilder.setLength(0);
                    isKey = false;
                    continue;
                }

                if (ch == ',' && braceCount == 0 && bracketCount == 0) {
                    map.put(key, extractValue(valueBuilder.toString().trim()));
                    valueBuilder.setLength(0);
                    isKey = true;
                    continue;
                }

                if (isKey) {
                    keyBuilder.append(ch);
                } else {
                    valueBuilder.append(ch);
                }
            }

            if (key != null && !valueBuilder.isEmpty()) {
                map.put(key, extractValue(valueBuilder.toString().trim()));
            }

            return map;
        }
        throw new IllegalArgumentException("Invalid JSON");
    }

    private Object extractValue(String value) {
        value = value.trim();
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1); // Строка
        } else if (value.startsWith("[") && value.endsWith("]")) {
            // Обработка массива
            String[] elements = value.substring(1, value.length() - 1).split(",");
            return Arrays.stream(elements)
                    .map(String::trim)
                    .collect(Collectors.toList());
        } else if (value.startsWith("{") && value.endsWith("}")) {
            return parseJson(value);
        } else if (value.equals("null")) {
            return null; // null
        } else if (value.equals("true") || value.equals("false")) {
            return Boolean.parseBoolean(value); // Булево значение
        } else if (value.contains(".")) {
            return Double.parseDouble(value); // Число с плавающей точкой
        } else {
            try {
                return Integer.parseInt(value); // Целое число
            } catch (NumberFormatException e) {
                // Если не число, то возвращаем строку
                return value;
            }
        }
    }
}