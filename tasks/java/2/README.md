# HTTP Server

This is a simple HTTP server implementation in Java, supporting part of the HTTP 1.1 protocol. The server uses `ServerSocketChannel` from the `java.nio` package and supports multithreading.

## Key Features

- Supported methods: GET, POST, PUT, PATCH, DELETE.
- Handling of request headers and body.
- Ability to add handlers for specific paths and methods.
- Multithreading support with configurable thread count.
- Option to use virtual threads.

## How to Use

### 1. Creating and Starting the Server

To create and start the server, use the `HttpServer` class. Example:

```java
HttpServer server = new HttpServer();

// Adding a handler for a GET request to the root path
server.addRoute("GET", "/", (request, response) -> {
    response.setStatus(200, "OK");
    response.setBody("<h1>Home Page</h1>");
    response.addHeader("Content-Type", "text/html");
});

// Starting the server on localhost:8080 with 4 threads and virtual threads
server.start("localhost", 8080, 4, true);
```
### 2. Adding Handlers
You can add handlers for different methods and paths. Example for a POST request:
```java
server.addRoute("POST", "/data", (request, response) -> {
    Map<String, Object> jsonData = request.parseJson();
    if (!jsonData.isEmpty()) {
        String id = UUID.randomUUID().toString();
        server.getDataStore().put(id, jsonData);
        response.setStatus(200, "OK");
        response.setBody("Data stored with ID: " + id);
    } else {
        response.setStatus(400, "Bad Request");
        response.setBody("Invalid or unsupported JSON data");
    }
    response.addHeader("Content-Type", "application/json");
});
```

### 3. Handling Requests with Parameters
To handle requests with parameters (e.g., /data/{id}), use headers:
```java 
server.addRoute("GET", "/data/{id}", (request, response) -> {
    String id = request.headers().get("X-Resource-ID");
    if (id == null) {
        response.setStatus(400, "Bad Request");
        response.setBody("ID not provided");
        return;
    }

    Map<String, Object> data = server.getDataStore().get(id);
    if (data != null) {
        response.setStatus(200, "OK");
        response.setBody("Data for ID " + id + ": " + data);
    } else {
        response.setStatus(404, "Not Found");
        response.setBody("No data found for ID " + id);
    }
    response.addHeader("Content-Type", "application/json");
});
```

## Examples
Usage examples can be found in the `Main` class and tests. The server supports JSON handling, data updates via PUT and PATCH, and data deletion via DELETE.

## Dependencies
- No external libraries are used.
- A built-in JSON parser is used (implemented in the JsonParser class).

