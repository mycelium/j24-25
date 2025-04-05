package ru.spbstu.telematics.httpserver;

import java.util.Objects;

public class RequestKey {
    private final String method;
    private final String path;

    public RequestKey(String method, String path) {
        this.method = method;
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RequestKey))
            return false;
        RequestKey that = (RequestKey) o;
        return method.equals(that.method) && path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, path);
    }
}
