package ru.spbstu.telematics.json.jsonreader;

import ru.spbstu.telematics.json.exceptions.WrongJsonStringFormatException;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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

    static public <T> T fromJsonNewObject(String json, Class<T> filledClass) throws
            WrongJsonStringFormatException,
            NoSuchFieldException,
            InstantiationException,
            IllegalAccessException
    {
        // Преобразуем JSON в Map
        Map<String, Object> jsonMap = fromJsonToMap(json);

        try {
            // Создаем экземпляр целевого класса
            T object;
            Constructor<T> ctr;
            try {
                ctr = filledClass.getDeclaredConstructor();
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("The constructor of "
                        + filledClass + " is not available");
            }
            try {
                object = ctr.newInstance();
            } catch (InvocationTargetException e) {
                throw new RuntimeException("The object of "
                        + filledClass + "cannot be instantiated");
            }

            // Заполняем поля объекта
            for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
                String fieldName = entry.getKey();
                Object value = entry.getValue();

                // Находим поле в классе
                Field field;
                try {
                    field = filledClass.getDeclaredField(fieldName);
                } catch (NoSuchFieldException e) {
                    throw new NoSuchFieldException("The class " + filledClass + " does not have field " + fieldName);
                }

                // Делаем поле доступным (если оно private)
                field.setAccessible(true);

                // Устанавливаем значение поля
                if (value instanceof Map) {
                    // Если значение — это вложенный объект, рекурсивно создаем его
                    value = fromJsonNewObject(mapToJson((Map<String, Object>) value), field.getType());
                }

                field.set(object, value);
            }

            return object;
        } catch (InstantiationException e) {
            throw new InstantiationException("The object of " + filledClass + " cannot be instantiated");
        } catch (IllegalAccessException e) {
            throw new IllegalAccessException(
                    "Cannot get access to the definition of the class " + filledClass
                            + ", its field, method or constructor; caused by " + e.getMessage()
            );
        }
    }

    private static String mapToJson(Map<String, Object> map) {
        StringBuilder json = new StringBuilder("{");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (json.length() > 1) {
                json.append(",");
            }
            json.append("\"").append(entry.getKey()).append("\":");
            Object value = entry.getValue();
            if (value instanceof String) {
                json.append("\"").append(value).append("\"");
            } else {
                json.append(value);
            }
        }
        json.append("}");
        return json.toString();
    }

    static public void fromJsonToObject(String json, Object targetObject) throws
            WrongJsonStringFormatException,
            NoSuchFieldException,
            IllegalAccessException,
            NoSuchMethodException,
            InvocationTargetException,
            InstantiationException
    {
        // Преобразуем JSON в Map
        Map<String, Object> jsonMap = fromJsonToMap(json);

        // Получаем класс объекта
        Class<?> targetClass = targetObject.getClass();

        // Заполняем поля объекта
        for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();

            // Находим поле в классе
            Field field;
            try {
                field = targetClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                throw new NoSuchFieldException("The class " + targetClass + " does not have field " + fieldName);
            }

            // Делаем поле доступным (если оно private)
            field.setAccessible(true);

            // Устанавливаем значение поля
            if (value instanceof Map) {
                // Если значение — это вложенный объект, рекурсивно заполняем его
                Object nestedObject = field.get(targetObject);
                if (nestedObject == null) {
                    // Если вложенный объект не инициализирован, создаем его
                    nestedObject = field.getType().getDeclaredConstructor().newInstance();
                    field.set(targetObject, nestedObject);
                }
                fromJsonToObject(mapToJson((Map<String, Object>) value), nestedObject);
            } else {
                field.set(targetObject, value);
            }
        }
    }

    static public Map<String, Object> fromJsonToMap(File jsonFile) throws
            WrongJsonStringFormatException,
            IOException
    {
        if (jsonFile == null) {
            throw new NullPointerException("The file is null");
        }
        if (!jsonFile.exists()) {
            throw new FileNotFoundException("The file does not exist");
        }

        Map<String, Object> result = null;
        InputStream inputJsonStream = null;
        try {
            inputJsonStream = new FileInputStream(jsonFile);
            String jsonString = new String(inputJsonStream.readAllBytes());
            result = fromJsonToMap(jsonString);
        } catch (IOException e) {
            throw new IOException("I/O error occurs reading from the input JSON stream");
        } finally {
            try {
                if (inputJsonStream != null) {
                    inputJsonStream.close();
                }
            } catch (IOException e) {
                System.err.println("I/O error occurs closing input JSON stream");
                e.printStackTrace();
            }
        }
        return result;
    }

    static public <T> T fromJsonNewObject(File jsonFile, Class<T> filledClass) throws
            WrongJsonStringFormatException,
            IOException,
            NoSuchFieldException,
            InstantiationException,
            IllegalAccessException {
        if (jsonFile == null) {
            throw new NullPointerException("The file is null");
        }
        if (!jsonFile.exists()) {
            throw new FileNotFoundException("The file does not exist");
        }

        InputStream inputJsonStream = null;
        try {
            inputJsonStream = new FileInputStream(jsonFile);
            String jsonString = new String(inputJsonStream.readAllBytes());

            return fromJsonNewObject(jsonString, filledClass);
        } catch (IOException e) {
            throw new IOException("I/O error occurs reading from the input JSON stream", e);
        } finally {
            // Закрытие потока
            if (inputJsonStream != null) {
                try {
                    inputJsonStream.close();
                } catch (IOException e) {
                    System.err.println("I/O error occurs closing input JSON stream");
                    e.printStackTrace();
                }
            }
        }
    }
}
