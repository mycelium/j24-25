package com.parser;

import java.util.Map;

public interface JsonParser {
    /**
     * Parses a JSON string into a Java object.
     *
     * @param json The JSON string to parse.
     * @return A Java object representation of the JSON.
     * @throws JsonParseException If the JSON is invalid or cannot be parsed.
     */
    Object parse(String json) throws JsonParseException;

    /**
     * Parses a JSON string into a Map.
     *
     * @param json The JSON string to parse.
     * @return A Map representation of the JSON.
     * @throws JsonParseException If the JSON is invalid or cannot be parsed.
     */
    Map<String, Object> parseToMap(String json) throws JsonParseException;

    /**
     * Parses a JSON string into a specified class.
     *
     * @param json The JSON string to parse.
     * @param clazz The class to parse the JSON into.
     * @param <T> The type of the class.
     * @return An instance of the specified class populated with JSON data.
     * @throws JsonParseException If the JSON is invalid or cannot be parsed.
     */
    <T> T parseToClass(String json, Class<T> clazz) throws JsonParseException;

    /**
     * Converts a Java object to a JSON string.
     *
     * @param object The object to convert to JSON.
     * @return A JSON string representation of the object.
     * @throws JsonParseException If the object cannot be converted to JSON.
     */
    String toJson(Object object) throws JsonParseException;
}