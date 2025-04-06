package ru.spbstu.telematics.httpserver.exceptions;

public class JsonParsingException extends Exception {
    public JsonParsingException(Throwable cause) {
        super("Failed to parse JSON", cause);
    }
}
