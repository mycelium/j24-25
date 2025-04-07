package ru.spbstu.java.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.spbstu.java.spring.components.Cat;

public class Main {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("cats.xml");

        Cat black = context.getBean("blackCat", Cat.class);
        System.out.println(black.getName());
    }
}
