package ru.lab.server.exceptions;

public class HttpServerException extends RuntimeException {
    public int statusCode;
    public String statusMessage;
    public HttpServerException(int statusCode, String statusMessage, String message) {
        super(message);
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }
}
