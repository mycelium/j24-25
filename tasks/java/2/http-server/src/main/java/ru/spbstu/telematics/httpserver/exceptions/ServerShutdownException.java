package ru.spbstu.telematics.httpserver.exceptions;

public class ServerShutdownException extends Exception {
    public ServerShutdownException(String message, Throwable cause) {
        super(message, cause);
    }
}
