package dev.tishenko;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;
import java.util.StringJoiner;

public class Tson {
    static final Map<Character, String> SPECIAL_CHARACTERS = Map.of(
            '"', "\\\"",
            '\\', "\\\\",
            '\b', "\\b",
            '\f', "\\f",
            '\n', "\\n",
            '\r', "\\r",
            '\t', "\\t");

    private String escapeCharacter(Character c) {
        if (SPECIAL_CHARACTERS.containsKey(c)) {
            return SPECIAL_CHARACTERS.get(c);
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
            if (Modifier.isTransient(field.getModifiers())) {
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

        if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        }

        if (obj instanceof Collection) {
            return collectionToJson((Collection<?>) obj);
        }

        if (obj.getClass().isArray()) {
            return arrayToJson(obj);
        }

        return objectToJson(obj);
    }
}
