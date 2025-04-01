package ru.lab.server.exceptions;

public class HttpListenerBadRequestException extends HttpServerException{
    public HttpListenerBadRequestException(String message){
        super(400, "Bad Request", message);
    }
}
