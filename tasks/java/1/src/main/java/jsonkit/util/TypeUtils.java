package jsonkit.util;

import jsonkit.model.JsonException;
import java.lang.reflect.*;
import java.util.*;

public class TypeUtils {

    /**
     * Проверяет, является ли тип простым (String, примитив, Number, Boolean, Character)
     */
    public static boolean isSimpleType(Class<?> type) {
        return type == String.class || type.isPrimitive()
                || Number.class.isAssignableFrom(type)
                || type == Boolean.class || type == Character.class;
    }

    /**
     * Конвертирует значение в указанный тип
     */
    public static Object convertValue(Object value, Class<?> targetType) throws JsonException {
        try {
            if (value == null) return null;
            if (targetType.isInstance(value)) return value;

            if (targetType == String.class) return value.toString();

            if (targetType == Boolean.class || targetType == boolean.class) {
                return Boolean.valueOf(value.toString());
            }

            // Обработка числовых типов
            if (targetType.isPrimitive() || Number.class.isAssignableFrom(targetType)) {
                if (value instanceof Boolean) {
                    throw new JsonException("Cannot convert Boolean to Number");
                }
                Number num = value instanceof Number ? (Number) value
                        : Double.parseDouble(value.toString());
                return convertNumber(num, targetType);
            }

            // Обработка массивов
            if (targetType.isArray()) {
                return handleArrayConversion(value, targetType);
            }

            // Обработка коллекций
            if (Collection.class.isAssignableFrom(targetType)) {
                return handleCollectionConversion(value, targetType);
            }

            // Обработка объектов
            if (value instanceof Map) {
                return createInstance(targetType, (Map<String, Object>) value);
            }

            return value;
        } catch (Exception e) {
            throw new JsonException("Conversion error: " + e.getMessage(), e);
        }
    }

    /**
     * Конвертирует число в указанный числовой тип
     */
    private static Object convertNumber(Number num, Class<?> targetType) {
        if (targetType == int.class || targetType == Integer.class) return num.intValue();
        if (targetType == long.class || targetType == Long.class) return num.longValue();
        if (targetType == double.class || targetType == Double.class) return num.doubleValue();
        if (targetType == float.class || targetType == Float.class) return num.floatValue();
        if (targetType == short.class || targetType == Short.class) return num.shortValue();
        if (targetType == byte.class || targetType == Byte.class) return num.byteValue();
        return num;
    }

    /**
     * Обрабатывает конвертацию в массив
     */
    public static Object handleArrayConversion(Object parsed, Class<?> arrayType) throws JsonException {
        try {
            Class<?> componentType = arrayType.getComponentType();

            if (parsed instanceof List) {
                List<?> list = (List<?>) parsed;
                Object array = Array.newInstance(componentType, list.size());
                for (int i = 0; i < list.size(); i++) {
                    Array.set(array, i, convertValue(list.get(i), componentType));
                }
                return array;
            }

            // Одиночное значение
            Object array = Array.newInstance(componentType, 1);
            Array.set(array, 0, convertValue(parsed, componentType));
            return array;
        } catch (Exception e) {
            throw new JsonException("Array conversion error", e);
        }
    }

    /**
     * Обрабатывает конвертацию в коллекцию
     */
    public static Collection<?> handleCollectionConversion(Object parsed, Class<?> collectionType)
            throws JsonException {
        try {
            if (!(parsed instanceof List)) {
                List<Object> list = new ArrayList<>(1);
                list.add(parsed);
                parsed = list;
            }

            List<?> sourceList = (List<?>) parsed;
            Collection<Object> collection;

            if (collectionType.isAssignableFrom(ArrayList.class)) {
                collection = new ArrayList<>(sourceList.size());
            }
            else if (collectionType.isAssignableFrom(HashSet.class)) {
                collection = new HashSet<>(sourceList.size());
            }
            else {
                try {
                    collection = (Collection<Object>) collectionType.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    collection = new ArrayList<>(sourceList.size());
                }
            }

            for (Object item : sourceList) {
                collection.add(item);
            }
            return collection;
        } catch (Exception e) {
            throw new JsonException("Collection conversion error", e);
        }
    }

    /**
     * Создает экземпляр класса и заполняет его поля из Map
     */
    public static <T> T createInstance(Class<T> targetClass, Map<String, Object> map)
            throws JsonException {
        try {
            // Попытка через конструктор по умолчанию
            try {
                T instance = targetClass.getDeclaredConstructor().newInstance();
                populateFields(instance, map);
                return instance;
            }
            catch (NoSuchMethodException e) {
                // Если нет конструктора по умолчанию, ищем подходящий
                return createWithConstructor(targetClass, map);
            }
        } catch (Exception e) {
            throw new JsonException("Instance creation error", e);
        }
    }

    /**
     * Создает экземпляр через подходящий конструктор
     */
    private static <T> T createWithConstructor(Class<T> targetClass, Map<String, Object> map)
            throws Exception {
        Constructor<?>[] constructors = targetClass.getDeclaredConstructors();
        Arrays.sort(constructors, (c1, c2) ->
                Integer.compare(c2.getParameterCount(), c1.getParameterCount()));

        for (Constructor<?> constructor : constructors) {
            try {
                Parameter[] parameters = constructor.getParameters();
                Object[] args = new Object[parameters.length];
                Map<String, Object> caseInsensitiveMap = new HashMap<>();
                map.forEach((k, v) -> caseInsensitiveMap.put(k.toLowerCase(), v));

                List<String> paramNames = getParameterNames(constructor);

                for (int i = 0; i < parameters.length; i++) {
                    String paramName = paramNames.get(i).toLowerCase();
                    Object value = findParameterValue(caseInsensitiveMap, paramName, targetClass);
                    args[i] = value != null ?
                            convertValue(value, parameters[i].getType()) :
                            getDefaultValue(parameters[i].getType());
                }

                constructor.setAccessible(true);
                return targetClass.cast(constructor.newInstance(args));
            } catch (IllegalArgumentException e) {
                continue; // Пробуем следующий конструктор
            }
        }
        throw new JsonException("No suitable constructor found");
    }

    /**
     * Заполняет поля объекта значениями из Map
     */
    private static <T> void populateFields(T instance, Map<String, Object> map) throws Exception {
        Class<?> currentClass = instance.getClass();
        while (currentClass != null) {
            for (Field field : currentClass.getDeclaredFields()) {
                if (Modifier.isTransient(field.getModifiers())) continue;

                field.setAccessible(true);
                String fieldName = field.getName();
                if (map.containsKey(fieldName)) {
                    Object value = map.get(fieldName);
                    Object convertedValue = convertValue(value, field.getType());
                    field.set(instance, convertedValue);
                }
            }
            currentClass = currentClass.getSuperclass();
        }
    }

    // Вспомогательные методы
    private static List<String> getParameterNames(Constructor<?> constructor) {
        List<String> names = new ArrayList<>();
        for (Parameter param : constructor.getParameters()) {
            names.add(param.isNamePresent() ? param.getName() :
                    getParameterNameFromFields(constructor.getDeclaringClass(), param));
        }
        return names;
    }

    private static String getParameterNameFromFields(Class<?> targetClass, Parameter param) {
        for (Field field : targetClass.getDeclaredFields()) {
            if (field.getType().equals(param.getType())) {
                return field.getName();
            }
        }
        return "arg" + Arrays.asList(targetClass.getDeclaredConstructors()[0].getParameters())
                .indexOf(param);
    }

    private static Object findParameterValue(
            Map<String, Object> caseInsensitiveMap,
            String paramName,
            Class<?> targetClass) {
        // 1. Точное совпадение
        Object value = caseInsensitiveMap.get(paramName);
        if (value != null) return value;

        // 2. По имени поля
        try {
            Field field = targetClass.getDeclaredField(paramName);
            value = caseInsensitiveMap.get(field.getName().toLowerCase());
            if (value != null) return value;
        } catch (NoSuchFieldException ignored) {}

        // 3. Без учета регистра
        for (Map.Entry<String, Object> entry : caseInsensitiveMap.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(paramName)) {
                return entry.getValue();
            }
        }

        return null;
    }

    private static Object getDefaultValue(Class<?> type) {
        if (!type.isPrimitive()) return null;
        if (type == boolean.class) return false;
        if (type == byte.class) return (byte) 0;
        if (type == short.class) return (short) 0;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == float.class) return 0.0f;
        if (type == double.class) return 0.0;
        if (type == char.class) return '\0';
        return null;
    }
}