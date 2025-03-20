package com.server;

/**
 * Represents a route with an HTTP method and path.
 */
public class Route {
    private final String method;
    private final String path;

    /**
     * Constructs a Route object.
     *
     * @param method the HTTP method
     * @param path   the request path
     */
    public Route(String method, String path) {
        this.method = method;
        this.path = path;
    }

    // Getters, equals, and hashCode methods

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Route route = (Route) o;

        if (!method.equals(route.method)) return false;
        return path.equals(route.path);
    }

    @Override
    public int hashCode() {
        int result = method.hashCode();
        result = 31 * result + path.hashCode();
        return result;
    }
}
