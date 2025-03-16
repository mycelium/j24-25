package org.example;
import java.util.*;
import java.util.HashMap;
import java.util.Map;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        // 1) Пример конвертации JSON -> Map
        String json1 = "{\"name\":\"Alice\",\"age\":30,\"isActive\":true}";
        Map<String,Object> map = SimpleJson.parseToMap(json1);
        System.out.println("Map:  " + map.get("age"));

        // 2) Пример простой модели:
        //    class User { public String name; public int age; public boolean isActive; }
        //    Сериализация и десериализация
        String json2 = "{\"name\":\"Bob\",\"age\":25,\"isActive\":false}";
        User user = SimpleJson.parse(json2, User.class);
        System.out.println("User: name=" + user.name + ", age=" + user.age + ", isActive=" + user.isActive);

        String userJson = SimpleJson.toJson(user);
        System.out.println("User to JSON: " + userJson);

        // 3) Пример сложнее: списки, вложенные объекты
        String json3 = "{\"users\":[{\"name\":\"Kate\",\"age\":22,\"isActive\":true},{\"name\":\"Mike\",\"age\":40,\"isActive\":false}]}";
        Map<String, Object> map3 = SimpleJson.parseToMap(json3);
        System.out.println("Complex map: " + map3.get("users"));

        // 4) Сериализация сложного объекта
        List<User> list = new ArrayList<>();
        list.add(new User("Charlie", 21, true));
        list.add(new User("Diana", 32, true));
        Map<String, Object> complex = new HashMap<>();
        complex.put("team", "Developers");
        complex.put("members", list);
        String complexJson = SimpleJson.toJson(complex);
        System.out.println("Complex to JSON: " + complexJson);
    }
}

class User {
    public String name;
    public int age;
    public boolean isActive;

    public User() {}

    public User(String name, int age, boolean isActive) {
        this.name = name;
        this.age = age;
        this.isActive = isActive;
    }
}