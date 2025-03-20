package com.parser;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        JsonParser parser = JsonParserFactory.getParser();

        try {
            // 1. Parse simple JSON object
            String simpleJson = "{\"name\":\"John Doe\",\"age\":30,\"isStudent\":false}";
            Object simpleObject = parser.parse(simpleJson);
            System.out.println("1. Simple JSON object:");
            System.out.println(simpleObject);
            System.out.println();

            // 2. Parse JSON array
            String arrayJson = "[1, 2, 3, 4, 5]";
            Object array = parser.parse(arrayJson);
            System.out.println("2. JSON array:");
            System.out.println(array);
            System.out.println();

            // 3. Parse complex JSON object
            String complexJson = "{\"person\":{\"name\":\"Jane Smith\",\"age\":25},\"scores\":[90,85,92],\"address\":{\"street\":\"123 Main St\",\"city\":\"New York\"}}";
            Object complexObject = parser.parse(complexJson);
            System.out.println("3. Complex JSON object:");
            System.out.println(complexObject);
            System.out.println();

            // 4. Parse JSON to Map
            Map<String, Object> map = parser.parseToMap(complexJson);
            System.out.println("4. JSON to Map:");
            System.out.println(map);
            System.out.println();

            // 5. Parse JSON to custom class
            String personJson = "{\"name\":\"Alice Johnson\",\"age\":28}";
            Person person = parser.parseToClass(personJson, Person.class);
            System.out.println("5. JSON to custom class (Person):");
            System.out.println("Name: " + person.getName() + ", Age: " + person.getAge());
            System.out.println();

            // 6. Parse JSON with collections to custom class
            String personWithHobbiesJson = "{\"name\":\"Bob Williams\",\"age\":35,\"hobbies\":[\"reading\",\"swimming\",\"cycling\"]}";
            PersonWithHobbies personWithHobbies = parser.parseToClass(personWithHobbiesJson, PersonWithHobbies.class);
            System.out.println("6. JSON with collections to custom class (PersonWithHobbies):");
            System.out.println("Name: " + personWithHobbies.getName() + ", Age: " + personWithHobbies.getAge() + ", Hobbies: " + personWithHobbies.getHobbies());
            System.out.println();

            // 7. Convert object to JSON
            Person newPerson = new Person("Charlie Brown", 40);
            String newPersonJson = parser.toJson(newPerson);
            System.out.println("7. Object to JSON:");
            System.out.println(newPersonJson);
            System.out.println();

            // 8. Convert object with collections to JSON
            PersonWithHobbies newPersonWithHobbies = new PersonWithHobbies("David Clark", 45, Arrays.asList("gardening", "cooking", "traveling"));
            String newPersonWithHobbiesJson = parser.toJson(newPersonWithHobbies);
            System.out.println("8. Object with collections to JSON:");
            System.out.println(newPersonWithHobbiesJson);
            System.out.println();

            // 9. Parse JSON with null values
            String jsonWithNull = "{\"name\":\"Eve Davis\",\"age\":null,\"address\":null}";
            Map<String, Object> mapWithNull = parser.parseToMap(jsonWithNull);
            System.out.println("9. JSON with null values:");
            System.out.println(mapWithNull);
            System.out.println();

            // 10. Parse empty JSON object and array
            String emptyObject = "{}";
            String emptyArray = "[]";
            System.out.println("10. Empty JSON object and array:");
            System.out.println("Empty object: " + parser.parse(emptyObject));
            System.out.println("Empty array: " + parser.parse(emptyArray));

        } catch (JsonParseException e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
        }
    }

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

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
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

        public void setHobbies(List<String> hobbies) {
            this.hobbies = hobbies;
        }
    }
}