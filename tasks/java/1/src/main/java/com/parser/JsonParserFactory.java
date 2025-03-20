package com.parser;

public class JsonParserFactory {
    private static final JsonParser INSTANCE = new JsonParserImpl();

    private JsonParserFactory() {}

    public static JsonParser getParser() {
        return INSTANCE;
    }
}