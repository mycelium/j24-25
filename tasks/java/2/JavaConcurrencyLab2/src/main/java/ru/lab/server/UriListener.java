package ru.lab.server;

import org.apache.log4j.Logger;
import ru.lab.server.exceptions.HttpListenerBadRequestException;

import java.util.Map;
import java.util.function.BiFunction;

class UriListener {
    String path;
    MethodType type;
    Object listener;
    Class<?> bodyType;
    Class<?> responseType;
    Logger logger;
    ObjectMapper mapper;

    UriListener(
            String path,
            MethodType type,
            Class<?> bodyType,
            Class<?> responseType,
            Object listener,
            Logger logger,
            ObjectMapper mapper
    ){
        this.path = path;
        this.type = type;
        this.listener = listener;
        this.bodyType = bodyType;
        this.responseType = responseType;
        this.logger = logger;
        this.mapper = mapper;
    }

    ResponseData execute(Map<String, String> headers, Map<String, String> pathVariables, String body){
        logger.debug("Has got request with pathVariables: " + pathVariables.toString() + " and body: " + body);
        TriFunction<Map<String, String>, Map<String, String>, Object, Object> listener = (TriFunction<Map<String, String>, Map<String, String>, Object, Object>) this.listener;
        Object obj;
        try{
            obj = mapper.deserialize(body, bodyType);
        }catch (Exception e){
            throw new HttpListenerBadRequestException("Syntax error found in the request body");
        }
        Object response = listener.apply(headers, pathVariables, obj);
        String contentType = determineContentType(response);
        if (response instanceof byte[]) {
            return new ResponseData(response, contentType);
        } else {
            String responseBody = mapper.serialize(response);
            return new ResponseData(responseBody, contentType);
        }
    }

    ResponseData execute(Map<String, String> headers, Map<String, String> pathVariables){
        logger.debug("Has got request with pathVariables: " + pathVariables.toString());
        BiFunction<Map<String, String>, Map<String, String>, Object> listener = (BiFunction<Map<String, String>, Map<String, String>, Object>) this.listener;
        Object response = listener.apply(headers, pathVariables);
        String contentType = determineContentType(response);
        if (response instanceof byte[]) {
            return new ResponseData(response, contentType);
        } else {
            String responseBody = mapper.serialize(response);
            return new ResponseData(responseBody, contentType);
        }
    }

    // Auxiliary class for storing response data
    record ResponseData(Object body, String contentType) { }


    private String determineContentType(Object response) {
        if (response == null) {
            return "text/plain";
        }

        if (response instanceof String) {
            return "text/plain";
        } else if (response instanceof byte[]) {
            return "application/octet-stream";
        } else {
            // Для всех остальных случаев считаем, что это JSON
            return "application/json";
        }
    }

}
