# HTTP Server
This project is a simple HTTP server implemented in Java using NIO (Non-blocking I/O) and multi-threading. It supports handling HTTP requests (GET, POST, PUT, PATCH, DELETE) and can be extended to handle custom routes and logic. The server is designed to be lightweight, efficient, and easy to use.

## Features
* Non-blocking I/O: Uses ServerSocketChannel and Selector for efficient handling of multiple connections.
* Multi-threading: Supports both platform threads and virtual threads (Java 19+).
* Customizable routes: Register handlers for specific HTTP methods and paths.
* JSON support: Built-in JSON parsing and serialization using a custom ObjectMapper.
* Error handling: Returns appropriate HTTP error responses (e.g., 400, 404, 500) with detailed messages.
* Logging: Integrated with Log4j for logging server events and errors.

## Prerequisites
* Java 19 or higher: Required for virtual thread support.
* Maven: For building and managing dependencies.
* Log4j: For logging (included in the project).


## Usage
### Starting the Server
To start the server, create an instance of HTTPServer and specify the host, port, and thread pool configuration:
``` java
public class Main {
    public static void main(String[] args) {
        try {
            HTTPServer server = new HTTPServer("localhost", 8080, true, 10);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```
* Host: The hostname or IP address to bind the server to (e.g., localhost).
* Port: The port to listen on (e.g., 8080).
* Virtual Threads: Set to true to use virtual threads (Java 19+), or false for platform threads.
* Number of Threads: The size of the thread pool.

### Registering Routes
You can register handlers for specific HTTP methods and paths using the following methods:

GET Request
```java
server.registerGetMethod(
    "/api/data",
    Response.class,
    (headers, pathVariables) -> {
        // Handle GET request
        return new Response("Data retrieved successfully");
    }
);
```

POST Request
```java
server.registerPostMethod(
    "/api/data",
    RequestBody.class,
    Response.class,
    (headers, pathVariables, body) -> {
        // Handle POST request
        return new Response("Data created successfully");
    }
);
```
PUT Request
```java
server.registerPutMethod(
    "/api/data/{id}",
    RequestBody.class,
    Response.class,
    (headers, pathVariables, body) -> {
        // Handle PUT request
        String id = pathVariables.get("id");
        return new Response("Data updated for ID: " + id);
    }
);
```
PATCH Request
```java
server.registerPatchMethod(
    "/api/data/{id}",
    RequestBody.class,
    Response.class,
    (headers, pathVariables, body) -> {
        // Handle PATCH request
        String id = pathVariables.get("id");
        return new Response("Data partially updated for ID: " + id);
    }
);
```
DELETE Request
```java
server.registerDeleteMethod(
    "/api/data/{id}",
    Response.class,
    (headers, pathVariables) -> {
        // Handle DELETE request
        String id = pathVariables.get("id");
        return new Response("Data deleted for ID: " + id);
    }
);
```

## Customizing the Mapper
By default, the server uses a built-in JSON mapper. You can replace it with a custom implementation:

```java
server.setBodyMapper(new CustomMapper());
```

## Error Handling
The server automatically handles common errors and returns appropriate HTTP responses:
* 400 Bad Request: Invalid request format or missing parameters.
* 404 Not Found: No handler registered for the requested path.
* 500 Internal Server Error: Unexpected server error.
