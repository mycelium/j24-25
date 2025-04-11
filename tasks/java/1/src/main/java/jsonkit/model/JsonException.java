package jsonkit.model;

public class JsonException extends Exception {
    public JsonException(String message) { super(message); }
    public JsonException(String message, Throwable cause) { super(message, cause); }
}
