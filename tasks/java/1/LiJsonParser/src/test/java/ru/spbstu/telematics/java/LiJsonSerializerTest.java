package ru.spbstu.telematics.java;
import org.junit.Test;
import ru.spbstu.telematics.java.Common.LiJsonException;
import ru.spbstu.telematics.java.Common.LiJsonUser;
import ru.spbstu.telematics.java.JsonWriting.LiJsonSerializer;
import java.util.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LiJsonSerializerTest {
    //проверка на преобразование null
    @Test
    public void testSerializeNull() throws IllegalAccessException, LiJsonException {
        LiJsonSerializer serializer = new LiJsonSerializer();
        String result = serializer.serializeToJson(null);
        assertEquals("null", result);
    }

    //проверка на преобразование String
    @Test
    public void testSerializeString() throws IllegalAccessException, LiJsonException {
        LiJsonSerializer serializer = new LiJsonSerializer();
        String result = serializer.serializeToJson("Hello, World!");
        assertEquals("\"Hello, World!\"", result);
    }

    //проверка на преобразование числа
    @Test
    public void testSerializeNumber() throws IllegalAccessException, LiJsonException {
        LiJsonSerializer serializer = new LiJsonSerializer();
        String result = serializer.serializeToJson(404);
        assertEquals("404", result);
    }

    //проверка на преобразование boolean
    @Test
    public void testSerializeBoolean() throws IllegalAccessException, LiJsonException {
        LiJsonSerializer serializer = new LiJsonSerializer();
        String result = serializer.serializeToJson(true);
        assertEquals("true", result);
    }

    //проверка на преобразование Map
    @Test
    public void testSerializeMap() throws IllegalAccessException, LiJsonException {
        LiJsonSerializer serializer = new LiJsonSerializer();
        Map<String, Object> map = new HashMap<>();
        map.put("key1", 1);
        map.put("key2", "value2");
        String result = serializer.serializeToJson(map);
        assertEquals("{\"key1\":1,\"key2\":\"value2\"}", result);
    }

    //проверка на преобразование List
    @Test
    public void testSerializeList() throws IllegalAccessException, LiJsonException {
        LiJsonSerializer serializer = new LiJsonSerializer();
        List<Object> list = new ArrayList<>();
        list.add("element1");
        list.add(2);
        String result = serializer.serializeToJson(list);
        assertEquals("[\"element1\",2]", result);
    }

    //проверка на преобразование пользовательского класса
    @Test
    public void testSerializeObject() throws IllegalAccessException, LiJsonException {
        LiJsonSerializer serializer = new LiJsonSerializer();
        LiJsonUser user = new LiJsonUser();
        user.setName("Din");
        user.setSurname("Don");
        user.setAge(27);
        user.setOnline(true);
        user.setFavMovies(Arrays.asList("The Shawshank Redemption","Fight Club"));
        String result = serializer.serializeToJson(user);
        assertEquals("{"
                + "\"name\":\"Din\","
                + "\"surname\":\"Don\","
                + "\"age\":27,"
                + "\"isOnline\":true,"
                + "\"favMovies\":[\"The Shawshank Redemption\",\"Fight Club\"]"
                + "}", result);
    }

    //проверка на преобразование объекта с наследованием
    @Test
    public void testSerializeObjectWithInheritance() throws IllegalAccessException, LiJsonException {
        LiJsonSerializer serializer = new LiJsonSerializer();
        LiJsonUser.ChildClass child = new LiJsonUser.ChildClass();
        String result = serializer.serializeToJson(child);

        assertTrue("JSON должен содержать поле из родительского класса",
                result.contains("\"parentField\":\"parentValue\""));
        assertTrue("JSON должен содержать поле из родительского класса",
                result.contains("\"parentNumber\":1"));
        assertTrue("JSON должен содержать поле из дочернего класса",
                result.contains("\"childField\":\"childValue\""));
        assertTrue("JSON должен содержать поле из дочернего класса",
                result.contains("\"childFlag\":true"));
    }

}

