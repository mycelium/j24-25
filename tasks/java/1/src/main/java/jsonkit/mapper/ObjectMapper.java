package jsonkit.mapper;

import jsonkit.model.JsonException;
import jsonkit.parser.JsonParser;
import jsonkit.util.TypeUtils;
import java.lang.reflect.*;
import java.util.*;

public class ObjectMapper {
    public static <T> T mapToClass(String json, Class<T> targetClass) throws JsonException {
        try {
            Object parsed = JsonParser.parse(json);

            if (TypeUtils.isSimpleType(targetClass)) {
                return (T) TypeUtils.convertValue(parsed, targetClass);
            }

            if (targetClass.isArray()) {
                return (T) TypeUtils.handleArrayConversion(parsed, targetClass);
            }

            if (Collection.class.isAssignableFrom(targetClass)) {
                return (T) TypeUtils.handleCollectionConversion(parsed, targetClass);
            }

            if (parsed instanceof Map) {
                return (T) TypeUtils.createInstance(targetClass, (Map<String, Object>) parsed);
            }

            return (T) parsed;
        } catch (Exception e) {
            throw new JsonException("Failed to parse to " + targetClass.getName(), e);
        }
    }
}