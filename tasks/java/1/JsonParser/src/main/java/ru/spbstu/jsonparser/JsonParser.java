package ru.spbstu.jsonparser;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class JsonParser {

    public static Map<String, Object> fromJsonToMap(String json) {
        return parseJson(json);
    }

    public static <T> T fromJsonToClass(String json, Class<T> targetClass) {
        Map<String, Object> map = fromJsonToMap(json);
        return convertMapToObject(map, targetClass);
    }

    public static String fromObjToJson(Object object) {
        return convertObjectToJson(object);
    }


    private static Map<String, Object> parseJson(String json) {
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

    private static List<String> splitJsonEntries(String json) {
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

    private static int findColonIndex(String pair) {
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

    private static String parseKey(String keyPart) {
        keyPart = keyPart.trim();
        if (keyPart.startsWith("\"") && keyPart.endsWith("\"")) {
            return keyPart.substring(1, keyPart.length() - 1);
        }
        return keyPart;
    }

    private static Object parseValue(String value) {
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

    private static List<Object> parseArray(String array) {
        String content = array.substring(1, array.length() - 1).trim();
        if (content.isEmpty()) return Collections.emptyList();

        List<String> elements = splitJsonEntries(content);
        return elements.stream()
                .map(JsonParser::parseValue)
                .collect(Collectors.toList());
    }

    private static Object parseNumber(String value) {
        try {
            if (value.contains(".")) {
                return Double.parseDouble(value);
            }
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number: " + value);
        }
    }


    private static <T> T convertMapToObject(Map<String, Object> map, Class<T> targetClass) {
        try {
            T instance = targetClass.getDeclaredConstructor().newInstance();
            for (Field field : targetClass.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = map.get(field.getName());
                //if (value == null) continue;

                // Обработка вложенности
                if (value instanceof Map) {
                    value = convertMapToObject((Map<String, Object>) value, field.getType());
                }

                // Обработка коллекций и массивов
                if (value instanceof List<?> list) {
                    value = convertListToFieldType(list, field);
                }

                field.set(instance, value);
            }
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert map to object", e);
        }
    }

    private static Object convertListToFieldType(List<?> list, Field field) {
        Class<?> fieldType = field.getType();

        if (fieldType.isArray()) {
            return convertListToArray(list, fieldType.getComponentType());
        }

        if (Collection.class.isAssignableFrom(fieldType)) {
            return convertListToCollection(list, fieldType);
        }

        throw new IllegalArgumentException("Unsupported field type: " + fieldType);
    }

    private static Object convertListToArray(List<?> list, Class<?> componentType) {
        Object array = Array.newInstance(componentType, list.size());
        for (int i = 0; i < list.size(); i++) {
            Array.set(array, i, list.get(i));
        }
        return array;
    }

    private static Object convertListToCollection(List<?> list, Class<?> collectionType) {
        try {
            Collection<Object> collection = createCollectionInstance(collectionType);
            collection.addAll(list);
            return collection;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create collection", e);
        }
    }


    private static Collection<Object> createCollectionInstance(Class<?> collectionType) {
        if (collectionType.isInterface()) {
            // Для интерфейсов стандартные реализации
            if (Set.class.isAssignableFrom(collectionType)) {
                return new HashSet<>();
            } else if (List.class.isAssignableFrom(collectionType)) {
                return new ArrayList<>();
            } else {
                throw new IllegalArgumentException("Unsupported collection interface: " + collectionType);
            }
        } else {
            // Для конкретных классов создаем экземпляр
            try {
                return (Collection<Object>) collectionType.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Failed to create collection instance: " + collectionType, e);
            }
        }
    }

    private static String convertObjectToJson(Object object) {
        return switch (object) {
            case null -> "null";
            case String str -> "\"" + escapeJsonString(str) + "\"";
            case Number num -> num.toString();
            case Boolean bool -> bool.toString();
            case Collection<?> collection -> serializeCollection(collection);
            case Object array when array.getClass().isArray() -> serializeArray(array);
            default -> serializeObject(object);
        };
    }

    private static String serializeObject(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();
        Arrays.stream(fields).forEach(field -> field.setAccessible(true));

        return Arrays.stream(fields)
                .map(field -> {
                    try {
                        String key = field.getName();
                        Object value = field.get(object);
                        return "\"" + key + "\":" + convertObjectToJson(value);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Failed to serialize object", e);
                    }
                })
                .collect(Collectors.joining(",", "{", "}"));
    }

    private static String serializeCollection(Collection<?> collection) {
        return collection.stream()
                .map(JsonParser::convertObjectToJson)
                .collect(Collectors.joining(",", "[", "]"));
    }

    private static String serializeArray(Object array) {
        List<Object> list = Arrays.asList((Object[]) array);
        return serializeCollection(list);
    }

    /*private static String serializeArray(Object array) {
        return Arrays.stream((Object[]) array)
                .map(JsonParser::convertObjectToJson)
                .collect(Collectors.joining(",", "[", "]"));
    }*/

    private static String escapeJsonString(String str) {
        if (str == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            switch (c) {
                case '\\' -> sb.append("\\\\");
                case '\"' -> sb.append("\\\"");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }

}