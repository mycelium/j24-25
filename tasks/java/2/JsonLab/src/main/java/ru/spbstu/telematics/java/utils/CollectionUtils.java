package ru.spbstu.telematics.java.utils;

import ru.spbstu.telematics.java.exceptions.JsonException;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;

import static ru.spbstu.telematics.java.mapper.Mapper.convertValue;

public class CollectionUtils {

    public static Object convertToArray(Object value, Class<?> arrayType) throws JsonException, ClassNotFoundException, NoSuchFieldException {
        Class<?> componentType = arrayType.getComponentType();

        if (value instanceof Object[]) {
            Object[] source = (Object[]) value;
            Object result = Array.newInstance(componentType, source.length);
            for (int i = 0; i < source.length; i++) {
                Array.set(result, i, convertValue(source[i], componentType));
            }
            return result;
        }

        if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            Object result = Array.newInstance(componentType, length);
            for (int i = 0; i < length; i++) {
                Array.set(result, i, Array.get(value, i));
            }
            return result;
        }

        if (value instanceof Collection) {
            Collection<?> collection = (Collection<?>) value;
            Object result = Array.newInstance(componentType, collection.size());
            int i = 0;
            for (Object element : collection) {
                Array.set(result, i++, convertValue(element, componentType));
            }
            return result;
        }

        throw new JsonException("Cannot convert to array: " + value.getClass());
    }

    // Вспомогательные методы:
    public static <T> T convertListToArray(List<Object> list, Class<?> componentType)
            throws JsonException, ClassNotFoundException, NoSuchFieldException {
        Object array = Array.newInstance(componentType, list.size());
        for (int i = 0; i < list.size(); i++) {
            Array.set(array, i, convertValue(list.get(i), componentType));
        }
        @SuppressWarnings("unchecked")
        T result = (T) array;
        return result;
    }

}
