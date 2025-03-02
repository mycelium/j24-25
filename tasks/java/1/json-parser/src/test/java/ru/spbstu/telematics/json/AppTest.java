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

    private static String correctJsonAllTypes = "{\"name\":\"Иван\",\"age\":30,\"isStudent\":false," +
            "\"address\":{\"city\":\"Москва\",\"street\":\"Ленина\"}," +
            "\"hobbies\":[\"чтение\",\"спорт\"],\"metadata\":null}";
    private static String correctEmptyJson = "{}";
    private static String incorrectFormattedJson = "\"name\": \"John Doe\"";
    private static String nullJson = null;
    private static String jsonToObject = "{\"name\": \"John Doe\", \"age\": 30," +
            " \"address\": {\"city\": \"Saint-Petersburg\", \"street\": \"Lenina\"}}";
    private static String jsonWithArray = "{\"animals\": [\"dogs\", \"cats\"]}";

    public static class Person {
        private String name;
        private int age;
        private Address address;

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public Address getAddress() {
            return address;
        }
    }

    static public class Address {
        private String city;
        private String street;

        public String getCity() {
            return city;
        }

        public String getStreet() {
            return street;
        }
    }

    public static class AnimalsCollection {
        private List<String> animals;

        public List<String> getAnimals() {
            return animals;
        }
    }

    @Test
    void testMapOnCorrectJsonAllTypes() throws WrongJsonStringFormatException {
        Map<String, Object> result = JsonReader.fromJsonToMap(correctJsonAllTypes);

        assertEquals(6, result.size());
        assertInstanceOf(String.class, result.get("name"));
        assertInstanceOf(Integer.class, result.get("age"));
        assertInstanceOf(Boolean.class, result.get("isStudent"));
        assertInstanceOf(List.class, result.get("hobbies"));
        assertInstanceOf(Map.class, result.get("address"));
        assertNull(result.get("metadata"));
    }

    @Test
    void testMapOnCorrectEmptyJson() throws WrongJsonStringFormatException {
        Map<String, Object> result = JsonReader.fromJsonToMap(correctEmptyJson);

        assertEquals(0, result.size());
    }

    @Test
    void testMapOnIncorrectFormattedJson() throws WrongJsonStringFormatException {
        WrongJsonStringFormatException exception = assertThrows(WrongJsonStringFormatException.class, () -> {
            Map<String, Object> result = JsonReader.fromJsonToMap(incorrectFormattedJson);
        });

        assertEquals("JSON does not have open bracket ({)", exception.getMessage());
    }

    @Test
    void testMapOnNullJson() throws WrongJsonStringFormatException {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            Map<String, Object> result = JsonReader.fromJsonToMap(nullJson);
        });

        assertEquals("JSON is null", exception.getMessage());
    }

    @Test
    void testJsonToCorrectNewObject() throws WrongJsonStringFormatException {
        Person person;
        try {
            person = JsonReader.fromJsonNewObject(jsonToObject, Person.class);
            assertEquals("John Doe", person.getName());
            assertEquals(30, person.getAge());
            assertEquals("Saint-Petersburg", person.getAddress().getCity());
            assertEquals("Lenina", person.getAddress().getStreet());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testJsonWithArrayToNewObject() throws WrongJsonStringFormatException {
        AnimalsCollection animalsCollection;

        try {
            animalsCollection = JsonReader.fromJsonNewObject(jsonWithArray, AnimalsCollection.class);
            assertEquals("dogs", animalsCollection.getAnimals().get(0));
            assertEquals("cats", animalsCollection.getAnimals().get(1));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}