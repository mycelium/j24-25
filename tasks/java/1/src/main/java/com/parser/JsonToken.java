package com.parser;

class JsonToken {
    private final JsonTokenType type;
    private final String value;

    public JsonToken(JsonTokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public JsonTokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "JsonToken{" +
                "type=" + type +
                ", value='" + value + '\'' +
                '}';
    }
}