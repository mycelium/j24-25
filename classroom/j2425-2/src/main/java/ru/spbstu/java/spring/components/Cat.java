package ru.spbstu.java.spring.components;

import org.springframework.beans.factory.annotation.Autowired;

public class Cat {
    private String name;
    private int age;
    private String color;

    private Tail tail;

    public Cat(String name, int age, String color) {
        this.name = name;
        this.age = age;
        this.color = color;
    }

    public Cat(String name, int age, String color, Tail tail) {
        this.name = name;
        this.age = age;
        this.color = color;
        this.tail = tail;
    }

    public Cat() {

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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Tail getTail() {
        return tail;
    }

    public void setTail(Tail tail) {
        this.tail = tail;
    }

    public String toJSON() {
        return "{ \"name\" : " + "\"" + name + "\"" + "," + "\"color\" : " + "\"" + color + "\"" + "," + "\"age\" : " + age + "}";
    }
}
