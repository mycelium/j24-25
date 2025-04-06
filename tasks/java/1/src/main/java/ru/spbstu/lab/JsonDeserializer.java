package ru.spbstu.lab;

import java.util.Map;
import java.util.function.BiFunction;

public interface JsonDeserializer<T> {
    /**
     * Метод для преобразования JSON-узла в объект типа T.
     * @param map входные данные (Разложенная в map JSON строка)
     * @return десериализованный объект
     */
    T deserialize(Map<String, Object> map);
}
