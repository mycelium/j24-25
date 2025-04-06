package ru.spbstu.hsai.httpserver.common;

public enum HttpMethods {
    GET,
    POST,
    PUT,
    PATCH,
    DELETE;

    public static HttpMethods convertFromString(String method) {
        try {
            return HttpMethods.valueOf(method.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
