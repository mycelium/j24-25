package ru.spbstu.telematics.json.jsoninteraction;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

public class JsonWriter implements JsonInteractor {
    static public String fromMapToJson(Map<String, Object> map) {
        if (map == null) {
            throw new NullPointerException("Converting map is null");
        }
        return JsonInteractor.mapToJson(map);
    }

    static public String fromObjectToJsonString(Object object) throws IllegalAccessException {
        if (object == null) {
            return "null";
        }

        if (object instanceof Number || object instanceof Boolean || object instanceof Character) {
            return object.toString();
        }
        if (object instanceof String) {
            return "\"" + escapeJsonString((String) object) + "\"";
        }

        if (object.getClass().isArray()) {
            return arrayToJsonString(object);
        }

        if (object instanceof Collection) {
            return collectionToJsonString((Collection<?>) object);
        }

        if (object instanceof Map) {
            return JsonInteractor.mapToJson((Map<String, Object>) object);
        }

        return objectToJsonString(object);
    }

    private static String objectToJsonString(Object object) throws IllegalAccessException {
        StringBuilder json = new StringBuilder("{");
        Class<?> filledClass = object.getClass();
        Field[] fields = filledClass.getDeclaredFields();

        boolean firstField = true;
        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(object);

            if (!firstField) {
                json.append(",");
            }
            json.append("\"").append(field.getName()).append("\":").append(fromObjectToJsonString(value));
            firstField = false;
        }

        json.append("}");
        return json.toString();
    }

    private static String arrayToJsonString(Object array) throws IllegalAccessException {
        StringBuilder json = new StringBuilder("[");
        int length = java.lang.reflect.Array.getLength(array);

        for (int i = 0; i < length; i++) {
            if (i > 0) {
                json.append(",");
            }
            Object element = java.lang.reflect.Array.get(array, i);
            json.append(fromObjectToJsonString(element));
        }

        json.append("]");
        return json.toString();
    }

    private static String collectionToJsonString(Collection<?> collection) throws IllegalAccessException {
        StringBuilder json = new StringBuilder("[");
        boolean firstElement = true;

        for (Object element : collection) {
            if (!firstElement) {
                json.append(",");
            }
            json.append(fromObjectToJsonString(element));
            firstElement = false;
        }

        json.append("]");
        return json.toString();
    }

    private static String escapeJsonString(String str) {
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
