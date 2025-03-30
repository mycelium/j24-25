package ru.spbstu.telematics.java;
import org.junit.Test;
import ru.spbstu.telematics.java.Common.LiJsonException;
import ru.spbstu.telematics.java.Common.LiJsonUser;
import ru.spbstu.telematics.java.JsonReading.LiJsonParser;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;

public class LiJsonParserTest {
    //проверка на парсинг простого json-объекта в map
    @Test
    public void testParseSimpleJsObject() throws LiJsonException {
        String json = "{\"name\": \"Din\", \"age\": 27, \"isFemale\": false}";
        LiJsonParser parser = new LiJsonParser(json);
        Object result = parser.parseCommon();

        assertTrue(result instanceof Map);
        Map<?, ?> map = (Map<?, ?>) result;
        assertEquals("Din", map.get("name"));
        assertEquals(27L, map.get("age"));
        assertEquals(false, map.get("isFemale"));
    }

    //проверка на парсинг json-объекта с вложенным json-объектом в map
    @Test
    public void testParseNestedJsObject() throws LiJsonException {
        String json = "{\"person\": {\"name\": \"Lara\", \"age\": 20}}";
        LiJsonParser parser = new LiJsonParser(json);
        Object result = parser.parseCommon();

        assertTrue(result instanceof Map);
        Map<?, ?> map = (Map<?, ?>) result;
        Map<?, ?> person = (Map<?, ?>) map.get("person");
        assertEquals("Lara", person.get("name"));
        assertEquals(20L, person.get("age"));
    }

    //проверка на парсинг json-массива в List<Object>
    @Test
    public void testParseJsArray() throws LiJsonException {
        String json = "[1, 2, 3, {\"key\": \"value\"}]";
        LiJsonParser parser = new LiJsonParser(json);
        Object result = parser.parseCommon();

        assertTrue(result instanceof List);
        List<?> list = (List<?>) result;
        assertEquals(1L, list.get(0));
        assertTrue(list.get(3) instanceof Map);
        Map<?, ?> map = (Map<?, ?>) list.get(3);
        assertEquals("value", map.get("key"));
    }

    //проверка на парсинг json-объекта с вложенным json-объектом и массивом в качестве ключа в map
    @Test
    public void testParseComplexJsStructure() throws LiJsonException {
        String json = "{\"numbers\": [1, 2, 3], \"info\": {\"active\": true, \"details\": null}}";
        LiJsonParser parser = new LiJsonParser(json);
        Object result = parser.parseCommon();

        assertTrue(result instanceof Map);
        Map<?, ?> map = (Map<?, ?>) result;
        List<?> numbers = (List<?>) map.get("numbers");
        Map<?, ?> info = (Map<?, ?>) map.get("info");
        assertEquals(1L, numbers.get(0));
        assertEquals(true, info.get("active"));
        assertNull(info.get("details"));
    }

    //проверка на парсинг json-объекта в пользовательский класс LiJsonUser
    @Test
    public void testParseJsToClass() throws LiJsonException {
        String json = "{"
                + "\"name\": \"Din\","
                + "\"surname\": \"Don\","
                + "\"age\": 27,"
                + "\"isOnline\": true,"
                + "\"favMovies\": [\"The Shawshank Redemption\", \"Fight Club\"]"
                + "}";

        LiJsonParser parser = new LiJsonParser(json);
        LiJsonUser user = parser.parseJsObjectToClass(LiJsonUser.class);

        assertEquals("Din", user.getName());
        assertEquals("Don", user.getSurname());
        assertEquals(27, user.getAge());
        assertTrue(user.isOnline());
        assertTrue(user.getFavMovies().contains("The Shawshank Redemption"));
        assertTrue(user.getFavMovies().contains("Fight Club"));
    }

    //проверка на парсинг объекта с наследованием
    @Test
    public void testParseToClassWithInheritance() throws LiJsonException {
        String json = "{" +
                "\"parentField\":\"parentValue\"," +
                "\"parentNumber\":1," +
                "\"childField\":\"childValue\"," +
                "\"childFlag\":true" +
                "}";

        LiJsonParser parser = new LiJsonParser(json);
        LiJsonUser.ChildClass result = parser.parseJsObjectToClass(LiJsonUser.ChildClass.class);

        assertEquals("parentValue", result.getParentField());
        assertEquals(1, result.getParentNumber());
        assertEquals("childValue", result.getChildField());
        assertTrue(result.isChildFlag());
    }
}