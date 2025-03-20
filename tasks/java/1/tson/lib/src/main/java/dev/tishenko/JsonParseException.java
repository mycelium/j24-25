package dev.tishenko;

public class JsonParseException extends RuntimeException {
    public JsonParseException(String msg) {
        super(msg);
    }

    public JsonParseException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
