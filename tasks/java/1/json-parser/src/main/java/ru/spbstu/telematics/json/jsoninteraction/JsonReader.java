package ru.spbstu.telematics.json.jsoninteraction;

import ru.spbstu.telematics.json.exceptions.WrongJsonStringFormatException;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.Supplier;

public class JsonReader implements JsonInteractor {
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
            return parseArray(value, ArrayList::new, Object.class);
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
            if (value.contains(".")) {
                return Double.valueOf(value);
            }
            return Integer.valueOf(value);
        } else {
            throw new WrongJsonStringFormatException("Unknown value type: " + value);
        }
    }

    private static <T extends Collection<Object>> T parseArray(String arrayJson, Supplier<T> collectionSupplier, Class<?> elementType)
            throws WrongJsonStringFormatException {

        T collection = collectionSupplier.get();
        arrayJson = arrayJson.substring(1, arrayJson.length() - 1).strip();

        if (arrayJson.isEmpty()) {
            return collection;
        }

        List<String> elements = splitJson(arrayJson);
        for (String element : elements) {
            element = element.strip();
            collection.add(parseValue(element));
        }
        return collection;
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

    static public <T> T fromJsonNewObject(String json, Class<T> filledClass)
            throws WrongJsonStringFormatException,
            InstantiationException,
            IllegalAccessException,
            NoSuchFieldException {

        if (json == null || json.isEmpty()) {
            throw new WrongJsonStringFormatException("JSON string is empty or null");
        }

        if (!json.startsWith("{") || !json.endsWith("}")) {
            throw new WrongJsonStringFormatException("JSON must be enclosed in curly braces");
        }

        try {
            // Создаем экземпляр целевого класса
            Constructor<T> constructor = filledClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            T object = null;
            try {
                object = constructor.newInstance();
            } catch (InstantiationException e) {
                throw new InstantiationException("The " + filledClass + " class is abstract");
            } catch (IllegalAccessException e) {
                throw new IllegalAccessException("Cannot access to constructor of " + filledClass);
            }

            // Убираем { } и разделяем JSON на пары ключ-значение
            json = json.substring(1, json.length() - 1).strip();
            List<String> keyValuePairs = splitJson(json);

            for (String pair : keyValuePairs) {
                String[] keyValue = pair.split(":", 2);
                if (keyValue.length != 2) {
                    throw new WrongJsonStringFormatException("Invalid key-value pair: " + pair);
                }

                // Получаем имя поля и его значение
                String fieldName = keyValue[0].strip().replaceAll("^\"|\"$", ""); // Убираем кавычки
                String fieldValue = keyValue[1].strip();

                Field field = null;
                try {
                    field = filledClass.getDeclaredField(fieldName);
                } catch (NoSuchFieldException e) {
                    throw new NoSuchFieldException("There is no field " + fieldName + " in " + filledClass);
                }
                field.setAccessible(true);
                Class<?> fieldType = field.getType();

                // Проверяем, является ли поле коллекцией
                if (Collection.class.isAssignableFrom(fieldType)) {
                    ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                    Class<?> collectionElementType = (Class<?>) genericType.getActualTypeArguments()[0];

                    Supplier<Collection<Object>> collectionSupplier = getCollectionSupplier(fieldType);
                    Collection<Object> collection = parseArray(fieldValue, collectionSupplier, collectionElementType);

                    field.set(object, collection);
                }
                // Если поле является вложенным объектом
                else if (fieldValue.startsWith("{") && fieldValue.endsWith("}")) {
                    Object nestedObject = fromJsonNewObject(fieldValue, fieldType);
                    field.set(object, nestedObject);
                }
                else {
                    Object parsedValue = parseValue(fieldValue);
                    field.set(object, parsedValue);
                }
            }
            return object;

        } catch (NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException("Cannot instantiate object of class " + filledClass, e);
        }
    }


    private static Supplier<Collection<Object>> getCollectionSupplier(Class<?> fieldType) {
        if (fieldType.isAssignableFrom(ArrayList.class)) {
            return ArrayList::new;
        } else if (fieldType.isAssignableFrom(LinkedList.class)) {
            return LinkedList::new;
        } else if (fieldType.isAssignableFrom(HashSet.class)) {
            return HashSet::new;
        } else if (fieldType.isAssignableFrom(TreeSet.class)) {
            return TreeSet::new;
        } else if (fieldType.isAssignableFrom(ArrayDeque.class)) {
            return ArrayDeque::new;
        } else {
            throw new IllegalArgumentException("Unsupported collection type: " + fieldType.getSimpleName());
        }
    }


    static public void fromJsonToObject(String json, Object targetObject) throws
            WrongJsonStringFormatException,
            NoSuchFieldException,
            IllegalAccessException,
            NoSuchMethodException,
            InvocationTargetException,
            InstantiationException {
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
                fromJsonToObject(JsonInteractor.mapToJson((Map<String, Object>) value), nestedObject);
            } else {
                field.set(targetObject, value);
            }
        }
    }

    static public Map<String, Object> fromJsonToMap(File jsonFile) throws IOException, WrongJsonStringFormatException {

        return fromJsonToMap(JsonInteractor.jsonFileToJsonString(jsonFile));

    }

    static public <T> T fromJsonNewObject(File jsonFile, Class<T> filledClass) throws
            IOException,
            WrongJsonStringFormatException,
            NoSuchFieldException,
            InstantiationException,
            IllegalAccessException {

        return fromJsonNewObject(JsonInteractor.jsonFileToJsonString(jsonFile), filledClass);

    }

    static public void fromJsonToObject(File jsonFile, Object targetObject) throws
            WrongJsonStringFormatException,
            IOException,
            NoSuchFieldException,
            InvocationTargetException,
            IllegalAccessException,
            NoSuchMethodException,
            InstantiationException {

        fromJsonToObject(JsonInteractor.jsonFileToJsonString(jsonFile), targetObject);

    }


}
