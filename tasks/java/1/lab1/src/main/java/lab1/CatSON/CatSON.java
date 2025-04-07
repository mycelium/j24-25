package lab1.CatSON;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.StringJoiner;

public class CatSON {

    private static final Map<Character, String> MAP_OF_SOME_ISO_SYMBOLS =
            Map.of('"', "\\\"",
                    '\\', "\\\\",
                    '\b', "\\b",
                    '\f', "\\f",
                    '\n', "\\n",
                    '\r', "\\r",
                    '\t', "\\t");


    public String toJson(Object obj) {

        if (obj == null) {
            return "null";
        }
        if (obj instanceof Number) {
            if (obj instanceof Double && (((Double) obj).isNaN() || ((Double) obj).isInfinite())) {
                throw new IllegalArgumentException("Nan or Infinite value can`t be read to JSON");
            }
            if (obj instanceof Float && (((Float) obj).isNaN() || ((Float) obj).isInfinite())) {
                throw new IllegalArgumentException("Nan or Infinite value can`t be read to JSON");
            }
            return obj.toString();
        }
        if (obj instanceof Boolean) {
            return obj.toString();
        }
        if (obj instanceof Character) {
            return readCharacterToJson((Character) obj);
        }
        if (obj instanceof String) {
            return readStringToJson((String) obj);
        }
        if (obj.getClass().isArray()) {
            return readArrayToJson(obj);
        }
        if (obj instanceof Collection) {
            return readCollectionToJSON((Collection<?>) obj);
        }
        if (obj instanceof Map) {
            return readMapToJSON((Map<?, ?>) obj);
        }
        return "err";

    }


    private String readCharacterToJson(Character obj) {

        if (Character.isISOControl(obj) || MAP_OF_SOME_ISO_SYMBOLS.containsKey(obj)) {
            if (MAP_OF_SOME_ISO_SYMBOLS.containsKey(obj)) return "\"" + MAP_OF_SOME_ISO_SYMBOLS.get(obj) + "\"";
            return "\"" + String.format("\\u%04x", (int) obj) + "\"";
        }
        return "\"" + obj + "\"";
    }

    private String readStringToJson(String obj) {
        return "\"" +
                obj.chars()
                        .mapToObj(c -> (char) c)
                        .map(el -> Character.isISOControl(el) || MAP_OF_SOME_ISO_SYMBOLS.containsKey(el) ?
                                MAP_OF_SOME_ISO_SYMBOLS.containsKey(el) ?
                                        MAP_OF_SOME_ISO_SYMBOLS.get(el) :
                                        String.format("\\u%04x", (int) el) :
                                String.valueOf(el))
                        .collect(Collectors.joining())
                + "\"";
    }

    private String readArrayToJson(Object obj) {
        StringJoiner str = new StringJoiner(",", "[", "]");
        for (int i = 0; i < Array.getLength(obj); i++) {
            str.add(this.toJson(Array.get(obj, i)));
        }
        return str.toString();
    }

    private String readCollectionToJSON(Collection<?> obj) {
        return "[" + obj.stream()
                .map(this::toJson)
                .collect(Collectors.joining(","))
                + "]";
    }

    private String readMapToJSON(Map<?, ?> obj) {
        return "{" + obj
                .entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .map(entry -> String
                        .format("%s:%s",
                                entry.getKey() instanceof Number ||
                                        entry.getKey() instanceof Boolean ||
                                        entry.getKey() == null?
                                        "\"" + this.toJson(entry.getKey()) + "\"" :
                                        this.toJson(entry.getKey()), this.toJson(entry.getValue())))
                .collect(Collectors.joining(",")) + "}";
    }

}
