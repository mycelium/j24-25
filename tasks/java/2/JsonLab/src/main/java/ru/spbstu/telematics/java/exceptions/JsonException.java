package ru.spbstu.telematics.java.exceptions;

public class JsonException extends Exception {
  public JsonException(String message) {
    super(message);
  }
}

class JsonSerializationException extends JsonException {
  public JsonSerializationException(String message) {
    super(message);
  }
}