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
Image loading
To send files using the implemented server, you need to get an image in the form of an array of bytes,
which will later need to be passed as a body to the HttpResponse object.
```java
server.registerGetMethod("/image", byte[].class, (headers, pathVars) -> {
            try {
                // Читаем файл изображения в байты
                Path imagePath = Paths.get("C:\\Users\\bogda\\j24-25\\tasks\\java\\2\\JavaConcurrencyLab2\\image\\cat.jpg");
                byte[] imageData = Files.readAllBytes(imagePath);

                // Создаем ответ с правильными заголовками
                return new HTTPServer.HttpResponse(
                        200,
                        "OK",
                        Map.of(
                                "Content-Type", "image/jpeg",
                                "Content-Disposition", "inline"
                        ),
                        imageData  // Тело ответа - байты изображения
                );
            } catch (IOException e) {
                return new HTTPServer.HttpResponse(
                        500,
                        "Internal Server Error",
                        Map.of("Content-Type", "text/plain"),
                        "Error loading image: " + e.getMessage()
                );
            }
        });
```
After executing a Get request along this path, you will receive an array of bytes of this image. The client only needs to get the Content-Type of the image and save the byte array as an image with this type. Example of a client for uploading an image:
```java
public static void execute() {
        String imageUrl = "http://localhost:30001/image";
        String savePath = "C:\\Users\\bogda\\j24-25\\tasks\\java\\2\\JavaConcurrencyLab2\\image\\";  // Путь для сохранения

        try {
            System.out.println("Downloading image from " + imageUrl);

            // Создаем соединение
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Проверяем код ответа
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Получаем тип контента
                String contentType = connection.getContentType();
                System.out.println("Content-Type: " + contentType);

                // Получаем размер файла
                long fileSize = connection.getContentLengthLong();
                System.out.println("File size: " + fileSize + " bytes");

                // Определяем расширение файла по Content-Type
                String extension = getFileExtension(contentType);
                if (extension != null) {
                    savePath += "downloaded_image" + extension;
                }

                // Скачиваем файл
                try (InputStream in = connection.getInputStream();
                     FileOutputStream out = new FileOutputStream(savePath)) {

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    long totalRead = 0;

                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                        totalRead += bytesRead;

                        // Выводим прогресс
                        if (fileSize > 0) {
                            int progress = (int) (totalRead * 100 / fileSize);
                            System.out.print("\rDownload progress: " + progress + "%");
                        }
                    }
                    System.out.println("\nDownload completed!");
                }
            } else {
                System.err.println("Server returned HTTP code: " + responseCode);
                try (InputStream errorStream = connection.getErrorStream()) {
                    if (errorStream != null) {
                        System.err.println("Error response: " + new String(errorStream.readAllBytes()));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error downloading image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Определяем расширение файла по Content-Type
    private static String getFileExtension(String contentType) {
        if (contentType == null) return null;

        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            case "image/svg+xml" -> ".svg";
            default -> null;
        };
    }
```
Alternatively, you can open the site with this path in the browser.

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
