package ru.spbstu.hsai.jsparser;
import ru.spbstu.hsai.jsparser.custom.JsonDeserialize;
import java.lang.reflect.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class JsonDeserializer {


    record MapPair(Map<String, Object> resultMap, int index) {}
    record ListPair(List<Object> resultList, int index) {};

    public static Map<String, Object> jsonToMap(String jsString) {
        JsonTokenizerClass tokensObj = new JsonTokenizerClass(jsString);
        List<String> tokens = tokensObj.tokenize().stream()
                .filter(n -> !n.equals(",") && !n.equals(":"))
                .collect(Collectors.toList());
        return convertTokenToMap(tokens, 1).resultMap;
    }

    public static <T> T jsonToClass(String jsString, Class<T> convertClass) {
        return TokensToClass(convertClass, jsonToMap(jsString));
    }


    private static ListPair convertTokenToList(List<String> subTokenList, int index) {
        List<Object> listObject = new ArrayList<>();
        while (index < subTokenList.size()) {
            String token = subTokenList.get(index);
            if (token.equals("[")) {
                ListPair result = convertTokenToList(subTokenList, index + 1);
                listObject.add(result.resultList);
                index = result.index;
            } else if (token.equals("{")) {
                MapPair result = convertTokenToMap(subTokenList, index + 1);
                listObject.add(result.resultMap);
                index = result.index;
            } else if (token.equals("]")) {
                return new ListPair(listObject, index);
            } else {
                listObject.add(parseValue(token));
            }
            index++;
        }
        return new ListPair(listObject, subTokenList.size());
    }

    private static MapPair convertTokenToMap(List<String> subTokenList, int index) {
        Map<String, Object> jsonMap = new HashMap<>();
        String key = "";
        while (index < subTokenList.size()) {
            String currentToken = subTokenList.get(index);
            switch (currentToken) {
                case "[" -> {
                    ListPair result = convertTokenToList(subTokenList, index + 1);
                    jsonMap.put(key, result.resultList);
                    key = "";
                    index = result.index;
                }
                case "{" -> {
                    MapPair result = convertTokenToMap(subTokenList, index+1);
                    jsonMap.put(key, result.resultMap);
                    key = "";
                    index = result.index;
                }
                case "}" -> {
                    return new MapPair(jsonMap, index);
                }
                default -> {
                    if (key.isEmpty()) {
                        key = currentToken;
                    } else {
                        jsonMap.put(key, parseValue(currentToken));
                        key = "";
                    }
                }
            }
            index++;
        }
        return new MapPair(jsonMap, subTokenList.size());
    }

    private static Object parseValue(String token) {
        switch (token) {
            case "true": return true;
            case "false": return false;
            case "null": return null;
        }

        if (token.startsWith("\"") && token.endsWith("\"")) {
            return token.substring(1, token.length() - 1);
        }

        try {
            if (token.matches("[-+]?\\d+")) {
                return Long.parseLong(token);

            } else if (token.matches("[-+]?\\d*\\.?\\d+([eE][-+]?\\d+)?")) {
                return Double.parseDouble(token);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format");
        }

        throw new IllegalArgumentException("Unknown token format");
    }


    public static <T> T TokensToClass(Class<T> convertClass, Map<String, Object> jsonTokens) {
        try {
            if (convertClass.isAnnotationPresent(JsonDeserialize.class)) {
                JsonDeserialize annotation = convertClass.getAnnotation(JsonDeserialize.class);
                Class<?> deserializerClass = annotation.using();
                Method method = deserializerClass.getDeclaredMethod("deserialize", Map.class);
                return (T) method.invoke(null, jsonTokens);
            }

            T instance = convertClass.getDeclaredConstructor().newInstance();
            List<Field> allFields = getAllFields(convertClass);
            for (Field field : allFields) {
                field.setAccessible(true);
                String fieldName = "\"" + field.getName() + "\"";
                Class<?> fieldType = field.getType();
                if (jsonTokens.containsKey(fieldName)) {
                    Object value = jsonTokens.get(fieldName);
                    Object convertValue = convertValue(value, fieldType);
                    field.set(instance, convertValue);
                }
            }

            return instance;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Convert to class is failed: " + e.getMessage());
        }
    }


    private static Object convertValue(Object value, Class<?> fieldType) throws Exception {
        if (value == null){
            return null;
        }

        if (fieldType.isAssignableFrom(value.getClass())){
            return value;
        }
        if (fieldType.isPrimitive() || isWrapperType(fieldType)){
            return convertToPrimitive(value, fieldType);
        }
        if (fieldType.isArray()){
            return convertToArray(value,fieldType);
        }
        if (Collection.class.isAssignableFrom(fieldType)) {
            return convertToCollection((List<Object>) value, fieldType);
        }

        if (Map.class.isAssignableFrom(fieldType)) {
            return convertToMap((Map<String, Object>) value, fieldType);
        }
        return TokensToClass(fieldType, (Map<String, Object>) value);
    }


    private static Object convertToPrimitive(Object value, Class<?> fieldType) {

        if (value instanceof Number number) {
            if (fieldType == Byte.class || fieldType == Byte.TYPE) return number.byteValue();
            if (fieldType == Short.class || fieldType == Short.TYPE) return number.shortValue();
            if (fieldType == Integer.class || fieldType == Integer.TYPE) return number.intValue();
            if (fieldType == Long.class || fieldType == Long.TYPE) return number.longValue();
            if (fieldType == Double.class || fieldType == Double.TYPE) return number.doubleValue();
            if (fieldType == Float.class || fieldType == Float.TYPE) return number.floatValue();
        }
        if (value instanceof Boolean && (fieldType == Boolean.TYPE || fieldType == Boolean.class))  return value;

        throw new RuntimeException("Can not convert to primitive");
    }

    private static Object convertToArray(Object value, Class<?> fieldType) throws Exception {
        Class<?> componentType = fieldType.getComponentType();
        List<?> valueList = (List<?>) value;
        Object array = Array.newInstance(componentType, valueList.size());
        for(int i =0; i<valueList.size(); i++){
            Object innerValue = convertValue(valueList.get(i), componentType);
            Array.set(array, i, innerValue);
        }
        return array;
    }

    private static Map<?,?> convertToMap(Map<String, Object> value, Class<?> fieldType) throws Exception {

        Map<Object, Object> resultMap;

        if (fieldType.isInterface()) {
            resultMap = new HashMap<>();
        } else {
            resultMap = (Map<Object, Object>) fieldType.getDeclaredConstructor().newInstance();
        }
        Type[] typeParameters = ((ParameterizedType) fieldType.getGenericSuperclass()).getActualTypeArguments();
        Class<?> keyClass = (Class<?>) typeParameters[0];
        Class<?> valueClass = (Class<?>) typeParameters[1];
        Set<Map.Entry<String, Object>> entries = value.entrySet();

        for (Map.Entry<String, Object> entry : entries) {
            Object keyMap = convertValue(entry.getKey(), keyClass);
            Object valueMap = convertValue(entry.getValue(), valueClass);
            resultMap.put(keyMap, valueMap);
        }
        return resultMap;
    }

    private static Object convertToCollection(List<Object> value, Class<?> fieldType) throws Exception {
        Collection<Object> resultCollection;

        if (fieldType == List.class){
            resultCollection = new ArrayList<>();
        }
        else if (fieldType == Set.class){
            resultCollection = new HashSet<>();
        }
        else resultCollection = (Collection<Object>) fieldType.getDeclaredConstructor().newInstance();

        Type elementType = ((ParameterizedType) fieldType.getGenericSuperclass()).getActualTypeArguments()[0];
        Class<?> elementClass = (Class<?>) elementType;

        for (Object item : value) {
            resultCollection.add(convertValue(item, elementClass));
        }

        return resultCollection;
    }


    private static boolean isWrapperType(Class<?> clazz) {
        return clazz == Integer.class || clazz == Long.class ||
                clazz == Double.class || clazz == Float.class ||
                clazz == Boolean.class || clazz == Character.class ||
                clazz == Byte.class || clazz == Short.class;
    }

     static List<Field> getAllFields(Class<?> convertClass){
        List<Field> allFields = new ArrayList<>();
        while (convertClass != null){
            allFields.addAll(Arrays.asList(convertClass.getDeclaredFields()));
            convertClass = convertClass.getSuperclass();
        }
        return allFields;
    }
}
