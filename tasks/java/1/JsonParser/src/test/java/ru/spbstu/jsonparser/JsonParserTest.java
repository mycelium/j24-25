package ru.spbstu.jsonparser;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class JsonParserTest {

    @Test
    void testFromJsonToMapSimple() {
        String json = "{\"name\":\"Mark\",\"age\":22}";
        Map<String, Object> map = JsonParser.fromJsonToMap(json);
        assertEquals("Mark", map.get("name"));
        assertEquals(22, map.get("age"));
    }

    @Test
    void testFromJsonToMapNested() {
        String json = "{\"person\":{\"name\":\"Mark\",\"age\":22}}";
        Map<String, Object> map = JsonParser.fromJsonToMap(json);
        assertInstanceOf(Map.class, map.get("person"));
        Map<String, Object> person = (Map<String, Object>) map.get("person");
        assertEquals("Mark", person.get("name"));
        assertEquals(22, person.get("age"));
    }

    @Test
    void testFromJsonToMapWithArray() {
        String json = "{\"skills\":[\"Teamwork\",\"Leadership\"]}";
        Map<String, Object> map = JsonParser.fromJsonToMap(json);
        assertInstanceOf(List.class, map.get("skills"));
        List<String> skills = (List<String>) map.get("skills");
        assertEquals(Arrays.asList("Teamwork", "Leadership"), skills);
    }

    @Test
    void testFromJsonToClassSimple() {
        String json = "{\"name\":\"Mark\",\"age\":22,\"isStudent\":false}";
        User user = JsonParser.fromJsonToClass(json, User.class);
        assertEquals("Mark", user.getName());
        assertEquals(22, user.getAge());
        assertFalse(user.getIsStudent());
    }

    @Test
    void testFromJsonToClassWithArray() {
        String json = "{\"name\":\"Mark\",\"hobbies\":[\"reading\",\"coding\"]}";
        User user = JsonParser.fromJsonToClass(json, User.class);
        assertArrayEquals(new String[]{"reading", "coding"}, user.getHobbies());
    }

    @Test
    void testFromJsonToClassWithCollection() {
        String json = "{\"name\":\"Mark\",\"skills\":[\"Teamwork\",\"Leadership\"]}";
        User user = JsonParser.fromJsonToClass(json, User.class);
        assertEquals(new HashSet<>(Arrays.asList("Teamwork", "Leadership")), user.getSkills());
    }

    @Test
    void testFromObjToJsonSimple() {
        User user = new User();
        user.setName("Mark");
        user.setAge(22);
        String json = JsonParser.fromObjToJson(user);
        assertTrue(json.contains("\"name\":\"Mark\""));
        assertTrue(json.contains("\"age\":22"));
    }

    @Test
    void testFromObjToJsonWithArray() {
        User user = new User();
        user.setHobbies(new String[]{"reading", "coding"});
        String json = JsonParser.fromObjToJson(user);
        assertTrue(json.contains("\"hobbies\":[\"reading\",\"coding\"]"));
    }

    @Test
    void testFromObjToJsonWithCollection() {
        User user = new User();
        user.setSkills(new HashSet<>(Arrays.asList("Teamwork", "Leadership")));
        String json = JsonParser.fromObjToJson(user);
        assertTrue(json.contains("\"skills\":[\"Leadership\",\"Teamwork\"]"));
    }

    @Test
    void testComplexJson() {
        String json = "{\"name\":\"Mark\",\"age\":22,\"isStudent\":false,\"hobbies\":[\"reading\",\"coding\"],\"skills\":[\"Teamwork\",\"Leadership\"],\"address\":{\"city\":\"Saint Petersburg\",\"region number\":\"78\"}}";
        Map<String, Object> map = JsonParser.fromJsonToMap(json);
        assertEquals("Mark", map.get("name"));
        assertEquals(22, map.get("age"));
        assertFalse((Boolean) map.get("isStudent"));
        assertEquals(Arrays.asList("reading", "coding"), map.get("hobbies"));
        assertEquals(Arrays.asList("Teamwork", "Leadership"), map.get("skills"));
        assertInstanceOf(Map.class, map.get("address"));
        Map<String, Object> address = (Map<String, Object>) map.get("address");
        assertEquals("Saint Petersburg", address.get("city"));
        assertEquals("78", address.get("region number"));
    }

    @Test
    void testInheritedFieldsSerialization() {
        ExtendedUser extendedUser = new ExtendedUser();
        extendedUser.setName("Mark");
        extendedUser.setAge(22);
        extendedUser.setDepartment("IT");

        String json = JsonParser.fromObjToJson(extendedUser);

        assertTrue(json.contains("\"name\":\"Mark\""));
        assertTrue(json.contains("\"age\":22"));
        assertTrue(json.contains("\"department\":\"IT\""));
    }

    @Test
    void testInheritedFieldsDeserialization() {
        String json = "{\"name\":\"Mark\",\"age\":22,\"department\":\"IT\"}";

        ExtendedUser extendedUser = JsonParser.fromJsonToClass(json, ExtendedUser.class);

        assertEquals("Mark", extendedUser.getName());
        assertEquals(22, extendedUser.getAge());
        assertEquals("IT", extendedUser.getDepartment());
    }
}