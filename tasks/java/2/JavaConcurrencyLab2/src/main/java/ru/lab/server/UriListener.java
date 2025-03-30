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

    HTTPServer.HttpResponse execute(Map<String, String> headers, Map<String, String> pathVariables, String body){
        logger.debug("Has got request with pathVariables: " + pathVariables.toString() + " and body: " + body);
        TriFunction<Map<String, String>, Map<String, String>, Object, HTTPServer.HttpResponse> listener
                = (TriFunction<Map<String, String>, Map<String, String>, Object, HTTPServer.HttpResponse>) this.listener;
        Object obj;
        try{
            obj = mapper.deserialize(body, bodyType);
        }catch (Exception e){
            throw new HttpListenerBadRequestException("Syntax error found in the request body");
        }
        HTTPServer.HttpResponse response = listener.apply(headers, pathVariables, obj);
        response.body = mapper.serialize(response.body);
        return response;
    }

    HTTPServer.HttpResponse execute(Map<String, String> headers, Map<String, String> pathVariables){
        logger.debug("Has got request with pathVariables: " + pathVariables.toString());
        BiFunction<Map<String, String>, Map<String, String>, HTTPServer.HttpResponse> listener
                = (BiFunction<Map<String, String>, Map<String, String>, HTTPServer.HttpResponse>) this.listener;
        HTTPServer.HttpResponse response = listener.apply(headers, pathVariables);

        response.body = mapper.serialize(response.body);
        return response;
    }

}
