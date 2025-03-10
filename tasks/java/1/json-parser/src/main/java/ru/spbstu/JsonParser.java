package main.java.ru.spbstu;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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

            StringBuilder buffer = new StringBuilder();
            String key = null;
            boolean inQuotes = false;
            int bracketCount = 0; // счетчик для массивов

            for (char ch : json.toCharArray()) {
                if (ch == '"' && bracketCount == 0) {
                    inQuotes = !inQuotes; //  внутри/вне кавычек
                }

                if (ch == '[' && !inQuotes) {
                    bracketCount++;
                }

                if (ch == ']' && !inQuotes) {
                    bracketCount--;
                }

                if (ch == ':' && !inQuotes && bracketCount == 0) {
                    key = buffer.toString().trim().replace("\"", "");
                    buffer.setLength(0);
                    continue;
                }

                if (ch == ',' && !inQuotes && bracketCount == 0) {
                    map.put(key, extractValue(buffer.toString().trim()));
                    buffer.setLength(0);
                    continue;
                }

                buffer.append(ch);
            }

            if (key != null && !buffer.isEmpty()) {
                map.put(key, extractValue(buffer.toString().trim()));
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
            // Обработка массива (без вложенности)
            String[] elements = value.substring(1, value.length() - 1).split(",");
            return Arrays.stream(elements)
                    .map(String::trim) // Убираем лишние пробелы
                    .map(this::extractValue) // Обрабатываем каждый элемент
                    .collect(Collectors.toList());
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
                // Если это не число, возвращаем как строку
                return value;
            }
        }
    }
}