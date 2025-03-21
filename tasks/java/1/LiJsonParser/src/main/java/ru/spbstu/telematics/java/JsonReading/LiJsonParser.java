package ru.spbstu.telematics.java.JsonReading;
import ru.spbstu.telematics.java.Common.LiJsonException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Класс LiJsonParser для разбора json-строки и преобразования ее в объекты
public class LiJsonParser {
    private final JsonSplitToToken tokenSplitter; //экземпляр для получения токенов из строки

    public LiJsonParser(String jsonString) {
        this.tokenSplitter = new JsonSplitToToken(jsonString);
    }

    //Возможно этот метод вообще стоит убрать и сделать три public метода
    //метод для начальной проверки json-строки на формат: json-объект, json-массив и вызова соответствующего метода для парсинга
    public Object parseCommon() throws LiJsonException {
        String token = tokenSplitter.getNextToken();
        //если json-объект
        if (token.equals("{")) {
            return parseJsObjectToMap();
        }
        //если json-массив
        else if (token.equals("[")) {
            return parseJsArrayToListOfObjects();
        } else {
            throw new LiJsonException("Неправильный формат json: должен начинаться с '{' или '['");
        }
    }

    //метод для парсинга json-объекта в Map<String, Object>
    private Map<String, Object> parseJsObjectToMap() throws LiJsonException {
        Map<String, Object> map = new HashMap<>();
        while (true) {
            String token = tokenSplitter.getNextToken();
            if (token.equals("}")) {
                break;
            } else if (token.equals(",")) {
                continue;
            } else {
                String key = token; //получаем ключ
                tokenSplitter.getNextToken(); //пропускаем ':'
                String valueStr = tokenSplitter.getNextToken();
                Object value = parseValue(valueStr); //получаем значение
                map.put(key, value);
            }
        }
        return map;
    }

    //метод для парсинга json-массива в List<Object>
    private List<Object> parseJsArrayToListOfObjects() throws LiJsonException {
        List<Object> list = new ArrayList<>();
        while (true) {
            String token = tokenSplitter.getNextToken();
            if (token.equals("]")) break;
            if (token.equals(",")) continue;

            Object value = parseValue(token);
            list.add(value);
        }
        return list;
    }

    //метод для парсинга json-объекта (распарсенного в Map<String,Object>) в произвольный класс (по соответствию ключей и полей класса)
    public <T> T parseJsObjectToClass(Class<T> clazz) throws LiJsonException {
        Object result = parseCommon();
        //проверяем, что результат парсинга json - Map
        if (result instanceof Map) {
            return parseMapToClass((Map<String, Object>) result, clazz);
        }
        throw new LiJsonException("Результат парсинга не является Map, его нельзя сопоставить с пользовательским классом");
    }

    private <T> T parseMapToClass(Map<String, Object> map, Class<T> clazz) throws LiJsonException {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            //итерируемся по всем записям в map
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                //получаем поля класса с именем, соответствующим ключу
                Field field = clazz.getDeclaredField(entry.getKey());
                field.setAccessible(true);
                //устанавливаем в поле экземпляра класса значение из map для соотв-го ключа
                field.set(instance, entry.getValue());
            }
            return instance;
        } catch (Exception e) {
            throw new LiJsonException("Ошибка при сопоставлении json-объекта с классом");
        }
    }

    //метод для определения типа значения и вызова соответствующего метода для парсинга
    private Object parseValue(String token) throws LiJsonException {
        switch (token) {
            case "{":
                return parseJsObjectToMap();
            case "[":
                return parseJsArrayToListOfObjects();
            case "true":
            case "false":
                return Boolean.parseBoolean(token);
            case "null":
                return null;
            default:
                //пытаемся преобразовать токен в число
                try {
                    return makeNumber(token);
                } catch (LiJsonException e) {
                    //если не получилось, считаем, что это строка
                    return token;
                }
        }
    }

    //метод для преобразования строкового представления числа в объект Number
    private Number makeNumber(String token) throws LiJsonException {
        try {
            if (token.contains(".") || token.contains("e") || token.contains("E")) {
                return Double.parseDouble(token);
            } else {
                return Long.parseLong(token);
            }
        } catch (NumberFormatException e) {
            throw new LiJsonException("Неправильный формат числа: " + token);
        }
    }
}