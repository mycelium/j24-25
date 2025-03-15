package main.java.ru.spbstu;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JsonParser {

    public Map<String, Object> fromJsonToMap(String json) {
        return parseJson(json);
    }

    private Map<String, Object> parseJson(String json) {
        json = json.trim();
        if (!json.startsWith("{") || !json.endsWith("}")) {
            throw new IllegalArgumentException("Invalid JSON: must start with { and end with }");
        }
        json = json.substring(1, json.length() - 1).trim();
        Map<String, Object> map = new HashMap<>();
        List<String> keyValuePairs = splitJsonEntries(json);

        keyValuePairs.forEach(pair -> {
            int colonIndex = findColonIndex(pair);
            String key = parseKey(pair.substring(0, colonIndex));
            Object value = parseValue(pair.substring(colonIndex + 1).trim());
            map.put(key, value);
        });

        return map;
    }

    private List<String> splitJsonEntries(String json) {
        List<String> entries = new ArrayList<>();
        int braceCount = 0;
        int bracketCount = 0;
        StringBuilder currentEntry = new StringBuilder();

        for (char ch : json.toCharArray()) {
            if (ch == '{') braceCount++;
            if (ch == '}') braceCount--;
            if (ch == '[') bracketCount++;
            if (ch == ']') bracketCount--;

            if (ch == ',' && braceCount == 0 && bracketCount == 0) {
                entries.add(currentEntry.toString().trim());
                currentEntry.setLength(0);
            } else {
                currentEntry.append(ch);
            }
        }

        if (!currentEntry.isEmpty()) {
            entries.add(currentEntry.toString().trim());
        }

        return entries;
    }

    private int findColonIndex(String pair) {
        int braceCount = 0;
        int bracketCount = 0;

        for (int i = 0; i < pair.length(); i++) {
            char ch = pair.charAt(i);
            if (ch == '{') braceCount++;
            if (ch == '}') braceCount--;
            if (ch == '[') bracketCount++;
            if (ch == ']') bracketCount--;

            if (ch == ':' && braceCount == 0 && bracketCount == 0) {
                return i;
            }
        }

        throw new IllegalArgumentException("Invalid key-value pair: " + pair);
    }

    private String parseKey(String keyPart) {
        return Stream.of(keyPart)
                .map(String::trim)
                .map(k -> {
                    if (k.startsWith("\"") && k.endsWith("\"")) {
                        return k.substring(1, k.length() - 1);
                    }
                    return k;
                })
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid key: " + keyPart));
    }

    private Object parseValue(String value) {
        String v = value.trim();
        return switch (v) {
            case String s when s.startsWith("\"") && s.endsWith("\"") ->
                    s.substring(1, s.length() - 1);
            case String s when s.startsWith("[") && s.endsWith("]") ->
                    parseArray(s);
            case String s when s.startsWith("{") && s.endsWith("}") ->
                    parseJson(s);
            case "null", "NULL", "Null" -> null;
            case "true", "TRUE", "True" -> true;
            case "false", "FALSE", "False" -> false;
            default -> parseNumber(v);
        };
    }

    private List<Object> parseArray(String array) {
        String content = array.substring(1, array.length() - 1).trim();
        if (content.isEmpty()) return Collections.emptyList();

        List<String> elements = splitJsonEntries(content);
        return elements.stream()
                .map(this::parseValue)
                .collect(Collectors.toList());
    }

    private Object parseNumber(String value) {
        try {
            if (value.contains(".")) {
                return Double.parseDouble(value);
            }
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number: " + value);
        }
    }
}