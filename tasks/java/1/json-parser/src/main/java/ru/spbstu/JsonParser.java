package main.java.ru.spbstu;

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
        return Stream.of(value)
                .map(String::trim)
                .map(v -> {
                    if (v.startsWith("\"") && v.endsWith("\"")) {
                        return v.substring(1, v.length() - 1);
                    } else if (v.startsWith("[") && v.endsWith("]")) {
                        return parseArray(v);
                    } else if (v.startsWith("{") && v.endsWith("}")) {
                        return parseJson(v);
                    } else if (v.equalsIgnoreCase("null")) {
                        return null;
                    } else if (v.equalsIgnoreCase("true") || v.equalsIgnoreCase("false")) {
                        return Boolean.parseBoolean(v);
                    } else {
                        return parseNumber(v);
                    }
                })
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid value: " + value));
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