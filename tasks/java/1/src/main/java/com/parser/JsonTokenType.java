package com.parser;

enum JsonTokenType {
    OBJECT_START,
    OBJECT_END,
    ARRAY_START,
    ARRAY_END,
    COMMA,
    COLON,
    STRING,
    NUMBER,
    BOOLEAN,
    NULL
}