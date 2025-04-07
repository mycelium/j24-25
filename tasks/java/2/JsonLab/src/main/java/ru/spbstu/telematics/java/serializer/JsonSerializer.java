package ru.spbstu.telematics.java.serializer;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsonSerializer {

    public static String parseToString(Object sourceObject) throws IllegalArgumentException {
        if (sourceObject == null) {
            return "null";
        }

        // Обработка строк
        if (sourceObject instanceof String) {
            return "\"" + escapeJson((String) sourceObject) + "\"";
        }

        // Обработка чисел и boolean
        if (sourceObject instanceof Number || sourceObject instanceof Boolean) {
            return sourceObject.toString();
        }

        // Обработка enum
        if (sourceObject.getClass().isEnum()) {
            return "\"" + ((Enum<?>) sourceObject).name() + "\"";
        }

        // Обработка массивов
        if (sourceObject.getClass().isArray()) {
            return arrayToJsonString(sourceObject);
        }

        // Обработка коллекций
        if (sourceObject instanceof Collection) {
            return collectionToJsonString((Collection<?>) sourceObject);
        }

        // Обработка Map
        if (sourceObject instanceof Map) {
            return mapToJsonString((Map<?, ?>) sourceObject);
        }

        // Обработка обычных объектов
        return objectToJsonString(sourceObject);
    }

    public static String objectToJsonString(Object obj) {
        Map<String, Object> fieldMap = collectAllFields(obj);
        return mapToJsonString(fieldMap);
    }

    private static String arrayToJsonString(Object array) {
        StringBuilder sb = new StringBuilder("[");
        int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(parseToString(Array.get(array, i)));
        }
        sb.append("]");
        return sb.toString();
    }

    private static String collectionToJsonString(Collection<?> collection) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Object item : collection) {
            if (!first) {
                sb.append(",");
            }
            first = false;
            sb.append(parseToString(item));
        }
        sb.append("]");
        return sb.toString();
    }

    private static <T> Map<String, Object> collectAllFields(T sourceObject) {
        Map<String, Object> map = new LinkedHashMap<>(); // Для сохранения порядка
        Class<?> currentClass = sourceObject.getClass();

        while (currentClass != null && currentClass != Object.class) {
            for (Field field : currentClass.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(Modifier.isStatic(field.getModifiers()) ? null : sourceObject);
                    if (value != null) {
                        map.put(field.getName(), value);
                    }
                } catch (IllegalAccessException e) {
                    continue;
                }
            }
            currentClass = currentClass.getSuperclass();
        }
        return map;
    }

    private static String mapToJsonString(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) {
                sb.append(",");
            }
            first = false;
            sb.append("\"").append(escapeJson(entry.getKey().toString())).append("\":");
            sb.append(parseToString(entry.getValue()));
        }
        sb.append("}");
        return sb.toString();
    }

    public static String escapeJson(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static String buildJsonString(Map<String, Object> map) {
        StringBuilder jsonBuilder = new StringBuilder("{");
        boolean first = true;

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) {
                jsonBuilder.append(",");
            }
            first = false;

            jsonBuilder.append("\"").append(escapeJson(entry.getKey())).append("\":");
            Object value = entry.getValue();

            if (value == null) {
                jsonBuilder.append("null");
            } else if (value instanceof String) {
                jsonBuilder.append("\"").append(escapeJson((String) value)).append("\"");
            } else if (value instanceof Number || value instanceof Boolean) {
                jsonBuilder.append(value);
            } else {
                // Рекурсивная обработка вложенных объектов
                jsonBuilder.append(objectToJsonString(value));
            }
        }

        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }
}
