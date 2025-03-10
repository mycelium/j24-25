package main.java.ru.spbstu;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        JsonParser parser = new JsonParser();

        String json = "{\"name\":\"Mark\",\"age\":22,\"isStudent\":true}";

        Map<String, Object> map = parser.fromJsonToMap(json);
        System.out.println(map);
    }
}

