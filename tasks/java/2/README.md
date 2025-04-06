# HTTP Server

Simple HTTP Server in Java, implementing the basic part of the HTTP/1.1 protocol using NIO (ServerSocketChannel). The library supports the registration of route handlers, multithreaded request processing, and the use of custom exceptions for error management.

## Key Components

### Server.java
The main server class. It is responsible for:
- Creating the server on the specified host and port;
- Registering route handlers via the method `addHandler(String method, String path, HttpHandler handler)`.
    - If an attempt is made to register a handler for an already existing route, it throws a `SameRouteException`.
- Starting the server using the `start()` method. In case of a startup error, it throws a `ServerStartupException`.
- Correctly stopping the server using the `stop()` method, which throws a `ServerShutdownException` in case of error.

Example usage:

```java
Server server = new Server("localhost", 8080, 4, false);

// Register a handler for the GET /hello route
server.addHandler("GET", "/hello", request -> {
    HttpResponse response = new HttpResponse();
    response.setBody("Hello, world!");
    return response;
});

// Start the server
server.start();

// To stop the server
server.stop();
```

### HttpRequest.java

A class for representing an HTTP request. It provides:

- Parsing of a raw HTTP request string to extract the method, path, headers, and body.

- A method `parseJson()` that attempts to parse the request body as JSON and returns a `Map<String, Object>`.

    - If parsing fails, a `JsonParsingException` is thrown.

### HttpResponse.java

A class for constructing an HTTP response. It allows:

- Setting the status code, status message, headers, and response body.

- Supports setting the body from a string, byte array, or file with a specified `Content-Type`.

- The `toBytes()` method combines headers and body into a single byte array for sending to the client.

### RequestKey.java

A class that encapsulates a combination of HTTP method and path. It is used as a key to store route handlers in the server.

### HttpHandler.java

A functional interface that allows implementing HTTP request handling using lambda expressions or dedicated classes.

### Exceptions

To improve debugging and error handling, the project defines the following custom exceptions:

- `JsonParsingException` – thrown when JSON parsing fails in `HttpRequest`.

- `SameRouteException` – thrown when trying to register a handler for a route that already exists.

- `ServerStartupException` – thrown when the server fails to start (e.g., if the port is already in use).

- `ServerShutdownException` – thrown when an error occurs during server shutdown.
