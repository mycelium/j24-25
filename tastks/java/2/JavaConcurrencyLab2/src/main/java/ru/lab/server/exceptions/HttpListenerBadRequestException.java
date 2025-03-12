package ru.lab.server.exceptions;

public class HttpListenerBadRequestException extends RuntimeException{
    public HttpListenerBadRequestException(String message){
        super(message);
    }
}
