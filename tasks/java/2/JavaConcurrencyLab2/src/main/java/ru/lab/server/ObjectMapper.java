package ru.lab.server;

public interface ObjectMapper {
    <T> T deserialize(String body, Class<T> clazz);
    <T> String serialize(T object);
}
