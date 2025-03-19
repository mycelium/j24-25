package dev.tishenko;

import java.util.Map;

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

        return obj.toString();
    }
}
