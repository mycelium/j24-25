package dev.tishenko;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class MapsToJsonTest {
    @Test
    void mapToJson() {
        Tson tson = new Tson();
        Map<String, Object> map = Map.of(
                "name", "Alice",
                "age", 30,
                "scores", Arrays.asList(90, 85));

        String result = tson.toJson(map);
        assertTrue(result.contains("\"name\":\"Alice\""));
        assertTrue(result.contains("\"age\":30"));
        assertTrue(result.contains("\"scores\":[90,85]"));
    }

    @Test
    void mapWithOneKeyToJson() {
        Tson tson = new Tson();
        Map<String, String> map = Map.of("Hello", "World");

        assertEquals("{\"Hello\":\"World\"}", tson.toJson(map));
    }

    @Test
    void mapWithNonStringKeysToJson() {
        Tson tson = new Tson();
        Map<Object, Object> map = Map.of(
                1, "one",
                2.5, "two point five",
                true, "boolean");

        String result = tson.toJson(map);
        assertTrue(result.contains("\"1\":\"one\""));
        assertTrue(result.contains("\"2.5\":\"two point five\""));
        assertTrue(result.contains("\"true\":\"boolean\""));
    }

    @Test
    void mapWithNullKeyToJson() {
        Tson tson = new Tson();
        Map<String, Object> map = new HashMap<>();
        map.put(null, "nullKeyValue");

        assertEquals("{\"null\":\"nullKeyValue\"}", tson.toJson(map));
    }

    @Test
    void mapWithNullValueToJson() {
        Tson tson = new Tson();
        Map<String, Object> map = new HashMap<>();
        map.put("key1", null);
        map.put("key2", "value2");

        String result = tson.toJson(map);
        assertTrue(result.contains("\"key1\":null"));
        assertTrue(result.contains("\"key2\":\"value2\""));
    }

    @Test
    void emptyMapToJson() {
        Tson tson = new Tson();
        Map<String, Object> map = Map.of();

        assertEquals("{}", tson.toJson(map));
    }

    @Test
    void mapWithSpecialCharactersToJson() {
        Tson tson = new Tson();
        Map<String, String> map = Map.of(
                "\"", "\"",
                "\\", "\\",
                "\b", "\b",
                "\f", "\f",
                "\n", "\n",
                "\r", "\r",
                "\t", "\t");

        String result = tson.toJson(map);

        assertTrue(result.contains("\"\\\"\":\"\\\"\"")); // "
        assertTrue(result.contains("\"\\\\\":\"\\\\\"")); // \
        assertTrue(result.contains("\"\\b\":\"\\b\"")); // \b
        assertTrue(result.contains("\"\\f\":\"\\f\"")); // \f
        assertTrue(result.contains("\"\\n\":\"\\n\"")); // \n
        assertTrue(result.contains("\"\\r\":\"\\r\"")); // \r
        assertTrue(result.contains("\"\\t\":\"\\t\"")); // \t
    }

}
