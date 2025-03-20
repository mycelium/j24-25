package com.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class JsonParserTest {

    private JsonParser parser;

    @BeforeEach
    void setUp() {
        parser = JsonParserFactory.getParser();
    }

    @Test
    void testParseSimpleObject() throws JsonParseException {
        String json = "{\"name\":\"John\",\"age\":30}";
        Object result = parser.parse(json);

        assertTrue(result instanceof Map);
        Map<String, Object> map = (Map<String, Object>) result;
        assertEquals("John", map.get("name"));
        assertEquals(30L, map.get("age"));
    }

    @Test
    void testParseNestedObject() throws JsonParseException {
        String json = "{\"person\":{\"name\":\"John\",\"age\":30}}";
        Object result = parser.parse(json);

        assertTrue(result instanceof Map);
        Map<String, Object> map = (Map<String, Object>) result;
        assertTrue(map.get("person") instanceof Map);
        Map<String, Object> person = (Map<String, Object>) map.get("person");
        assertEquals("John", person.get("name"));
        assertEquals(30L, person.get("age"));
    }

    @Test
    void testParseArray() throws JsonParseException {
        String json = "[1,2,3,4,5]";
        Object result = parser.parse(json);

        assertTrue(result instanceof List);
        List<Object> list = (List<Object>) result;
        assertEquals(Arrays.asList(1L, 2L, 3L, 4L, 5L), list);
    }

    @Test
    void testParseComplexObject() throws JsonParseException {
        String json = "{\"name\":\"John\",\"age\":30,\"isStudent\":false,\"grades\":[85,90,78],\"address\":{\"street\":\"123 Main St\",\"city\":\"New York\"}}";
        Object result = parser.parse(json);

        assertTrue(result instanceof Map);
        Map<String, Object> map = (Map<String, Object>) result;
        assertEquals("John", map.get("name"));
        assertEquals(30L, map.get("age"));
        assertEquals(false, map.get("isStudent"));
        assertTrue(map.get("grades") instanceof List);
        assertEquals(Arrays.asList(85L, 90L, 78L), map.get("grades"));
        assertTrue(map.get("address") instanceof Map);
        Map<String, Object> address = (Map<String, Object>) map.get("address");
        assertEquals("123 Main St", address.get("street"));
        assertEquals("New York", address.get("city"));
    }

    @Test
    void testParseToMap() throws JsonParseException {
        String json = "{\"name\":\"John\",\"age\":30}";
        Map<String, Object> result = parser.parseToMap(json);

        assertEquals("John", result.get("name"));
        assertEquals(30L, result.get("age"));
    }

    @Test
    void testParseToClass() throws JsonParseException {
        String json = "{\"name\":\"John\",\"age\":30}";
        Person result = parser.parseToClass(json, Person.class);

        assertEquals("John", result.getName());
        assertEquals(30, result.getAge());
    }

    @Test
    void testToJson() throws JsonParseException {
        Person person = new Person("John", 30);
        String result = parser.toJson(person);

        assertTrue(result.contains("\"name\":\"John\""));
        assertTrue(result.contains("\"age\":30"));
    }

    @Test
    void testParseNull() throws JsonParseException {
        String json = "{\"name\":null}";
        Map<String, Object> result = parser.parseToMap(json);

        assertTrue(result.containsKey("name"));
        assertNull(result.get("name"));
    }

    @Test
    void testParseEmptyObject() throws JsonParseException {
        String json = "{}";
        Map<String, Object> result = parser.parseToMap(json);

        assertTrue(result.isEmpty());
    }

    @Test
    void testParseEmptyArray() throws JsonParseException {
        String json = "[]";
        List<Object> result = (List<Object>) parser.parse(json);

        assertTrue(result.isEmpty());
    }

    @Test
    void testParseObjectWithArrays() throws JsonParseException {
        String json = "{\"numbers\":[1,2,3],\"strings\":[\"a\",\"b\",\"c\"]}";
        Map<String, Object> result = parser.parseToMap(json);

        assertEquals(Arrays.asList(1L, 2L, 3L), result.get("numbers"));
        assertEquals(Arrays.asList("a", "b", "c"), result.get("strings"));
    }

    @Test
    void testParseObjectWithNestedArrays() throws JsonParseException {
        String json = "{\"matrix\":[[1,2],[3,4]]}";
        Map<String, Object> result = parser.parseToMap(json);

        List<List<Object>> matrix = (List<List<Object>>) result.get("matrix");
        assertEquals(Arrays.asList(1L, 2L), matrix.get(0));
        assertEquals(Arrays.asList(3L, 4L), matrix.get(1));
    }

    @Test
    void testParseToClassWithCollections() throws JsonParseException {
        String json = "{\"name\":\"John\",\"age\":30,\"hobbies\":[\"reading\",\"swimming\"]}";
        PersonWithHobbies result = parser.parseToClass(json, PersonWithHobbies.class);

        assertEquals("John", result.getName());
        assertEquals(30, result.getAge());
        assertEquals(Arrays.asList("reading", "swimming"), result.getHobbies());
    }

    @Test
    void testToJsonWithCollections() throws JsonParseException {
        PersonWithHobbies person = new PersonWithHobbies("John", 30, Arrays.asList("reading", "swimming"));
        String result = parser.toJson(person);

        assertTrue(result.contains("\"name\":\"John\""));
        assertTrue(result.contains("\"age\":30"));
        assertTrue(result.contains("\"hobbies\":[\"reading\",\"swimming\"]"));
    }

    // Helper classes for testing

    private static class Person {
        private String name;
        private int age;

        public Person() {
        }

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }

    private static class PersonWithHobbies extends Person {
        private List<String> hobbies;

        public PersonWithHobbies() {
        }

        public PersonWithHobbies(String name, int age, List<String> hobbies) {
            super(name, age);
            this.hobbies = hobbies;
        }

        public List<String> getHobbies() {
            return hobbies;
        }
    }
}