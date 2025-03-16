package main.java.ru.spbstu;

import java.util.Arrays;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        JsonParser parser = new JsonParser();

        String json = "{\"name\":\"John\",\"age\":22,\"isStudent\":null,\"hobbies\":[\"reading\",\"coding\"]}";
        String json2 = "{address:[1, 2, { nestedKey: [true, false] }, [3,4]],\"array\":[1,\"12\",3,14,15,61], \"bool\":false}";

        Map<String, Object> map = parser.fromJsonToMap(json2);
        System.out.println(map);
    }
}

class User {
    private String name;
    private Integer age;
    private Boolean isStudent;
    private String[] hobbies;

    @Override
    public String toString() {
        return "User{name='" + name + "', age=" + age + ", isStudent=" + isStudent + ", hobbies=" + Arrays.toString(hobbies) + "}";
    }
}