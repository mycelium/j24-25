package jsonkit.core;

import jsonkit.parser.JsonParser;
import jsonkit.mapper.ObjectMapper;
import jsonkit.model.JsonException;
import java.util.Map;

public final class JsonLibrary {
    private JsonLibrary() {}

    public static Object parse(String json) throws JsonException {
        return JsonParser.parse(json);
    }

    public static Map<String, Object> parseToMap(String json) throws JsonException {
        Object result = parse(json);
        if (result instanceof Map) {
            return (Map<String, Object>) result;
        }
        throw new JsonException("JSON root is not an object");
    }

    public static <T> T parseToClass(String json, Class<T> targetClass) throws JsonException {
        return ObjectMapper.mapToClass(json, targetClass);
    }
}