package com.parser;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

public class JsonParserImpl implements JsonParser {

    @Override
    public Object parse(String json) throws JsonParseException {
        JsonTokenizer tokenizer = new JsonTokenizer(json);
        return parseValue(tokenizer);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> parseToMap(String json) throws JsonParseException {
        Object parsed = parse(json);
        if (!(parsed instanceof Map)) {
            throw new JsonParseException("Root JSON element is not an object");
        }
        return (Map<String, Object>) parsed;
    }

    @Override
    public <T> T parseToClass(String json, Class<T> clazz) throws JsonParseException {
        Map<String, Object> map = parseToMap(json);
        return mapToClass(map, clazz);
    }

    @Override
    public String toJson(Object object) throws JsonParseException {
        StringBuilder sb = new StringBuilder();
        writeValue(object, sb);
        return sb.toString();
    }

    private Object parseValue(JsonTokenizer tokenizer) throws JsonParseException {
        JsonToken token = tokenizer.nextToken();
        switch (token.getType()) {
            case STRING:
                return token.getValue();
            case NUMBER:
                return parseNumber(token.getValue());
            case BOOLEAN:
                return Boolean.parseBoolean(token.getValue());
            case NULL:
                return null;
            case OBJECT_START:
                return parseObject(tokenizer);
            case ARRAY_START:
                return parseArray(tokenizer);
            default:
                throw new JsonParseException("Unexpected token: " + token);
        }
    }

    private Map<String, Object> parseObject(JsonTokenizer tokenizer) throws JsonParseException {
        Map<String, Object> map = new HashMap<>();
        while (true) {
            JsonToken token = tokenizer.nextToken();
            if (token.getType() == JsonTokenType.OBJECT_END) {
                break;
            }
            if (token.getType() != JsonTokenType.STRING) {
                throw new JsonParseException("Expected string key in object");
            }
            String key = token.getValue();
            if (tokenizer.nextToken().getType() != JsonTokenType.COLON) {
                throw new JsonParseException("Expected ':' after object key");
            }
            Object value = parseValue(tokenizer);
            map.put(key, value);
            token = tokenizer.nextToken();
            if (token.getType() == JsonTokenType.OBJECT_END) {
                break;
            }
            if (token.getType() != JsonTokenType.COMMA) {
                throw new JsonParseException("Expected ',' or '}' in object");
            }
        }
        return map;
    }

    private List<Object> parseArray(JsonTokenizer tokenizer) throws JsonParseException {
        List<Object> list = new ArrayList<>();
        while (true) {
            JsonToken token = tokenizer.peekToken();
            if (token.getType() == JsonTokenType.ARRAY_END) {
                tokenizer.nextToken();
                break;
            }
            list.add(parseValue(tokenizer));
            token = tokenizer.nextToken();
            if (token.getType() == JsonTokenType.ARRAY_END) {
                break;
            }
            if (token.getType() != JsonTokenType.COMMA) {
                throw new JsonParseException("Expected ',' or ']' in array");
            }
        }
        return list;
    }

    private Number parseNumber(String value) {
        if (value.contains(".") || value.contains("e") || value.contains("E")) {
            return Double.parseDouble(value);
        } else {
            return Long.parseLong(value);
        }
    }

    private <T> T mapToClass(Map<String, Object> map, Class<T> clazz) throws JsonParseException {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
                for (Field field : c.getDeclaredFields()) {
                    field.setAccessible(true);
                    String fieldName = field.getName();
                    if (map.containsKey(fieldName)) {
                        Object value = map.get(fieldName);
                        field.set(instance, convertValue(value, field.getType()));
                    }
                }
            }
            return instance;
        } catch (ReflectiveOperationException e) {
            throw new JsonParseException("Error creating instance of " + clazz.getName(), e);
        }
    }

    private Object convertValue(Object value, Class<?> targetType) throws JsonParseException {
        if (value == null) {
            return null;
        }
        if (targetType.isAssignableFrom(value.getClass())) {
            return value;
        }
        if (targetType == String.class) {
            return value.toString();
        }
        if (targetType.isArray()) {
            return convertToArray(value, targetType.getComponentType());
        }
        if (Collection.class.isAssignableFrom(targetType)) {
            return convertToCollection(value, targetType);
        }
        if (targetType == int.class || targetType == Integer.class) {
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
        }
        if (targetType == long.class || targetType == Long.class) {
            if (value instanceof Number) {
                return ((Number) value).longValue();
            }
        }
        if (targetType == double.class || targetType == Double.class) {
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
        }
        // Add more type conversions as needed
        throw new JsonParseException("Unsupported type conversion from " + value.getClass() + " to " + targetType);
    }

    private Object convertToArray(Object value, Class<?> componentType) throws JsonParseException {
        if (!(value instanceof List)) {
            throw new JsonParseException("Cannot convert " + value.getClass() + " to array");
        }
        List<?> list = (List<?>) value;
        Object array = Array.newInstance(componentType, list.size());
        for (int i = 0; i < list.size(); i++) {
            Array.set(array, i, convertValue(list.get(i), componentType));
        }
        return array;
    }

    @SuppressWarnings("unchecked")
    private Object convertToCollection(Object value, Class<?> targetType) throws JsonParseException {
        if (!(value instanceof List)) {
            throw new JsonParseException("Cannot convert " + value.getClass() + " to collection");
        }
        List<?> list = (List<?>) value;
        Collection<Object> collection;
        try {
            collection = (Collection<Object>) targetType.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new JsonParseException("Cannot create instance of " + targetType, e);
        }
        for (Object item : list) {
            collection.add(item);
        }
        return collection;
    }

    private void writeValue(Object value, StringBuilder sb) throws JsonParseException {
        if (value == null) {
            sb.append("null");
        } else if (value instanceof String) {
            writeString((String) value, sb);
        } else if (value instanceof Number) {
            sb.append(value.toString());
        } else if (value instanceof Boolean) {
            sb.append(value.toString());
        } else if (value instanceof Map) {
            writeObject((Map<?, ?>) value, sb);
        } else if (value instanceof Collection) {
            writeArray((Collection<?>) value, sb);
        } else if (value.getClass().isArray()) {
            writeArray(Arrays.asList((Object[]) value), sb);
        } else {
            writeObject(value, sb);
        }
    }

    private void writeString(String value, StringBuilder sb) {
        sb.append('"');
        for (char c : value.toCharArray()) {
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c < ' ') {
                        String t = "000" + Integer.toHexString(c);
                        sb.append("\\u").append(t.substring(t.length() - 4));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append('"');
    }

    private void writeObject(Map<?, ?> map, StringBuilder sb) throws JsonParseException {
        sb.append('{');
        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (first) {
                first = false;
            } else {
                sb.append(',');
            }
            writeString(entry.getKey().toString(), sb);
            sb.append(':');
            writeValue(entry.getValue(), sb);
        }
        sb.append('}');
    }

    private void writeObject(Object obj, StringBuilder sb) throws JsonParseException {
        sb.append('{');
        boolean first = true;
        for (Class<?> c = obj.getClass(); c != null; c = c.getSuperclass()) {
            for (Field field : c.getDeclaredFields()) {
                field.setAccessible(true);
                if (first) {
                    first = false;
                } else {
                    sb.append(',');
                }
                writeString(field.getName(), sb);
                sb.append(':');
                try {
                    Object value = field.get(obj);
                    writeValue(value, sb);
                } catch (IllegalAccessException e) {
                    throw new JsonParseException("Error accessing field " + field.getName(), e);
                }
            }
        }
        sb.append('}');
    }

    private void writeArray(Collection<?> collection, StringBuilder sb) throws JsonParseException {
        sb.append('[');
        boolean first = true;
        for (Object item : collection) {
            if (first) {
                first = false;
            } else {
                sb.append(',');
            }
            writeValue(item, sb);
        }
        sb.append(']');
    }
}