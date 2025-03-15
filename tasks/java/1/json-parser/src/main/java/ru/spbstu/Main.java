package main.java.ru.spbstu;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        JsonParser parser = new JsonParser();

        String json = "{\"name\":\"John\",\"age\":22,\"isStudent\":null,\"hobbies\":[\"reading\",\"coding\"],\"age2\":30, \"intArr\":[1,2]}";
        String json2 = "{address:[1, 2, { nestedKey: [true, false] }, [3,4]],\"array\":[1,\"12\",3,14,15,61], \"bool\":false}";
        Map<String, Object> map = parser.fromJsonToMap(json2);
        System.out.println(map);
    }
}
