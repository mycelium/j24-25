package dev.tishenko;

import java.lang.reflect.Array;
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

        if (obj.getClass().isArray()) {
            return arrayToJson(obj);
        }

        return obj.toString();
    }
}
