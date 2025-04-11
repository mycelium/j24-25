
package jsonkit.core;

import jsonkit.model.JsonException;

import java.util.Map;

public interface JsonCustomDeserializer<T> {
    T deserialize(Map<String, Object> jsonMap) throws JsonException;
}
