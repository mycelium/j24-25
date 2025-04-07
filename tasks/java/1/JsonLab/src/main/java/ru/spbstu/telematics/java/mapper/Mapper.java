package ru.spbstu.telematics.java.mapper;

import ru.spbstu.telematics.java.exceptions.FieldNotFoundException;
import ru.spbstu.telematics.java.exceptions.JsonException;
import ru.spbstu.telematics.java.exceptions.MappingException;
import ru.spbstu.telematics.java.exceptions.TypeMismatchException;

import java.lang.reflect.Field;
import java.util.*;

import static ru.spbstu.telematics.java.utils.CollectionUtils.convertToArray;
import static ru.spbstu.telematics.java.utils.ReflectionUtils.*;

public class Mapper {

    public static <T> T setFieldsInClass(Map<String, Object> data, Class<T> targetClass) throws MappingException {
        try {

            T instance = createInstance(targetClass);
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                String fieldName = entry.getKey();
                Object value = entry.getValue();

                Field field = findField(targetClass, fieldName);
                field.setAccessible(true);
                // Обработка коллекций
                if (value instanceof Collection && !Collection.class.isAssignableFrom(field.getClass()) && !field.getType().isArray()) {
                    value = convertValue(value, getGenericType(field));
                }
                else{
                    value = convertValue(value, field.getType());
                }

                field.set(instance, value);

            }
            return instance;
        } catch (ReflectiveOperationException | IllegalArgumentException | TypeMismatchException |
                 FieldNotFoundException e) {
            throw new MappingException("Failed to map field: " + e.getMessage());
        }
        catch (JsonException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object convertValue(Object value, Class<?> targetType) throws JsonException, ClassNotFoundException, NoSuchFieldException {
        if (value == null) return null;

        // Обработка массивов
        if (targetType.isArray() && value instanceof Collection) {
            return convertToArray(value, targetType);
        }

        // Обработка Map
        if (value instanceof Map map) {
            String type = (String) map.remove("@type");
            Class<?> actualType = type != null ? Class.forName(type) : targetType;

            // Создаем копию map, чтобы не изменять оригинал
            Map<String, Object> fieldMap = new HashMap<>(map);
            var classList = findSubclasses(actualType, getRawType(actualType).getPackageName());
            var valuesFieldSet = new HashSet<>(fieldMap.keySet());

            for (var subclass : classList) {
                // Собираем все поля класса, включая родительские
                var subclassFieldSet = new HashSet<>();
                Class<?> currentClass = subclass;
                while (currentClass != null && currentClass != Object.class) {
                    Arrays.stream(currentClass.getDeclaredFields())
                            .map(Field::getName)
                            .forEach(subclassFieldSet::add);
                    currentClass = currentClass.getSuperclass();
                }

                if (valuesFieldSet.equals(subclassFieldSet)) {
                    return setFieldsInClass(fieldMap, subclass);
                }
            }

            // Если не нашли подходящий подкласс, пробуем создать экземпляр targetType
            try {
                return setFieldsInClass(fieldMap, targetType);
            } catch (Exception e) {
                throw new NoSuchFieldException("No matching subclass found and cannot create instance of " + targetType.getName());
            }
        }

        if (value instanceof Collection && !Collection.class.isAssignableFrom(targetType)) {
            try {


                List<Object> result = new ArrayList<>();
                for (Object item : (Collection<?>) value) {
                    // System.out.println(item);
                    result.add(convertValue(item, targetType));
                }
                return result;
            } catch (Exception e) {
                throw new JsonException("Failed to parse collection: " + e.getMessage());
            }
        }



        // Обработка Iterable (кроме массивов)
        if (value instanceof Iterable && !(value instanceof Collection)) {
            value = new ArrayList<>((Collection) value);
        }

//        // Обработка коллекций
//        if (value instanceof Collection) {
//            return convertToCollection(value, targetType);
//        }

        // Обработка примитивов и строк
        if (targetType.isPrimitive() || targetType == String.class) {
            return convertPrimitive(value, targetType);
        }

        // Обработка boolean
        if (targetType == Boolean.class) {
            return convertPrimitive(value, targetType);
        }

        // Обработка enum
        if (targetType.isEnum()) {
            try {
                return Enum.valueOf((Class<Enum>) targetType, value.toString());
            } catch (IllegalArgumentException e) {
                throw new JsonException("Invalid enum value");
            }
        }

        // Проверка типа
        if (!targetType.isInstance(value)) {
            throw new TypeMismatchException(
                    "Type mismatch for " + targetType.getName() +
                            ". Expected: " + targetType + ", actual: " + value.getClass());
        }

        return value;
    }

    // Преобразование примитивных типов
    private static Object convertPrimitive(Object value, Class<?> targetClass) throws JsonException {
        String strValue = value.toString();
        if (targetClass.equals(Integer.class) || targetClass.equals(int.class)) {
            return Integer.parseInt(value.toString());
        }
        if (targetClass.equals(Double.class) || targetClass.equals(double.class)) {
            return Double.parseDouble(value.toString());
        }
        if (targetClass.equals(Boolean.class) || targetClass.equals(boolean.class)) {
            if ("true".equalsIgnoreCase(strValue) || "false".equalsIgnoreCase(strValue)) {
                return Boolean.parseBoolean(strValue);
            }
            throw new JsonException("Invalid boolean value: " + strValue);
        }

        if (targetClass.equals(String.class)) return value.toString();
        throw new JsonException("Unsupported primitive type: " + targetClass);
    }

    private static Iterable<?> convertToCollection(Object value, Class<?> collectionType)
            throws JsonException {
        try {
            if (collectionType == null) {
                throw new NullPointerException("collectionType cannot be null");
            }

            // Проверяем, что value является Iterable (например, List)
            if (!(value instanceof Iterable<?>)) {
                throw new JsonException("Value must be an Iterable");
            }
            Class<?> elementType = collectionType.getComponentType();// getGenericType(field); // Нужно определить тип элементов
            List<Object> result = new ArrayList<>();
            for (Object item : (Collection<?>) value) {
                result.add(convertValue(item, elementType));
            }
            return result;

//            // Если collectionType — интерфейс (например, List), используем ArrayList
//            Class<?> concreteType = collectionType.isInterface()
//                    ? ArrayList.class
//                    : collectionType;
//
//            // Создаём экземпляр коллекции
//            Collection<Object> result;
//            try {
//                result = (Collection<Object>) concreteType.getDeclaredConstructor().newInstance();
//            } catch (NoSuchMethodException e) {
//                throw new JsonException("Collection type must have a no-args constructor: " + concreteType.getName());
//            }
//
//            // Копируем элементы
//            for (Object item : (Iterable<?>) value) {
//                result.add(item);
//            }

            //return result;
        } catch (Exception e) {
            throw new JsonException("Failed to create collection of type " + collectionType.getName() + ": " + e.getMessage());
        }
    }

}
