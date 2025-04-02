package ru.spbstu.hsai.jsparser;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class JsonSerializer {
    public static String classToJson(Object object){

        if (object instanceof Number || object instanceof Boolean){
            return object.toString();
        }
        if (object instanceof String) {
            return "\"" + replaceEscape((String) object) + "\"";
        }
        if (object.getClass().isArray()) {
            return arrayToJson(object);
        }
        if (object instanceof Collection) {
            return collectionToJson((Collection<?>) object);
        }
        if (object instanceof Map) {
            return mapToJson((Map<?, ?>) object);
        }

        return objectToJson(object);
    }

    private static String arrayToJson(Object array) {
        int length = Array.getLength(array);
        return IntStream.range(0, length)
                .mapToObj(i -> classToJson(Array.get(array, i)))
                .collect(Collectors.joining(",", "[", "]"));
    }

    private static String collectionToJson(Collection<?> collection) {
        return collection.stream()
                .map(JsonSerializer::classToJson)
                .collect(Collectors.joining(",", "[", "]"));
    }

    private static String mapToJson(Map<?, ?> map) {
        return map.entrySet().stream()
                .map(entry ->
                        "\"" + replaceEscape(entry.getKey().toString()) + "\":" + classToJson(entry.getValue()))
                .collect(Collectors.joining(",", "{", "}"));
    }

    private static String objectToJson(Object object) {
        List<Field> allFields = JsonDeserializer.getAllFields(object.getClass());
            return allFields.stream()
                    .map(field -> {
                        try {
                            field.setAccessible(true);
                            return "\"" + field.getName() + "\":" + classToJson(field.get(object));
                        } catch (IllegalAccessException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(",", "{", "}"));
        }


    private static String replaceEscape(String input) {
        input = input.replace("\"", "");
        return input.replaceAll("([\\\\\b\f\n\r\t])", "\\\\$1");
    }

}
