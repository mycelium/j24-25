package ru.spbstu.telematics.httpserver;

import java.util.Objects;

/**
 * Represents a combination of HTTP method and path used as a key for storing and retrieving route handlers.
 * This class overrides the `equals` and `hashCode` methods to ensure that requests with the same method and path
 * are considered equal, making it suitable for use as a key in collections like `HashMap`.
 */
public class RequestKey {
    private final String method;
    private final String path;

    /**
     * Constructs a new {@link RequestKey} with the given HTTP method and path.
     *
     * @param method The HTTP method (e.g., "GET", "POST").
     * @param path The HTTP path (e.g., "/index").
     */
    public RequestKey(String method, String path) {
        this.method = method;
        this.path = path;
    }

    /**
     * Compares this {@link RequestKey} to another object for equality.
     * Two {@link RequestKey} objects are considered equal if they have the same HTTP method and path.
     *
     * @param o The object to compare this {@link RequestKey} to.
     * @return true if the object is equal to this {@link RequestKey}, otherwise false.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RequestKey))
            return false;
        RequestKey that = (RequestKey) o;
        return method.equals(that.method) && path.equals(that.path);
    }


    /**
     * Returns a hash code value for this {@link RequestKey}.
     * The hash code is based on the HTTP method and path to ensure consistent hashing.
     *
     * @return The hash code value for this {@link RequestKey}.
     */
    @Override
    public int hashCode() {
        return Objects.hash(method, path);
    }
}
