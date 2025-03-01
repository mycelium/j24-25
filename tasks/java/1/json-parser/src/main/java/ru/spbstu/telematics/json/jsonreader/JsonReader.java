package ru.spbstu.telematics.json.jsonreader;

import ru.spbstu.telematics.json.exceptions.WrongJsonStringFormatException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonReader {
    static public Map<String, Object> fromJsonToMap(String json) throws WrongJsonStringFormatException {
        Map<String, Object> result = new HashMap<String, Object>();
        if (json == null) {
            throw new NullPointerException("JSON is null");
        }
        if (json.isEmpty()) {
            return result;
        }
        if (!json.strip().startsWith("{")) {
            throw new WrongJsonStringFormatException("JSON does not have open bracket ({)");
        }
        if (!json.strip().endsWith("}")) {
            throw new WrongJsonStringFormatException("JSON does not have end bracket ({)");
        }
        if (json.chars().filter(ch -> ch == '{').count() != json.chars().filter(ch -> ch == '}').count()) {
            throw new WrongJsonStringFormatException("JSON contains different number of brackets");
        }
        return readJson(json);
    }

    private static List<String> splitJson(String json) throws WrongJsonStringFormatException {
        List<String> result = new ArrayList<>();
        int depth = 0;
        StringBuilder current = new StringBuilder();
        for (char ch : json.toCharArray()) {
            if (ch == '{' || ch == '[') {
                depth++;
            } else if (ch == '}' || ch == ']') {
                depth--;
            } else if (ch == ',' && depth == 0) {
                result.add(current.toString());
                current.setLength(0);
                continue;
            }
            current.append(ch);
        }
        if (!current.isEmpty()) {
            result.add(current.toString());
        }
        return result;
    }

    private static Object parseValue(String value) throws WrongJsonStringFormatException {
        if (value.startsWith("{") && value.endsWith("}")) {
            // Вложенный объект
            return readJson(value);
        } else if (value.startsWith("[") && value.endsWith("]")) {
            // Массив
            return parseArray(value);
        } else if (value.equals("true") || value.equals("false")) {
            // Булево значение
            return Boolean.valueOf(value);
        } else if (value.equals("null")) {
            // null
            return null;
        } else if (value.startsWith("\"") && value.endsWith("\"")) {
            // Строка
            return value.substring(1, value.length() - 1);
        } else if (value.matches("-?\\d+(\\.\\d+)?")) {
            // Число (целое или с плавающей точкой)
            if (value.contains("."))
            {
                return Double.valueOf(value);
            }
            return Integer.valueOf(value);
        } else {
            throw new WrongJsonStringFormatException("Unknown value type: " + value);
        }
    }

    private static List<Object> parseArray(String array) throws WrongJsonStringFormatException {
        List<Object> result = new ArrayList<>();
        // Убираем квадратные скобки []
        array = array.substring(1, array.length() - 1).strip();
        if (array.isEmpty()) {
            return result;
        }
        // Разделяем массив на элементы
        List<String> elements = splitJson(array);
        for (String element : elements) {
            result.add(parseValue(element.strip()));
        }
        return result;
    }

    private static Map<String, Object> readJson(String json) throws WrongJsonStringFormatException {
        Map<String, Object> result = new HashMap<>();
        json = json.substring(1, json.length() - 1).strip();

        // Разделяем JSON на пары ключ-значение
        List<String> keyValuePairs = splitJson(json);
        for (String pair : keyValuePairs) {
            String[] keyValue = pair.split(":", 2);
            if (keyValue.length != 2) {
                throw new WrongJsonStringFormatException("Invalid key-value pair: " + pair);
            }
            // Убираем кавычки у ключа
            String key = keyValue[0].strip().replaceAll("^\"|\"$", "");
            String value = keyValue[1].strip();
            result.put(key, parseValue(value));
        }
        return result;
    }
}
