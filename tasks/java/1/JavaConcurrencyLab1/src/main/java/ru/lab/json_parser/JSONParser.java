package ru.lab.json_parser;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.IntStream;


public class JSONParser {

    private static <T> T fillClazzWithMap(Map<String, Object> map, Class<T> clazz){
        try{
            List<Object> arguments = new ArrayList<>();
            Constructor<?>[] constructors = clazz.getDeclaredConstructors();
            for (Field field : clazz.getDeclaredFields()) {
                String fieldName = field.getName();
                if (map.containsKey(fieldName)) {
                    Class<?> fieldType = field.getType();
                    Object value = map.get(fieldName);

                    // Если значение является Map и поле не является Map, рекурсивно преобразуем его в объект
                    if (value instanceof Map && !Map.class.isAssignableFrom(fieldType)) {
                        value = fillClazzWithMap((Map<String, Object>) value, fieldType);
                    }

                    // Если значение является List, обрабатываем его элементы
                    if (value instanceof List<?> list) {
                        if (!list.isEmpty() && list.get(0) instanceof Map) {
                            Class<?> listType = (Class<?>) ((java.lang.reflect.ParameterizedType) field.getGenericType())
                                    .getActualTypeArguments()[0];
                            List<Object> typedList = (List<Object>) list;
                            typedList.replaceAll(o -> fillClazzWithMap((Map<String, Object>) o, listType));
                        } else if (fieldType.isArray()){
                            Class<?> componentType = list.isEmpty() ? Object.class : list.get(0).getClass();

                            // Создаём массив нужного типа
                            Object array = Array.newInstance(componentType, list.size());

                            // Заполняем массив элементами из списка
                            for (int i = 0; i < list.size(); i++) {
                                Array.set(array, i, list.get(i));
                            }
                            value = array;
                        }
                        // Если fieldType является Iterable (например, HashSet, ArrayList и т.д.)
                        else if (Iterable.class.isAssignableFrom(fieldType)) {
                            // Создаём новый экземпляр Iterable (например, HashSet)
                            Iterable<?> iterable = createIterableFromList(fieldType, list);

                            // Присваиваем значение полю
                            value = iterable;
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


    private static Iterable<?> createIterableFromList(Class<?> iterableType, List<?> list) throws Exception {
        // Получаем конструктор по умолчанию
        Constructor<?> constructor = iterableType.getDeclaredConstructor();

        // Создаём новый экземпляр Iterable
        Iterable<Object> iterable = (Iterable<Object>) constructor.newInstance();

        // Добавляем все элементы из List в Iterable
        if (iterable instanceof Collection) {
            ((Collection<Object>) iterable).addAll((List<Object>) list);
        } else {
            throw new UnsupportedOperationException("Unsupported Iterable type: " + iterableType.getName());
        }

        return iterable;
    }


    /**
     * Преобразует JSON-строку в объект указанного класса.
     * <p>
     * Метод парсит JSON-строку, преобразует её в {@link Map}, а затем заполняет поля объекта
     * с использованием рефлексии. Поддерживаются вложенные объекты и массивы.
     * </p>
     * <p>
     * Пример использования:
     * <pre>
     * {@code
     * String json = "{\"name\":\"John\", \"age\":30}";
     * Person person = JSONParser.readJsonToEntity(json, Person.class);
     * System.out.println(person.getName()); // Output: John
     * }
     * </pre>
     * </p>
     *
     * @param json  JSON-строка, которую необходимо преобразовать в объект.
     * @param clazz Класс, объект которого необходимо создать и заполнить.
     * @param <T>   Тип возвращаемого объекта.
     * @return Объект типа {@code T}, заполненный данными из JSON-строки.
     * @throws RuntimeException если произошла ошибка при преобразовании JSON в объект.
     */
    public static <T> T readJsonToEntity(String json, Class<T> clazz) {
        try {
            return fillClazzWithMap(readJsonToMap(json), clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert json to Entity", e);
        }
    }

    /**
     * Преобразует JSON-строку в {@link Map<String, Object>}.
     * <p>
     * Метод парсит JSON-строку, представляющую объект, и возвращает её в виде {@link Map},
     * где ключи — это имена полей, а значения — соответствующие значения из JSON.
     * Поддерживаются вложенные объекты и массивы.
     * </p>
     * <p>
     * Пример использования:
     * <pre>
     * {@code
     * String json = "{\"name\":\"John\", \"age\":30}";
     * Map<String, Object> map = JSONParser.readJsonToMap(json);
     * System.out.println(map.get("name")); // Output: John
     * }
     * </pre>
     * </p>
     *
     * @param json JSON-строка, которую необходимо преобразовать в {@link Map}.
     * @return {@link Map<String, Object>}, содержащий данные из JSON-строки.
     * @throws IllegalArgumentException если JSON-строка некорректна (например, не начинается с '{' или не заканчивается '}').
     */
    public static Map<String, Object> readJsonToMap(String json){
        json = json.trim();
        if(json.startsWith("{") && json.endsWith("}")){
            return parseObject(json.substring(1, json.length() - 1));
        } else {
            throw new IllegalArgumentException("Invalid JSON string");
        }
    }

    private static Map<String, Object> parseObject(String json) {
        Map<String, Object> map = new HashMap<>();
        StringBuilder keyBuilder = new StringBuilder();
        StringBuilder valueBuilder = new StringBuilder();
        int depth = 0;
        boolean inQuotes = false;
        char prevChar = '\0';
        String key = null;

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            inQuotes = updateInQuotes(c, prevChar, inQuotes);
            depth = updateDepth(c, inQuotes, depth);

            if (isKeyValueSeparator(c, depth, inQuotes, key)) {
                key = extractKey(keyBuilder);
                continue;
            }

            if (isValueEnd(c, depth, inQuotes, i, json.length(), key)) {
                String value = extractValue(valueBuilder, c, i, json.length());
                map.put(key, parseValue(value));
                key = null;
                continue;
            }

            appendToBuilder(key, keyBuilder, valueBuilder, c);
            prevChar = c;
        }

        if (depth != 0 || inQuotes) {
            throw new IllegalArgumentException("Invalid JSON: Unbalanced brackets or unclosed string");
        }

        return map;
    }

    private static boolean updateInQuotes(char c, char prevChar, boolean inQuotes) {
        if (c == '"' && prevChar != '\\') {
            return !inQuotes;
        }
        return inQuotes;
    }

    private static int updateDepth(char c, boolean inQuotes, int depth) {
        if (!inQuotes) {
            if (c == '{' || c == '[') {
                return depth + 1;
            } else if (c == '}' || c == ']') {
                return depth - 1;
            }
        }
        return depth;
    }

    private static boolean isKeyValueSeparator(char c, int depth, boolean inQuotes, String key) {
        return depth == 0 && !inQuotes && c == ':' && key == null;
    }

    private static String extractKey(StringBuilder keyBuilder) {
        String key = keyBuilder.toString().trim();
        if (key.startsWith("\"") && key.endsWith("\"")) {
            key = key.substring(1, key.length() - 1);
        }
        keyBuilder.setLength(0);
        return key;
    }

    private static boolean isValueEnd(char c, int depth, boolean inQuotes, int index, int length, String key) {
        return depth == 0 && !inQuotes && (c == ',' || index == length - 1) && key != null;
    }

    private static String extractValue(StringBuilder valueBuilder, char c, int index, int length) {
        if (index == length - 1) {
            valueBuilder.append(c);
        }
        String value = valueBuilder.toString().trim();
        valueBuilder.setLength(0);
        return value;
    }

    private static void appendToBuilder(String key, StringBuilder keyBuilder, StringBuilder valueBuilder, char c) {
        if (key == null) {
            keyBuilder.append(c);
        } else {
            valueBuilder.append(c);
        }
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
            inQuotes = updateInQuotes(c, prevChar, inQuotes);
            depth = updateDepth(c, inQuotes, depth);

            if (isValueSeparator(c, depth, inQuotes, i, json.length())) {
                String value = extractValue(valueBuilder, c, i, json.length());
                list.add(parseValue(value));
                continue;
            }

            valueBuilder.append(c);
            prevChar = c;
        }

        if (depth != 0 || inQuotes) {
            throw new IllegalArgumentException("Invalid JSON array: Unbalanced brackets or unclosed string");
        }

        return list;
    }

    private static boolean isValueSeparator(char c, int depth, boolean inQuotes, int index, int length) {
        return depth == 0 && !inQuotes && (c == ',' || index == length - 1);
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

    /**
     * Преобразует объект в JSON-строку.
     * <p>
     * Метод рекурсивно обходит поля объекта и преобразует их в JSON-строку.
     * Поддерживаются примитивные типы, строки, массивы, коллекции и вложенные объекты.
     * </p>
     * <p>
     * Пример использования:
     * <pre>
     * {@code
     * Person person = new Person("John", 30);
     * String json = JSONParser.convertEntityToJSON(person);
     * System.out.println(json); // Output: {"name":"John","age":30}
     * }
     * </pre>
     * </p>
     *
     * @param object Объект, который необходимо преобразовать в JSON-строку.
     * @param <T>    Тип объекта.
     * @return JSON-строка, представляющая объект.
     */
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
