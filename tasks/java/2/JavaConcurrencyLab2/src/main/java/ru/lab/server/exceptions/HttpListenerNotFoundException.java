package ru.lab.server.exceptions;

public class HttpListenerNotFoundException extends HttpServerException{
    public HttpListenerNotFoundException(String message){
        super(404, "Not Found", message);
    }
}
