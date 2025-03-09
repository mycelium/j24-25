package ru.lab.json_parser;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.IntStream;


public class JSONParser {

    public static <T> T readJsonToObject(String json, T object) {
        Map<String, Object> map = readJsonToMap(json);
        Class<?> clazz = object.getClass();
        Object newObject = fillClazzWithMap(map, clazz);
        Field[] fields = newObject.getClass().getDeclaredFields();

        for(Field field: fields){
            try{
                field.setAccessible(true);
                field.set(object, field.get(newObject));
            }catch (IllegalAccessException exc){
                exc.printStackTrace();
            }
        }
        return object;
    }

    private static <T> T fillObjectWithMap(Map<String, Object> map, T object){
        try{
            Class<?> clazz = object.getClass();
            for (Field field : clazz.getDeclaredFields()) {
                String fieldName = field.getName();
                if (map.containsKey(fieldName)) {
                    Class<?> fieldType = field.getType();
                    Object value = map.get(fieldName);

                    // Если значение является Map, рекурсивно преобразуем его в объект
                    if (value instanceof Map) {
                        value = fillObjectWithMap((Map<String, Object>) value, fieldType);
                    }

                    // Если значение является List, обрабатываем его элементы
                    if (value instanceof List<?> list) {
                        if (!list.isEmpty() && list.get(0) instanceof Map) {
                            Class<?> listType = (Class<?>) ((java.lang.reflect.ParameterizedType) field.getGenericType())
                                    .getActualTypeArguments()[0];
                            List<Object> typedList = (List<Object>) list;
                            typedList.replaceAll(o -> fillObjectWithMap((Map<String, Object>) o, listType));
                        }
                    }

                    // Устанавливаем значение поля
                    String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    Method setter = clazz.getMethod(setterName, fieldType);
                    setter.invoke(object, value);
                }
            }

            return object;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert map to object", e);
        }
    }


    private static <T> T fillClazzWithMap(Map<String, Object> map, Class<T> clazz){
        try{
            List<Object> arguments = new ArrayList<>();
            Constructor<?>[] constructors = clazz.getDeclaredConstructors();
            for (Field field : clazz.getDeclaredFields()) {
                String fieldName = field.getName();
                if (map.containsKey(fieldName)) {
                    Class<?> fieldType = field.getType();
                    Object value = map.get(fieldName);

                    // Если значение является Map, рекурсивно преобразуем его в объект
                    if (value instanceof Map) {
                        value = fillClazzWithMap((Map<String, Object>) value, fieldType);
                    }

                    // Если значение является List, обрабатываем его элементы
                    if (value instanceof List<?> list) {
                        if (!list.isEmpty() && list.get(0) instanceof Map) {
                            Class<?> listType = (Class<?>) ((java.lang.reflect.ParameterizedType) field.getGenericType())
                                    .getActualTypeArguments()[0];
                            List<Object> typedList = (List<Object>) list;
                            typedList.replaceAll(o -> fillClazzWithMap((Map<String, Object>) o, listType));
                        }
                    }

                    arguments.add(value);
                }
            }
            for (Constructor<?> constructor: constructors){
                try{
                    constructor.setAccessible(true);
                    return (T) constructor.newInstance(arguments.toArray());
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert map to object", e);
        }
        throw new RuntimeException("Failed to convert map to object");
    }

    public static <T> T readJsonToEntity(String json, Class<T> clazz) {
        try {
            return fillClazzWithMap(readJsonToMap(json), clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert json to Entity", e);
        }
    }

    public static Map<String, Object> readJsonToMap(String json){
        json = json.trim();
        if(json.startsWith("{") && json.endsWith("}")){
            return parseObject(json.substring(1, json.length() - 1));
        } else {
            throw new IllegalArgumentException("Invalid JSON string");
        }
    }

    private static Map<String, Object> parseObject(String json){
        Map<String, Object> map = new HashMap<>();
        StringBuilder keyBuilder = new StringBuilder();
        StringBuilder valueBuilder = new StringBuilder();
        int depth = 0;
        boolean inQuotes = false;
        char prevChar = '\0';
        String key = null;

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);

            if (c == '"' && prevChar != '\\') {
                inQuotes = !inQuotes;
            }

            if (!inQuotes) {
                if (c == '{' || c == '[') {
                    depth++;
                } else if (c == '}' || c == ']') {
                    depth--;
                }
            }

            if (depth == 0 && !inQuotes && c == ':' && key == null) {
                key = keyBuilder.toString().trim();
                if (key.startsWith("\"") && key.endsWith("\"")) {
                    key = key.substring(1, key.length() - 1);
                }
                keyBuilder.setLength(0);
                continue;
            }

            if (depth == 0 && !inQuotes && (c == ',' || i == json.length() - 1)) {
                if (i == json.length() - 1) {
                    valueBuilder.append(c);
                }
                String value = valueBuilder.toString().trim();
                map.put(key, parseValue(value));
                valueBuilder.setLength(0);
                key = null;
                continue;
            }

            if (key == null) {
                keyBuilder.append(c);
            } else {
                valueBuilder.append(c);
            }

            prevChar = c;
        }

        return map;
    }

    private static Object parseValue(String value) {
        value = value.trim();
        if (value.startsWith("{") && value.endsWith("}")) {
            return parseObject(value.substring(1, value.length() - 1));
        } else if (value.startsWith("[") && value.endsWith("]")) {
            return parseArray(value.substring(1, value.length() - 1));
        } else if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        } else if (value.equals("true") || value.equals("false")) {
            return Boolean.parseBoolean(value);
        } else if (value.equals("null")) {
            return null;
        } else {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e1) {
                try {
                    return Double.parseDouble(value);
                } catch (NumberFormatException e2) {
                    throw new IllegalArgumentException("Invalid JSON value: " + value);
                }
            }
        }
    }

    private static List<Object> parseArray(String json) {
        List<Object> list = new ArrayList<>();
        StringBuilder valueBuilder = new StringBuilder();
        int depth = 0;
        boolean inQuotes = false;
        char prevChar = '\0';

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);

            if (c == '"' && prevChar != '\\') {
                inQuotes = !inQuotes;
            }

            if (!inQuotes) {
                if (c == '{' || c == '[') {
                    depth++;
                } else if (c == '}' || c == ']') {
                    depth--;
                }
            }

            if (depth == 0 && !inQuotes && (c == ',' || i == json.length() - 1)) {
                if (i == json.length() - 1) {
                    valueBuilder.append(c);
                }
                String value = valueBuilder.toString().trim();
                list.add(parseValue(value));
                valueBuilder.setLength(0);
                continue;
            }

            valueBuilder.append(c);
            prevChar = c;
        }

        return list;
    }

    private static String toJsonString(Map<String, Object> map) {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");

        var iterator = map.entrySet().iterator();
        while(iterator.hasNext()){
            var entry = iterator.next();
            jsonBuilder.append("\"")
                    .append(escapeJson(entry.getKey()))
                    .append("\":")
                    .append(convertEntityToJSON(entry.getValue()));
            if (iterator.hasNext()){
                jsonBuilder.append(",");
            }
        }

        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }

    public static <T> String convertEntityToJSON(T object) {
        if(object == null){
            return "null";
        } else if (object instanceof String) {
            return "\"" + escapeJson((String) object) + "\"";
        } else if (object instanceof Map) {
            return toJsonString((Map<String, Object>) object);
        } else if (object instanceof Iterable) {
            return toJsonArray((Iterable<?>) object);
        } else if (object.getClass().isArray()) {
            return toJsonArray(arrayToIterable(object));
        } else if (isBoxed(object)) {
            return String.valueOf(object);
        }else {
            Map<String, Object> mappedJavaObject = new HashMap<>();

            for(var field: object.getClass().getDeclaredFields()){
                field.setAccessible(true);
                try{
                    mappedJavaObject.put(field.getName(), field.get(object));
                } catch (IllegalAccessException exc){
                    exc.printStackTrace();
                }
            }
            return toJsonString(mappedJavaObject);
        }
    }

    private static boolean isBoxed(Object value) {
        return value instanceof Integer ||
                value instanceof Double ||
                value instanceof Long ||
                value instanceof Float ||
                value instanceof Short ||
                value instanceof Byte ||
                value instanceof Character ||
                value instanceof Boolean;
    }

    private static Iterable<?> arrayToIterable(Object array) {
        if (array instanceof Object[]) {
            return Arrays.asList((Object[]) array);
        } else if (array instanceof int[]) {
            return Arrays.stream((int[]) array).boxed().toList();
        } else if (array instanceof boolean[] booleanArray) {
            return IntStream.range(0, booleanArray.length).mapToObj(i -> booleanArray[i]).toList();
        } else if (array instanceof long[]) {
            return Arrays.stream((long[]) array).boxed().toList();
        } else if (array instanceof double[]) {
            return Arrays.stream((double[]) array).boxed().toList();
        } else if (array instanceof float[] floatArray) {
            List<Double> doubleList = new ArrayList<>();
            for(float floatValue: floatArray){
                doubleList.add((double) floatValue);
            }
            return doubleList;
        } else if (array instanceof short[] shortArray) {
            return IntStream.range(0, shortArray.length).mapToObj(i -> shortArray[i]).toList();
        } else if (array instanceof byte[] byteArray) {
            return IntStream.range(0, byteArray.length).mapToObj(i -> byteArray[i]).toList();
        } else if (array instanceof char[]) {
            return new String((char[]) array).chars().mapToObj(c -> (char) c).toList();
        } else {
            throw new IllegalArgumentException("Unsupported array type: " + array.getClass());
        }
    }

    private static String toJsonArray(Iterable<?> iterable) {
        StringBuilder arrayBuilder = new StringBuilder();
        arrayBuilder.append("[");

        var iterator = iterable.iterator();
        while(iterator.hasNext()){
            arrayBuilder.append(convertEntityToJSON(iterator.next()));
            if (iterator.hasNext()) {
                arrayBuilder.append(",");
            }
        }
        arrayBuilder.append("]");

        return arrayBuilder.toString();
    }

    private static String escapeJson(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c < ' ') {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }


}
