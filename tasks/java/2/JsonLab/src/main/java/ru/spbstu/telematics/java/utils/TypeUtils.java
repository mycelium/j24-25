package ru.spbstu.telematics.java.utils;

public class TypeUtils {
    public static boolean isSimpleValueType(Class<?> type) {
        return type == String.class || type.isPrimitive() ||
                Number.class.isAssignableFrom(type) ||
                type == Boolean.class || type.isEnum();
    }
}
