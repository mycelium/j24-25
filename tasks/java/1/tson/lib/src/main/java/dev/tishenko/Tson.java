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

    private String characterToJson(Character c) {
        if (SPECIAL_CHARACTERS.containsKey(c)) {
            return "\"" + SPECIAL_CHARACTERS.get(c) + "\"";
        }

        return "\"" + c + "\"";
    }

    public String toJson(Object obj) {
        if (obj == null) {
            return "null";
        }

        if (obj instanceof Character) {
            return characterToJson((Character) obj);
        }

        return obj.toString();
    }
}
