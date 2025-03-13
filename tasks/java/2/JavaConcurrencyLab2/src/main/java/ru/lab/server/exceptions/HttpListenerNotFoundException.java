package ru.lab.server.exceptions;

public class HttpListenerNotFoundException extends RuntimeException{
    public HttpListenerNotFoundException(String message){
        super(message);
    }
}
