package dev.tishenko;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReadmeExamplesTest {
    @Test
    void primitivesExamples() {
        // Serialization
        Tson tson = new Tson();
        assertEquals("1", tson.toJson(1)); // ==> 1
        assertEquals("\"abcd\"", tson.toJson("abcd")); // ==> "abcd"
        assertEquals("10", tson.toJson(Long.valueOf(10))); // ==> 10
        int[] values = { 1 };
        assertEquals("[1]", tson.toJson(values)); // ==> [1]

        // Deserialization
        int i = tson.fromJson("1", int.class);
        assertEquals(1, i);
        Integer intObj = tson.fromJson("1", Integer.class);
        assertEquals(1, intObj);
        Long longObj = tson.fromJson("1", Long.class);
        assertEquals(1L, longObj);
        Boolean boolObj = tson.fromJson("false", Boolean.class);
        assertEquals(false, boolObj);
        String str = tson.fromJson("\"abc\"", String.class);
        assertEquals("abc", str);
        String[] strArray = tson.fromJson("[\"abc\"]", String[].class);
        assertArrayEquals(new String[] { "abc" }, strArray);
    }

    static class BagOfPrimitives {
        private int value1 = 1;
        private String value2 = "abc";
        private transient int value3 = 3;

        BagOfPrimitives() {
            // no-args constructor
        }
    }

    @Test
    void objectExamples() {
        // Serialization
        BagOfPrimitives obj = new BagOfPrimitives();
        Tson tson = new Tson();
        String json = tson.toJson(obj);
        // ==> {"value1":1,"value2":"abc"}
        assertEquals("{\"value1\":1,\"value2\":\"abc\"}", json);

        // Deserialization
        BagOfPrimitives obj2 = tson.fromJson(json, BagOfPrimitives.class);
        // ==> obj2 is just like obj
        assertEquals(obj.value1, obj2.value1);
        assertEquals(obj.value2, obj2.value2);
        assertEquals(obj.value3, obj2.value3);
    }

    @Test
    void arrayExamples() {
        Tson tson = new Tson();
        int[] ints = { 1, 2, 3, 4, 5 };
        String[] strings = { "abc", "def", "ghi" };

        // Serialization
        assertEquals("[1,2,3,4,5]", tson.toJson(ints)); // ==> [1,2,3,4,5]
        assertEquals("[\"abc\",\"def\",\"ghi\"]", tson.toJson(strings)); // ==> ["abc", "def", "ghi"]

        // Deserialization
        int[] ints2 = tson.fromJson("[1,2,3,4,5]", int[].class);
        // ==> ints2 will be same as ints
        assertArrayEquals(ints, ints2);
    }

    @Test
    void collectionExamples() {
        Tson tson = new Tson();
        Collection<Long> ints = Arrays.asList(1L, 2L, 3L, 4L, 5L);

        // Serialization
        String json = tson.toJson(ints); // ==> [1,2,3,4,5]
        assertEquals("[1,2,3,4,5]", json);

        // Deserialization
        Collection<Long> ints2 = tson.fromJson(json, List.class);
        // ==> ints2 is same as ints
        assertEquals(ints, ints2);
    }

    @Test
    void mapsExamples() {
        Tson tson = new Tson();
        Map<String, String> stringMap = new LinkedHashMap<>();
        stringMap.put("key", "value");
        stringMap.put(null, "null-entry");

        // Serialization
        String json = tson.toJson(stringMap); // ==> {"key":"value","null":"null-entry"}
        assertEquals("{\"key\":\"value\",\"null\":\"null-entry\"}", json);

        Map<Integer, Integer> intMap = new LinkedHashMap<>();
        intMap.put(2, 4);
        intMap.put(3, 6);

        // Serialization
        json = tson.toJson(intMap); // ==> {"2":4,"3":6}
        assertEquals("{\"2\":4,\"3\":6}", json);

        // Deserialization
        json = "{\"key\": \"value\"}";
        stringMap = tson.fromJson(json, Map.class);
        // ==> stringMap is {key=value}
        assertEquals(stringMap, Map.of("key", "value"));
    }
}
