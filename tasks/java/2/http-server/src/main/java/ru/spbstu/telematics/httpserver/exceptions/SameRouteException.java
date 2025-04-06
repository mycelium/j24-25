package ru.spbstu.telematics.httpserver.exceptions;

public class SameRouteException extends Exception {
    public SameRouteException(String method, String path) {
      super("Маршрут уже существует: " + method + " " + path);
    }
}
