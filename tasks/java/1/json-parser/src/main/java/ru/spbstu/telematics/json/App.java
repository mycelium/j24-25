package ru.spbstu.telematics.json;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import ru.spbstu.telematics.json.exceptions.WrongJsonStringFormatException;
import ru.spbstu.telematics.json.jsonreader.JsonReader;

/**
 * Hello world!
 */
public class App {
    public static class Person {
        private String name;
        private int age;
        private Address address;

        // Геттеры и сеттеры
        @Override
        public String toString() {
            return "Person{name='" + name + "', age=" + age + ", address=" + address + "}";
        }
    }

    static public class Address {
        private String city;
        private String street;

        // Геттеры и сеттеры
        @Override
        public String toString() {
            return "Address{city='" + city + "', street='" + street + "'}";
        }
    }

    public static void main(String[] args) {
        String json = "{\"name\":\"Иван\",\"age\":30,\"isStudent\":false," +
                "\"address\":{\"city\":\"Москва\",\"street\":\"Ленина\"}," +
                "\"hobbies\":[\"чтение\",\"спорт\"],\"metadata\":null}";
        Map<String, Object> jsonMap = new HashMap<>();
        try {
            jsonMap = JsonReader.fromJsonToMap(json);
        } catch (WrongJsonStringFormatException e) {
            e.printStackTrace();
        }
        for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + (entry.getValue() instanceof Integer));
        }

        String jsonToObject = "{\"name\": \"John\", \"age\": 30, \"address\": {\"city\": \"Moscow\", \"street\": \"Lenina\"}}";
        try {
            Person person = JsonReader.fromJsonNewObject(jsonToObject, Person.class);
            System.out.println(person);
        } catch (WrongJsonStringFormatException | NoSuchFieldException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }


        Person person = new Person();
        try {
            JsonReader.fromJsonToObject(jsonToObject, person);

            System.out.println(person);
        } catch (WrongJsonStringFormatException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
}
