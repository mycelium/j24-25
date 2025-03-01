package ru.spbstu.telematics.json;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.spbstu.telematics.json.exceptions.WrongJsonStringFormatException;
import ru.spbstu.telematics.json.jsonreader.JsonReader;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JsonReaderTest {

    private static String correctJsonAllTypes;

    @BeforeAll
    static void setUp() {
        correctJsonAllTypes = "{\"name\":\"Иван\",\"age\":30,\"isStudent\":false," +
                "\"address\":{\"city\":\"Москва\",\"street\":\"Ленина\"}," +
                "\"hobbies\":[\"чтение\",\"спорт\"],\"metadata\":null}";
    }

    @Test
    void testOnCorrectJsonAllTypes() throws WrongJsonStringFormatException {
        Map<String, Object> result = JsonReader.fromJsonToMap(correctJsonAllTypes);

        assertEquals(6, result.size());
        assertInstanceOf(String.class, result.get("name"));
        assertInstanceOf(Integer.class, result.get("age"));
        assertInstanceOf(Boolean.class, result.get("isStudent"));
        assertInstanceOf(List.class, result.get("hobbies"));
        assertInstanceOf(Map.class, result.get("address"));
        assertNull(result.get("metadata"));
    }
}