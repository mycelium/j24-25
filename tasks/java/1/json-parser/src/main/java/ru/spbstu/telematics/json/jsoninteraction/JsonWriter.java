package ru.spbstu.telematics.json.jsoninteraction;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Class for writing Map and fields of an object in JSON string
 *
 * @author Astafiev Igor (StanleyStanMarsh)
 */
public class JsonWriter implements JsonInteractor {
    /**
     * Write Map into JSON formatted string. Check {@link JsonWriter#mapToJson(Map)} for more details
     * @param map Map of (String, Object) that will be written
     * @return JSON formatted string
     */
    static public String fromMapToJson(Map<String, Object> map) {
        if (map == null) {
            throw new NullPointerException("Converting map is null");
        }
        return JsonInteractor.mapToJson(map);
    }

    /**
     * Writes fields of the {@code object} in JSON string
     * @param object object that should be written in JSON string
     * @return JSON string that contains all {@code object}'s fields
     * @implNote For more details about exception check {@link #objectToJsonString(Object)}
     */
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

        // Получаем все поля, включая унаследованные
        List<Field> allFields = getAllFields(filledClass);

        boolean firstField = true;
        for (Field field : allFields) {
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

    /**
     * Рекурсивно собирает все поля класса, включая поля суперклассов.
     */
    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    /**
     * Convert {@code array} into string of JSON array
     * @param array converting Array
     * @return string that contains elements of {@code array}
     * @implNote For more details about exception check {@link #objectToJsonString(Object)}
     */
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

    /**
     * Convert {@code collection} into string of JSON array
     * @param collection converting Collection
     * @return string that contains elements of {@code collection}
     * @implNote For more details about exception check {@link #objectToJsonString(Object)}
     */
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

    /**
     * Escapes special characters in a string so that it is displayed correctly in JSON format.
     * @param str original string
     * @return correct formatted string
     */
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
