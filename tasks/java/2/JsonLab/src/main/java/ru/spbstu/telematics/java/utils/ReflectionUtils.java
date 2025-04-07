package ru.spbstu.telematics.java.utils;

import ru.spbstu.telematics.java.exceptions.FieldNotFoundException;
import ru.spbstu.telematics.java.exceptions.JsonException;

import java.io.File;
import java.lang.reflect.*;
import java.net.URL;
import java.util.*;

public class ReflectionUtils {

    public static Field findField(Class<?> clazz, String name) throws FieldNotFoundException {
        Class<?> current = clazz;
        while (current != null) {
            try {
                return current.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        throw new FieldNotFoundException("Field '" + name + "' not found in class " + clazz.getName());
    }

    public static <T> T createInstance(Class<T> clazz) throws ReflectiveOperationException {
        // Сначала попробуем найти конструктор по умолчанию
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }
            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            // Если нет конструктора по умолчанию, ищем любой конструктор
            Constructor<?>[] constructors = clazz.getDeclaredConstructors();
            if (constructors.length == 0) {
                throw new ReflectiveOperationException("No constructors found for " + clazz.getName());
            }

            // Берем первый конструктор (можно добавить логику выбора лучшего конструктора)
            Constructor<?> ctor = constructors[0];
            ctor.setAccessible(true);

            // Создаем массив параметров с null/значениями по умолчанию
            Object[] params = new Object[ctor.getParameterCount()];
            Class<?>[] paramTypes = ctor.getParameterTypes();

            // Заполняем параметры значениями по умолчанию
            for (int i = 0; i < params.length; i++) {
                params[i] = getDefaultValue(paramTypes[i]);
            }

            @SuppressWarnings("unchecked")
            T instance = (T) ctor.newInstance(params);
            return instance;
        }
    }

    // Метод для получения значений по умолчанию для разных типов
    private static Object getDefaultValue(Class<?> type) throws ReflectiveOperationException {
        if (type == boolean.class) return false;
        if (type == byte.class) return (byte)0;
        if (type == short.class) return (short)0;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == float.class) return 0.0f;
        if (type == double.class) return 0.0d;
        if (type == char.class) return '\0';
        // Коллекции
        if (List.class.isAssignableFrom(type)) return Collections.emptyList();
        if (Map.class.isAssignableFrom(type)) return Collections.emptyMap();

        // Массивы
        if (type.isArray()) return Array.newInstance(type.getComponentType(), 0);

        // Enum
        if (type.isEnum()) return type.getEnumConstants()[0];
        // Пользовательские классы - пытаемся создать через рефлексию
        try {
            return createInstance(type);
        } catch (Exception e) {
            return null; // Фолбэк
        }
    }

    public static Class<?> getGenericType(Field field) throws JsonException {
        Type genericType = field.getGenericType();

        // Если тип не параметризованный, возвращаем Object.class
        if (!(genericType instanceof ParameterizedType)) {
            return Object.class;
        }

        ParameterizedType pType = (ParameterizedType) genericType;
        Type[] actualTypeArguments = pType.getActualTypeArguments();


        // Берем первый аргумент (для List<T> это будет T)
        if (actualTypeArguments.length == 0) {
            return Object.class;
        }

        Type actualType = actualTypeArguments[0];
        // Получаем raw type (если parentType является ParameterizedType)
        Class<?> parentClass = getRawType(actualType);

        // Обрабатываем разные варианты Type
        if (actualType instanceof Class) {
            return (Class<?>) actualType;
        } else if (actualType instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) actualType).getRawType();
            if (rawType instanceof Class) {
                return (Class<?>) rawType;
            }
        } else if (actualType instanceof WildcardType) {
            // Обработка wildcard-типов (? extends SomeClass)
            Type[] upperBounds = ((WildcardType) actualType).getUpperBounds();
            if (upperBounds.length > 0 && upperBounds[0] instanceof Class) {
                return (Class<?>) upperBounds[0];
            }
        }

        throw new JsonException("Cannot determine generic type for field: " + field.getName());
    }

    // Вспомогательный метод для получения raw type из Type
    public static Class<?> getRawType(Type type) {
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return getRawType(((ParameterizedType) type).getRawType());
        } else if (type instanceof GenericArrayType) {
            // Для массивов возвращаем Object.class (можно изменить при необходимости)
            return Object.class;
        } else if (type instanceof TypeVariable<?>) {
            // Для type variables возвращаем верхнюю границу
            Type[] bounds = ((TypeVariable<?>) type).getBounds();
            return bounds.length > 0 ? getRawType(bounds[0]) : Object.class;
        } else if (type instanceof WildcardType) {
            // Для wildcard возвращаем верхнюю границу
            Type[] upperBounds = ((WildcardType) type).getUpperBounds();
            return upperBounds.length > 0 ? getRawType(upperBounds[0]) : Object.class;
        }
        return null;
    }

    public static List<Class<?>> findSubclasses(Type parentType, String packageName) {
        List<Class<?>> subclasses = new ArrayList<>();

        // Получаем raw type (если parentType является ParameterizedType)
        Class<?> parentClass = getRawType(parentType);

        if (parentClass == null) {
            throw new IllegalArgumentException("Invalid parent type provided");
        }

        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String path = packageName.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File directory = new File(resource.getFile());

                if (directory.exists()) {
                    for (File file : directory.listFiles()) {
                        if (file.getName().endsWith(".class")) {
                            String className = packageName + '.' +
                                    file.getName().substring(0, file.getName().length() - 6);
                            try {
                                Class<?> clazz = Class.forName(className);

                                if (parentClass.isAssignableFrom(clazz) && !clazz.equals(parentClass)) {
                                    subclasses.add(clazz);
                                }
                            } catch (ClassNotFoundException | NoClassDefFoundError e) {
                                // Пропускаем классы, которые не могут быть загружены
                                continue;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return subclasses;
    }
}
