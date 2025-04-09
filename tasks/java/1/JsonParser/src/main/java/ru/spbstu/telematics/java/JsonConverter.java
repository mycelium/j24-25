package ru.spbstu.telematics.java;
import java.lang.reflect.*;
import java.util.*;

@SuppressWarnings("ALL")
public class JsonConverter {
    // 1. из объекта в json
    public String toJson(Object object) {
        if (object == null) {
            return "null";
        }
        if (object instanceof Collection) {
            return toJsonArray((Collection<?>) object);
        }
        if (object.getClass().isArray()) {
            return toJsonArray(Arrays.asList((Object[]) object));
        }

        Map<String, Object> map = toMap(object);
        return toJsonObject(map);
    }


    private Map<String, Object> toMap(Object object) {
        Map<String, Object> map = new LinkedHashMap<>();
        Class<?> currentClass = object.getClass();

        while (currentClass != null && currentClass != Object.class) {
            for (Field field : currentClass.getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    Object value = field.get(object);
                    map.put(field.getName(), value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Не удалось получить значение поля: " + field.getName(), e);
                }
            }
            currentClass = currentClass.getSuperclass(); // к родительскому классу
        }

        return map;
    }

    // Map в json строку
    private String toJsonObject(Map<String, Object> map) {
        StringBuilder json = new StringBuilder();
        json.append("{");

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            json.append("\"").append(key).append("\":");

            if (value == null) {
                json.append("null");
            } else if (value instanceof String) {
                json.append("\"").append(value).append("\"");
            } else if (value instanceof Number || value instanceof Boolean) {
                json.append(value);
            } else if (value instanceof Collection) {
                json.append(toJsonArray((Collection<?>) value));
            } else if (value.getClass().isArray()) {
                json.append(toJsonArray(Arrays.asList((Object[]) value)));
            } else {
                json.append(toJsonObject(toMap(value)));
            }

            json.append(",");
        }

        if (json.charAt(json.length() - 1) == ',') {
            json.deleteCharAt(json.length() - 1);
        }

        json.append("}");
        return json.toString();
    }

    private String toJsonArray(Collection<?> collection) {
        StringBuilder json = new StringBuilder();
        json.append("[");


        for (Object item : collection) {
            if (item == null) {
                json.append("null");
            } else if (item instanceof String) {
                json.append("\"").append(item).append("\"");
            } else if (item instanceof Number || item instanceof Boolean) {
                json.append(item);
            } else if (item instanceof Collection) {
                json.append(toJsonArray((Collection<?>) item));
            } else if (item.getClass().isArray()) {
                json.append(toJsonArray(Arrays.asList((Object[]) item)));
            } else {
                json.append(toJsonObject(toMap(item)));
            }
            json.append(",");
        }

        if (json.charAt(json.length() - 1) == ',') {
            json.deleteCharAt(json.length() - 1);
        }

        json.append("]");
        return json.toString();
    }

    //2. из json в объект указанного класса
    public <T> T fromJson(Object jsonObject, Class<T> spClass) {
        if (jsonObject == null) {
            return null;
        }

        if (jsonObject instanceof Map) {
            return fromMap((Map<String, Object>) jsonObject, spClass);
        } else {
            throw new IllegalArgumentException("Ожидался объект JSON, но получено: " + jsonObject.getClass());
        }
    }

    private <T> T fromMap(Map<String, Object> map, Class<T> spClass) {
        try {
            // Ищем конструктор с аннотацией @JsonCreator
            Constructor<?> constructor = findJsonCreatorConstructor(spClass);

            if (constructor != null) {
                // Обрабатываем параметры конструктора с аннотациями
                Parameter[] params = constructor.getParameters();
                Object[] args = new Object[params.length];

                for (int i = 0; i < params.length; i++) {
                    Parameter param = params[i];
                    JsonProperty jsonProperty = param.getAnnotation(JsonProperty.class);
                    String paramName = jsonProperty != null ? jsonProperty.value() : param.getName();
                    Object value = map.get(paramName);

                    if (param.isVarArgs()) {
                        // Для varargs параметров
                        args[i] = handleVarArgsParameter(param, value);
                    } else {
                        // Для обычных параметров
                        args[i] = convertValue(value, param.getType(), param.getParameterizedType());
                    }
                }

                return (T) constructor.newInstance(args);
            }

            // Если конструктора с @JsonCreator нет, используем стандартный подход
            return createViaDefaultConstructor(spClass, map);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка создания " + spClass.getSimpleName(), e);
        }
    }

    private Object handleVarArgsParameter(Parameter param, Object jsonValue) {
        Class<?> componentType = param.getType().getComponentType();

        if (jsonValue == null) {
            return Array.newInstance(componentType, 0);
        }

        if (jsonValue instanceof List<?> list) {
            Object array = Array.newInstance(componentType, list.size());
            for (int i = 0; i < list.size(); i++) {
                Array.set(array, i, convertValue(list.get(i), componentType, componentType));
            }
            return array;
        }

        // Если значение не список, создаем массив из одного элемента
        Object singleArray = Array.newInstance(componentType, 1);
        Array.set(singleArray, 0, convertValue(jsonValue, componentType, componentType));
        return singleArray;
    }

    private Constructor<?> findJsonCreatorConstructor(Class<?> clazz) {
        for (Constructor<?> c : clazz.getDeclaredConstructors()) {
            if (c.isAnnotationPresent(JsonCreator.class)) {
                c.setAccessible(true);
                return c;
            }
        }
        return null;
    }

    private <T> T createViaDefaultConstructor(Class<T> spClass, Map<String, Object> map) throws Exception {
        try {
            T instance = spClass.getDeclaredConstructor().newInstance();
            for (Field field : spClass.getDeclaredFields()) {
                field.setAccessible(true);
                if (map.containsKey(field.getName())) {
                    field.set(instance, convertValue(map.get(field.getName()),
                            field.getType(), field.getGenericType()));
                }
            }
            return instance;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Не найден конструктор без параметров. Добавьте @JsonCreator", e);
        }
    }

    private Object convertValue(Object value, Class<?> targetType, Type genericType) {
        if (value == null) return null;

        if (targetType.isPrimitive() || isWrapperType(targetType) || targetType == String.class) {
            return castPrimitive(value, targetType);
        }

        if (targetType.isArray()) {
            return convertToArray(value, targetType.getComponentType());
        }

        if (Collection.class.isAssignableFrom(targetType)) {
            return convertToCollection(value, targetType, genericType);
        }

        if (value instanceof Map) {
            return fromMap((Map<String, Object>) value, targetType);
        }

        throw new RuntimeException("Невозможно преобразовать значение " + value + " в тип " + targetType);
    }

    private Object castPrimitive(Object value, Class<?> targetType) {
        if (targetType == int.class || targetType == Integer.class) return ((Number) value).intValue();
        if (targetType == long.class || targetType == Long.class) return ((Number) value).longValue();
        if (targetType == double.class || targetType == Double.class) return ((Number) value).doubleValue();
        if (targetType == boolean.class || targetType == Boolean.class) return (Boolean) value;
        if (targetType == String.class) return value.toString();
        return value;
    }

    private Object convertToArray(Object value, Class<?> componentType) {
        if (!(value instanceof List<?> list)) {
            throw new RuntimeException("Ожидался массив в JSON");
        }

        Object array = Array.newInstance(componentType, list.size());
        for (int i = 0; i < list.size(); i++) {
            Array.set(array, i, convertValue(list.get(i), componentType, componentType));
        }
        return array;
    }

    private Object convertToCollection(Object value, Class<?> collectionType, Type genericType) {
        if (!(value instanceof List<?> list)) {
            throw new RuntimeException("Ожидался список в JSON");
        }


        Collection<Object> result;
        if (collectionType.isInterface()) {
            result = new ArrayList<>();
        } else {
            try {
                result = (Collection<Object>) collectionType.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Невозможно создать экземпляр " + collectionType.getName(), e);
            }
        }

        Type elementType = Object.class;
        if (genericType instanceof ParameterizedType parameterizedType) {
            elementType = parameterizedType.getActualTypeArguments()[0];
        }

        for (Object item : list) {
            result.add(convertValue(item, (Class<?>) elementType, elementType));
        }

        return result;
    }

    private boolean isWrapperType(Class<?> spClass) {
        return spClass == Integer.class || spClass == Long.class || spClass == Double.class ||
                spClass == Boolean.class || spClass == Byte.class || spClass == Short.class ||
                spClass == Float.class || spClass == Character.class;
    }



}