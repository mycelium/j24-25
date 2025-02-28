package ru.spbstu.telematics.json.jsonreader;

public class JsonValue {
    public enum JsonType {
        STRING,
        NUMBER,
        BOOLEAN,
        NULL,
        ARRAY,
        OBJECT
    }

    private JsonType type;
    private String value;

    public JsonValue(JsonType type, String value) {
        this.type = type;
        this.value = value;
    }

    public JsonType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
