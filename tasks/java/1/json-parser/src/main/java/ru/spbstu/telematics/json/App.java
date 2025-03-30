package ru.spbstu.telematics.json;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import ru.spbstu.telematics.json.exceptions.WrongJsonStringFormatException;
import ru.spbstu.telematics.json.jsoninteraction.JsonInteractor;
import ru.spbstu.telematics.json.jsoninteraction.JsonReader;
import ru.spbstu.telematics.json.jsoninteraction.JsonWriter;

/**
 * Hello world!
 */
public class App {
    public static class Vehicle {
        private String material;
        private String color;
        public Vehicle(String material, String color) {
            this.material = material;
            this.color = color;
        }
    }
    public static class Car extends Vehicle {
        private String brand;
        private String model;

        // Конструктор без параметров
        public Car() {
            // Вызываем конструктор суперкласса с дефолтными значениями
            super("", "");
        }

        public Car(String material, String color, String brand, String model) {
            super(material, color);
            this.brand = brand;
            this.model = model;
        }

        @Override
        public String toString() {
            return "Car {Vehicle.material=" + super.material + ", Vehicle.color=" + super.color +
                    ", brand=" + brand + ", model=" + model + "}";
        }
    }

    static class Person {
        private String name;
        private int age;
        private Address address;
        private List<String> hobbies;  // Коллекция List
        private Set<String> skills;    // Коллекция Set

        // Конструктор без параметров (обязателен для рефлексии)
        public Person() {}

        public Person(String name, int age, Address address, List<String> hobbies, Set<String> skills) {
            this.name = name;
            this.age = age;
            this.address = address;
            this.hobbies = hobbies;
            this.skills = skills;
        }

        @Override
        public String toString() {
            return "Person{name='" + name + "', age=" + age +
                    ", address=" + address + ", hobbies=" + hobbies +
                    ", skills=" + skills + "}";
        }

        // Getters and setters (необходимы для сериализации)
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

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public List<String> getHobbies() {
            return hobbies;
        }

        public void setHobbies(List<String> hobbies) {
            this.hobbies = hobbies;
        }

        public Set<String> getSkills() {
            return skills;
        }

        public void setSkills(Set<String> skills) {
            this.skills = skills;
        }
    }

    static class Address {
        private String city;
        private String street;

        public Address() {}

        public Address(String city, String street) {
            this.city = city;
            this.street = street;
        }

        @Override
        public String toString() {
            return "Address{city='" + city + "', street='" + street + "'}";
        }

        // Getters and setters
        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }
    }

    public static void main(String[] args) {
        Person person = new Person(
                "John Doe",
                30,
                new Address("New York", "5th Avenue"),
                Arrays.asList("Reading", "Gaming", "Cycling"),  // List
                new HashSet<>(Set.of("Java", "Python", "SQL")) // Set
        );

        // Конвертируем в JSON
        String json = null;
        try {
            json = JsonWriter.fromObjectToJsonString(person);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        System.out.println("JSON: " + json);

        // Обратно из JSON в объект
        Person parsedPerson = null;
        try {
            parsedPerson = JsonReader.fromJsonNewObject(json, Person.class);
        } catch (WrongJsonStringFormatException | InstantiationException | IllegalAccessException |
                 NoSuchFieldException e) {
            e.printStackTrace();
        }
        System.out.println("Parsed Object: " + parsedPerson);

        try {
            System.out.println(JsonWriter.fromMapToJson(JsonReader.fromJsonToMap(json)));
        } catch (WrongJsonStringFormatException e) {
            e.printStackTrace();
        }

        Car car = new Car("steel", "white", "Tesla", "model X");
        // Конвертируем в JSON
        json = null;
        try {
            json = JsonWriter.fromObjectToJsonString(car);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        System.out.println("JSON: " + json);

        // Обратно из JSON в объект
        Car parsedCar = null;
        try {
             parsedCar = JsonReader.fromJsonNewObject(json, Car.class);
        } catch (WrongJsonStringFormatException | InstantiationException | IllegalAccessException |
                 NoSuchFieldException e) {
            e.printStackTrace();
        }
        System.out.println("Parsed Object: " + parsedCar);
    }
}
