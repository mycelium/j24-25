package ru.spbstu.telematics.json;

import java.util.HashMap;
import java.util.Map;

import ru.spbstu.telematics.json.exceptions.WrongJsonStringFormatException;
import ru.spbstu.telematics.json.jsonreader.JsonReader;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        String json = "\"name\":\"Иван\",\"age\":30,\"isStudent\":false," +
                "\"address\":{\"city\":\"Москва\",\"street\":\"Ленина\"}," +
                "\"hobbies\":[\"чтение\",\"спорт\"],\"metadata\":null}";
        Map<String, Object> jsonMap = new HashMap<>();
        try {
            jsonMap = JsonReader.fromJsonToMap(json);
        } catch (WrongJsonStringFormatException e) {
            e.printStackTrace();
        }
        System.out.println(jsonMap);
    }
}
