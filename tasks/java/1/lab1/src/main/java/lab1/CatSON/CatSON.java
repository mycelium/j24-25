package lab1.CatSON;
import java.util.Map;

public class CatSON {

    private static final Map<Character, String> MAP_OF_SOME_ISO_SYMBOLS = Map.of(
            '"', "\\\"",
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

        if (obj instanceof Character) {
            return readCharacterToJson((Character) obj);
        }

        return "";
    }


    private String readCharacterToJson(Character obj) {

        if (Character.isISOControl(obj)) {
            if(MAP_OF_SOME_ISO_SYMBOLS.containsKey(obj))
                return "\"" + MAP_OF_SOME_ISO_SYMBOLS.get(obj) + "\"";
            return "\"" + String.format("\\u%04x", (int) obj) + "\"";
        }
        return "\"" + obj + "\"";
    }

}
