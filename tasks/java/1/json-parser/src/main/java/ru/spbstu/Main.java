package main.java.ru.spbstu;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        JsonParser parser = new JsonParser();

        String json = "{\"name\":\"John\",\"age\":22,\"isStudent\":null,\"hobbies\":[\"reading\",\"coding\"],\"age2\":30, \"intArr\":[1,2]}";

        Map<String, Object> map = parser.fromJsonToMap(json);
        System.out.println(map);
    }
}

