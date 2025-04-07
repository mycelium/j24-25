package lab1.CatSON;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ReadMapToJSONTests {
    @Test
    void mapToJson() {
        CatSON CatSON = new CatSON();
        Map<String, Object> map = Map.of(
                "name", "Alice",
                "age", 30,
                "scores", Arrays.asList(90, 85));

        String result = CatSON.toJson(map);
        assertTrue(result.contains("\"name\":\"Alice\""));
        assertTrue(result.contains("\"age\":30"));
        assertTrue(result.contains("\"scores\":[90,85]"));
    }

    @Test
    void mapWithOneKeyToJson() {
        CatSON CatSON = new CatSON();
        Map<String, String> map = Map.of("Hello", "World");

        assertEquals("{\"Hello\":\"World\"}", CatSON.toJson(map));
    }

    @Test
    void mapWithNonStringKeysToJson() {
        CatSON CatSON = new CatSON();
        Map<Object, Object> map = Map.of(
                1, "one",
                2.5, "two point five",
                true, "boolean");

        String result = CatSON.toJson(map);
        assertTrue(result.contains("\"1\":\"one\""));
        assertTrue(result.contains("\"2.5\":\"two point five\""));
        assertTrue(result.contains("\"true\":\"boolean\""));
    }

    @Test
    void mapWithNullKeyToJson() {
        CatSON CatSON = new CatSON();
        Map<String, Object> map = new HashMap<>();
        map.put(null, "nullKeyValue");

        assertEquals("{\"null\":\"nullKeyValue\"}", CatSON.toJson(map));
    }

    @Test
    void mapWithNullValueToJson() {
        CatSON CatSON = new CatSON();
        Map<String, Object> map = new HashMap<>();
        map.put("key1", null);
        map.put("key2", "value2");

        String result = CatSON.toJson(map);
        assertTrue(!result.contains("\"key1\":null"));
        assertTrue(result.contains("\"key2\":\"value2\""));
    }

    @Test
    void emptyMapToJson() {
        CatSON CatSON = new CatSON();
        Map<String, Object> map = Map.of();

        assertEquals("{}", CatSON.toJson(map));
    }

    @Test
    void mapWithSpecialCharactersToJson() {
        CatSON CatSON = new CatSON();
        Map<String, String> map = Map.of(
                "\"", "\"",
                "\\", "\\",
                "\b", "\b",
                "\f", "\f",
                "\n", "\n",
                "\r", "\r",
                "\t", "\t");

        String result = CatSON.toJson(map);

        assertTrue(result.contains("\"\\\"\":\"\\\"\"")); // "
        assertTrue(result.contains("\"\\\\\":\"\\\\\"")); // \
        assertTrue(result.contains("\"\\b\":\"\\b\"")); // \b
        assertTrue(result.contains("\"\\f\":\"\\f\"")); // \f
        assertTrue(result.contains("\"\\n\":\"\\n\"")); // \n
        assertTrue(result.contains("\"\\r\":\"\\r\"")); // \r
        assertTrue(result.contains("\"\\t\":\"\\t\"")); // \t
    }

}