package dev.tishenko;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class Tson {
    private static final Map<Character, String> SPECIAL_CHARACTERS_TO_JSON = Map.of(
            '"', "\\\"",
            '\\', "\\\\",
            '\b', "\\b",
            '\f', "\\f",
            '\n', "\\n",
            '\r', "\\r",
            '\t', "\\t");

    private static final Map<String, Character> SPECIAL_CHARACTERS_FROM_JSON = Map.of(
            "\\\"", '"',
            "\\\\", '\\',
            "\\b", '\b',
            "\\f", '\f',
            "\\n", '\n',
            "\\r", '\r',
            "\\t", '\t');

    private String escapeCharacter(Character c) {
        if (SPECIAL_CHARACTERS_TO_JSON.containsKey(c)) {
            return SPECIAL_CHARACTERS_TO_JSON.get(c);
        }
        return c.toString();
    }

    private String characterToJson(Character c) {
        return "\"" + escapeCharacter(c) + "\"";
    }

    private String stringToJson(String s) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"");

        for (Character c : s.toCharArray()) {
            sb.append(escapeCharacter(c));
        }

        sb.append("\"");
        return sb.toString();
    }

    private String arrayToJson(Object array) {
        StringJoiner sj = new StringJoiner(",", "[", "]");
        int length = Array.getLength(array);

        for (int i = 0; i < length; i++) {
            Object element = Array.get(array, i);
            sj.add(toJson(element));
        }

        return sj.toString();
    }

    private String objectToJson(Object obj) {
        Class<?> clazz = obj.getClass();

        if (clazz.isAnonymousClass() ||
                clazz.isMemberClass() && !Modifier.isStatic(clazz.getModifiers()) ||
                clazz.isLocalClass()) {
            return "null";
        }

        StringJoiner sj = new StringJoiner(",", "{", "}");

        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isTransient(field.getModifiers())
                    || Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            field.setAccessible(true);

            try {
                Object value = field.get(obj);
                sj.add("\"" + field.getName() + "\":" + toJson(value));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return sj.toString();
    }

    private String collectionToJson(Collection<?> collection) {
        StringJoiner sj = new StringJoiner(",", "[", "]");

        for (Object obj : collection) {
            sj.add(toJson(obj));
        }

        return sj.toString();
    }

    private String mapToJson(Map<?, ?> map) {
        StringJoiner sj = new StringJoiner(",", "{", "}");
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object key = entry.getKey();
            String keyStr;
            if (key == null) {
                keyStr = "\"null\"";
            } else {
                keyStr = stringToJson(key.toString());
            }
            String valueStr = toJson(entry.getValue());
            sj.add(keyStr + ":" + valueStr);
        }
        return sj.toString();
    }

    public String toJson(Object obj) {
        if (obj == null) {
            return "null";
        }

        if (obj instanceof Character) {
            return characterToJson((Character) obj);
        }

        if (obj instanceof String) {
            return stringToJson((String) obj);
        }

        if (obj instanceof Boolean) {
            return obj.toString();
        }

        if (obj instanceof Number) {
            if (obj instanceof Double) {
                Double d = (Double) obj;
                if (d.isNaN() || d.isInfinite()) {
                    throw new IllegalArgumentException(
                            "Invalid double value (NaN or Infinity).");
                }
            } else if (obj instanceof Float) {
                Float f = (Float) obj;
                if (f.isNaN() || f.isInfinite()) {
                    throw new IllegalArgumentException(
                            "Invalid float value (NaN or Infinity).");
                }
            }
            return obj.toString();
        }

        if (obj instanceof Collection) {
            return collectionToJson((Collection<?>) obj);
        }

        if (obj instanceof Map) {
            return mapToJson((Map<?, ?>) obj);
        }

        if (obj.getClass().isArray()) {
            return arrayToJson(obj);
        }

        return objectToJson(obj);
    }

    private boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() ||
                clazz == Byte.class ||
                clazz == Short.class ||
                clazz == Integer.class ||
                clazz == Long.class ||
                clazz == Float.class ||
                clazz == Double.class ||
                clazz == Boolean.class ||
                clazz == Character.class;
    }

    @SuppressWarnings("unchecked")
    private <T> T primitivesFromJson(String json, Class<T> classOfT) throws JsonParseException {
        json = json.trim();

        try {
            return (T) switch (classOfT.getSimpleName()) {
                case "byte", "Byte" -> Byte.valueOf(json);
                case "short", "Short" -> Short.valueOf(json);
                case "int", "Integer" -> Integer.valueOf(json);
                case "long", "Long" -> Long.valueOf(json);
                case "float", "Float" -> Float.valueOf(json);
                case "double", "Double" -> Double.valueOf(json);
                case "boolean", "Boolean" -> Boolean.valueOf(json);
                case "char", "Character" -> characterFromJson(json);
                default -> throw new JsonParseException("Unsupported primitive type: " + classOfT.getName());
            };
        } catch (NumberFormatException e) {
            throw new JsonParseException("Failed to parse JSON: " + json, e);
        }
    }

    private Character characterFromJson(String json) throws JsonParseException {
        json = json.trim();

        if (json.length() >= 2 && json.startsWith("\"") && json.endsWith("\"")) {
            String content = json.substring(1, json.length() - 1);

            if (content.length() == 1) {
                return content.charAt(0);
            }

            if (content.startsWith("\\")) {
                if (SPECIAL_CHARACTERS_FROM_JSON.containsKey(content)) {
                    return SPECIAL_CHARACTERS_FROM_JSON.get(content);
                }
                if (content.startsWith("\\u") && content.length() == 6) {
                    String hex = content.substring(2);
                    try {
                        return (char) Integer.parseInt(hex, 16);
                    } catch (NumberFormatException e) {
                        throw new JsonParseException("Invalid Unicode escape sequence: " + content);
                    }
                }
                throw new JsonParseException("Unsupported escape sequence: " + content);
            }
        }

        throw new JsonParseException("Invalid JSON format for char: " + json);
    }

    @SuppressWarnings("unchecked")
    private <T> T arrayFromJson(String json, Class<T> arrayClass) throws JsonParseException {
        json = json.trim();

        if (!json.startsWith("[") || !json.endsWith("]")) {
            throw new JsonParseException("Invalid JSON array format: " + json);
        }

        String content = json.substring(1, json.length() - 1).trim();
        String[] elements = content.isEmpty() ? new String[0] : splitJsonArrayElements(content);
        Class<?> componentType = arrayClass.getComponentType();
        Object array = Array.newInstance(componentType, elements.length);
        for (int i = 0; i < elements.length; i++) {
            String elementJson = elements[i].trim();
            Object element = null;
            if (!elementJson.equals("null")) {
                element = fromJson(elementJson, componentType);
            } else if (componentType.isPrimitive()) {
                throw new JsonParseException("Cannot set null to primitive array element");
            }
            setArrayElement(array, i, element, componentType);
        }
        return (T) array;
    }

    /**
     * Метод работает как "умный" split по запятой, игнорируя все запятые в строках,
     * вложенных объектах и массивах.
     * 
     * @param content строка, в которой находится массив в JSON.
     * @return одномерный массив из строковых JSON представлений элементов исходного
     *         массива.
     */
    private String[] splitJsonArrayElements(String content) {
        List<String> elements = new ArrayList<>();
        int start = 0;
        int depth = 0;
        boolean inString = false;
        boolean escape = false;

        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);

            if (escape) {
                escape = false;
                continue;
            }

            switch (c) {
                case '\\' -> escape = true;
                case '"' -> inString = !inString;
            }

            if (!inString) {
                switch (c) {
                    case '[', '{' -> depth++;
                    case ']', '}' -> depth--;
                    case ',' -> {
                        if (depth == 0) {
                            elements.add(content.substring(start, i));
                            start = i + 1;
                        }
                    }
                }
            }
        }
        elements.add(content.substring(start));
        return elements.toArray(new String[0]);
    }

    private void setArrayElement(Object array, int index, Object element, Class<?> componentType) {
        if (componentType.isPrimitive()) {
            switch (componentType.getTypeName()) {
                case "byte" -> ((byte[]) array)[index] = (Byte) element;
                case "short" -> ((short[]) array)[index] = (Short) element;
                case "int" -> ((int[]) array)[index] = (Integer) element;
                case "long" -> ((long[]) array)[index] = (Long) element;
                case "float" -> ((float[]) array)[index] = (Float) element;
                case "double" -> ((double[]) array)[index] = (Double) element;
                case "boolean" -> ((boolean[]) array)[index] = (Boolean) element;
                case "char" -> ((char[]) array)[index] = (Character) element;
                default -> throw new IllegalStateException();
            }
        } else {
            ((Object[]) array)[index] = element;
        }
    }

    public <T> T fromJson(String json, Class<T> classOfT) throws JsonParseException {
        if (json == null || json.trim().isEmpty()) {
            throw new JsonParseException("Input JSON string is null or empty");
        }

        if (isPrimitiveOrWrapper(classOfT)) {
            return primitivesFromJson(json, classOfT);
        }

        if (classOfT.isArray()) {
            return arrayFromJson(json, classOfT);
        }

        return null;
    }
}
