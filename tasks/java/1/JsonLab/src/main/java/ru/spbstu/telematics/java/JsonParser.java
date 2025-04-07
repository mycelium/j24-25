package ru.spbstu.telematics.java;

import ru.spbstu.telematics.java.exceptions.*;
import ru.spbstu.telematics.java.serializer.JsonSerializer;

import java.util.*;


import static ru.spbstu.telematics.java.mapper.Mapper.setFieldsInClass;
import static ru.spbstu.telematics.java.serializer.JsonLexer.*;
import static ru.spbstu.telematics.java.utils.CollectionUtils.convertListToArray;
import static ru.spbstu.telematics.java.utils.TypeUtils.isSimpleValueType;


public class JsonParser {

    public static <T> T parseStringToClass(String input, Class<T> targetClass) throws JsonException,
            IllegalArgumentException{
        try{
            Objects.requireNonNull(input, "Input JSON string cannot be null");
            Objects.requireNonNull(targetClass, "Target class cannot be null");
        }catch (NullPointerException e) {
            throw new IllegalArgumentException(e);
        }

        String trimmedInput = input.strip();
        try {
            if (trimmedInput.startsWith("[")) {
                // Обработка массивов
                if (targetClass.isArray()) {
                    List<Object> list = parseStringToList(trimmedInput);
                    return convertListToArray(list, targetClass.getComponentType());
                }
                // Обработка коллекций
                else if (Iterable.class.isAssignableFrom(targetClass)) {
                    return (T) parseStringToList(trimmedInput);
                }
                throw new JsonException("Target class " + targetClass.getName() +
                        " is not an array or Iterable");
            }
            //Обработка объектов
            if (trimmedInput.startsWith("{")) {
                Map<String, Object> parsedMap = parseStringToMap(trimmedInput);
                return setFieldsInClass(parsedMap, targetClass);
            }
            // Обработка примитивных значений
            if (isSimpleValueType(targetClass)) {
                return parseSimpleValue(trimmedInput, targetClass);
            }
            throw new JsonException("Invalid JSON format for target type " +
                    targetClass.getName());
        } catch (JsonException e) {
            throw e;
        } catch (Exception e) {
            throw new JsonException("Parsing failed");
        }
    }
    static Object parseValue(String valueStr) throws JsonException {
        if (valueStr == null) return null;
        valueStr = valueStr.trim();

        // Обработка null
        if (valueStr.equalsIgnoreCase("null")) {
            return null;
        }

        // Обработка вложенных структур
        if (valueStr.startsWith("{")) {
            return parseStringToMap(valueStr);
        }
        if (valueStr.startsWith("[")) {
            return parseStringToList(valueStr);
        }

        // Обработка boolean
        if (isBoolean(valueStr)) {
            return Boolean.parseBoolean(valueStr.toLowerCase());
        }

        // Обработка строк (только в кавычках)
        if (valueStr.startsWith("\"") && valueStr.endsWith("\"")) {
            return valueStr.substring(1, valueStr.length() - 1);
        }

        // Обработка чисел
        return parseNumber(valueStr);
    }



    private static <T> T parseSimpleValue(String value, Class<T> targetType)
            throws JsonException {
        Object parsed = parseValue(value);
        // Специальная обработка для enum
        if (targetType.isEnum()) {
            try {
                // Удаляем кавычки если они есть
                String enumValue = value.startsWith("\"") && value.endsWith("\"")
                        ? value.substring(1, value.length() - 1)
                        : value;
                return (T) Enum.valueOf((Class<Enum>) targetType, enumValue);
            } catch (IllegalArgumentException e) {
                throw new JsonException("Invalid enum value: " + value);
            }
        }
        if (targetType.isInstance(parsed)) {
            return targetType.cast(parsed);
        }
        throw new JsonException("Value " + parsed + " cannot be converted to " +
                targetType.getName());
    }
    public static Map<String, Object> parseStringToMap(String input) throws JsonException, NullPointerException {
        if (input == null) throw new NullPointerException();
        if(!input.startsWith("{") || !input.endsWith("}"))
            throw new InvalidJsonStringException("JSON object start syntax violation");


        HashMap<String, Object> resultMap = new HashMap<>();
        input = input.substring(1, input.length() - 1).strip();
        if (input.isEmpty()) { // если только скобочки.
            return resultMap;
        }

        //основной парсинг
        String[] listOfPairs = splitJsonPairs(input);

        for (String pairString : listOfPairs) {
            String[] keyValuePair = splitKeyValue(pairString);
            if (!keyValuePair[0].startsWith("\"")) throw new InvalidJsonStringException("Key provided without quotes");
            String key = keyValuePair[0].replaceAll("^\"|\"$", "").strip();
            String valueStr = keyValuePair[1].strip();

            Object value = parseValue(valueStr);
            resultMap.put(key, value);
        }
        return resultMap;
    }

    public static List<Object> parseStringToList(String input) throws JsonException
    {
            if (!input.startsWith("[") || !input.endsWith("]")) {
                throw new InvalidJsonStringException("JSON array syntax violation");
            }

            String content = input.substring(1, input.length() - 1).strip();
            if (content.isEmpty()) {
                return new ArrayList<Object>();
            }

            List<Object> result = new ArrayList<>();
            String[] elements = splitJsonPairs(content);

            for (String element : elements) {
                result.add(parseValue(element.strip()));
            }

            return result;
    }



    public static String parseToString(Object sourceObject) {
        return JsonSerializer.parseToString(sourceObject);
    }
}


