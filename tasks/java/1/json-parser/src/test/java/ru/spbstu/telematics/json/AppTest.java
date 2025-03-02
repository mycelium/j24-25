package ru.spbstu.telematics.json;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.spbstu.telematics.json.exceptions.WrongJsonStringFormatException;
import ru.spbstu.telematics.json.jsonreader.JsonReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
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

    @TempDir
    Path tempDir;

    public static class Person {
        private String name;
        private int age;
        private Address address;

        public void setName(String personName) {
            this.name = personName;
        }

        public void setAge(int personAge) {
            this.age = personAge;
        }

        public void setAddress(Address personAddress) {
            this.address = personAddress;
        }

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

        public void setCity(String city) {
            this.city = city;
        }

        public void setStreet(String street) {
            this.street = street;
        }

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
        assertThrows(WrongJsonStringFormatException.class, () -> {
            Map<String, Object> result = JsonReader.fromJsonToMap(incorrectFormattedJson);
        });
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
        } catch (NoSuchFieldException | IllegalAccessException | InstantiationException e) {
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
        } catch (NoSuchFieldException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testJsonToExistingObject() throws WrongJsonStringFormatException {
        Person person = new Person();
        person.setName("Ivan");
        person.setAge(27);
        Address personAddress = new Address();
        personAddress.setCity("Moscow");
        personAddress.setStreet("Central");
        person.setAddress(personAddress);

        try {
            JsonReader.fromJsonToObject(jsonToObject, person);

            assertEquals("John Doe", person.getName());
            assertEquals(30, person.getAge());
            assertEquals("Saint-Petersburg", person.getAddress().getCity());
            assertEquals("Lenina", person.getAddress().getStreet());
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException |
                 InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testFromJsonToMapValidJsonFile() throws WrongJsonStringFormatException, IOException {
        // Создаем временный файл с корректным JSON
        File jsonFile = tempDir.resolve("test.json").toFile();
        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write("{\"name\": \"John\", \"age\": 30}");
        }

        Map<String, Object> result = JsonReader.fromJsonToMap(jsonFile);

        assertNotNull(result);
        assertEquals("John", result.get("name"));
        assertEquals(30, result.get("age"));
    }

    @Test
    void testFromJsonToMapFileDoesNotExist() {
        // Создаем путь к несуществующему файлу
        File nonExistentFile = tempDir.resolve("nonexistent.json").toFile();

        // Проверяем, что метод выбрасывает FileNotFoundException
        assertThrows(FileNotFoundException.class, () -> JsonReader.fromJsonToMap(nonExistentFile));
    }

    @Test
    void testFromJsonToMapNullFile() {
        File jsonFile = null;
        assertThrows(NullPointerException.class, () -> JsonReader.fromJsonToMap(jsonFile));
    }

    @Test
    void testFromJsonToMapInvalidJsonFile() throws Exception {
        // Создаем временный файл с некорректным JSON
        File jsonFile = tempDir.resolve("invalid.json").toFile();
        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write("{\"name\": \"John\", \"age\": }"); // Некорректный JSON
        }

        // Проверяем, что метод выбрасывает WrongJsonStringFormatException
        assertThrows(WrongJsonStringFormatException.class, () -> JsonReader.fromJsonToMap(jsonFile));
    }
}