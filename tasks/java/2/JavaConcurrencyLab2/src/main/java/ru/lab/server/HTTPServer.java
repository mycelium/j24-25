package ru.lab.server;

import org.apache.log4j.Logger;
import ru.lab.parser.JSONParser;
import ru.lab.server.exceptions.HttpListenerBadRequestException;
import ru.lab.server.exceptions.HttpListenerNotFoundException;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiFunction;

public class HTTPServer {
    // Настройка сервера
    private final ServerSocketChannel channel;

    // Настройка пула потоков
    private final ExecutorService executors;

    private final Map<String, UriListener> listeners = new ConcurrentHashMap<>();
    private ObjectMapper objectMapper = new HttpServerBaseMapper();

    private final Logger logger = Logger.getLogger(HTTPServer.class);
    private boolean isRunning = false;

    /**
     * Creates an HTTP server that listens on the specified host and port.
     *
     * @param host          The host on which the server will run.
     * @param port          The port on which the server will run.
     * @param isVirtual     If true, a virtual thread pool is used; otherwise, a regular thread pool is used.
     * @param numberOfThreads The number of threads in the pool.
     * @throws IOException If the server channel could not be created or configured.
     */
    public HTTPServer(
        String host,
        int port,
        boolean isVirtual,
        int numberOfThreads
    ) throws IOException {
        try{
            this.channel = ServerSocketChannel.open();
            this.channel.bind(new InetSocketAddress(host, port));
            this.channel.configureBlocking(false);
            logger.info("Server created on " + host + ":" + port + "...");
        }catch (IOException e){
            throw new IOException("Couldn't create channel for this host: " + host + "and port: " + port, e);
        }
        if (isVirtual){
            this.executors = Executors.newFixedThreadPool(numberOfThreads, Thread.ofVirtual().factory());
        }
        else{
            this.executors = Executors.newFixedThreadPool(numberOfThreads);
        }
    }

    /**
     * Starts the HTTP server, if it is not already running.
     * Creates a separate thread for processing incoming connections via Selector.
     *
     * @throws IllegalStateException если сервер уже запущен
     */
    public void start() {
        if (isRunning) {
            throw new IllegalStateException("Server is already running");
        }
        isRunning = true;
        this.executors.submit(() -> {
            try {
                addSelector();
            } catch (IOException e) {
                logger.error("Internal server error: " + e.getLocalizedMessage());
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Stops the HTTP server by closing:
     * - server channel
     * - pool of handler threads
     */
    public void stop() {
        isRunning = false;
        try {
            channel.close();
            executors.shutdown();
        } catch (IOException e) {
            logger.error("Error stopping server: " + e.getMessage());
        }
    }

    // Record classes for better request/response handling
    public record HttpRequest(MethodType method, String path,
                       Map<String, String> headers, String body) {}

    public static class HttpResponse{

        public int statusCode;
        public String statusMessage;
        public Map<String, String> headers;
        public Object body;

        public HttpResponse(int statusCode, String statusMessage,
                            Map<String, String> headers, Object body){
            this.statusCode = statusCode;
            this.statusMessage = statusMessage;
            this.headers = Objects.requireNonNullElseGet(headers, Map::of);
            this.body = body;
        }
    }

    /**
     * Implementation of a mapper for converting JSON to objects and vice versa.
     */
    private static class HttpServerBaseMapper implements ObjectMapper {
        /**
         * Deserializes a JSON string into an object of the specified type.
         *
         * @param body  The JSON string.
         * @param clazz The class of the object.
         * @return The deserialized object.
         */
        @Override
        public <T> T deserialize(String body, Class<T> clazz) {
            return JSONParser.readJsonToEntity(body, clazz);
        }

        /**
         * Serializes an object into a JSON string.
         *
         * @param object The object to serialize.
         * @return The JSON string.
         */
        @Override
        public String serialize(Object object) {
            return JSONParser.convertEntityToJSON(object);
        }
    }

    /**
     * Generates a unique key for registering request handlers.
     * Key format: "METHOD:PATH" (for example, "GET:/api/users")
     */
    private String generateListenerKey(MethodType methodType, String path) {
        return methodType.name() + ":" + path;
    }

    /**
     * Registers a request handler for the specified method and path.
     * Checks that there is no already registered handler for this method+path combination.
     *
     * @param methodType HTTP method
     * @param path URL path
     * @param listener request handler
     * @throws IllegalArgumentException if the handler for this method and path is already registered
     */
    private void registerListener(MethodType methodType, String path, UriListener listener) {
        String key = generateListenerKey(methodType, path);
        if (listeners.containsKey(key)) {
            throw new IllegalArgumentException("Listener already registered for " + methodType + " " + path);
        }
        listeners.put(key, listener);
    }

    /**
     * Registers a handler for GET requests at the specified path.
     *
     * @param path         The path to register the handler for.
     * @param responseType The type of object to be returned in the response.
     * @param listener     The function to be called to handle the request.
     * @param <Response>   The type of the response.
     */
    public <Response> void registerGetMethod(
            String path,
            Class<Response> responseType,
            BiFunction<Map<String, String>, Map<String, String>, HttpResponse> listener
    ){
        registerListener(MethodType.GET, path,
            new UriListener(path, MethodType.GET, null, responseType, listener, logger, objectMapper));
    }

    /**
     * Registers a handler for POST requests at the specified path.
     *
     * @param path         The path to register the handler for.
     * @param bodyType     The type of the request body.
     * @param responseType The type of object to be returned in the response.
     * @param listener     The function to be called to handle the request.
     * @param <Body>       The type of the request body.
     * @param <Response>   The type of the response body.
     */
    public <Body, Response> void registerPostMethod(
            String path,
            Class<Body> bodyType,
            Class<Response> responseType,
            TriFunction<Map<String, String>, Map<String, String>, Body, HttpResponse> listener){
        registerListener(MethodType.POST, path,
                new UriListener(path, MethodType.POST, bodyType, responseType, listener, logger, objectMapper));
    }

    /**
     * Registers a handler for PUT requests at the specified path.
     *
     * @param path         The path to register the handler for.
     * @param bodyType     The type of the request body.
     * @param responseType The type of object to be returned in the response.
     * @param listener     The function to be called to handle the request.
     * @param <Body>       The type of the request body.
     * @param <Response>   The type of the response Body.
     */
    public <Body, Response> void registerPutMethod(
            String path,
            Class<Body> bodyType,
            Class<Response> responseType,
            TriFunction<Map<String, String>, Map<String, String>, Body, HttpResponse> listener    ){
        registerListener(MethodType.PUT, path,
                new UriListener(path, MethodType.PUT, bodyType, responseType, listener, logger, objectMapper));
    }

    /**
     * Registers a handler for PATCH requests at the specified path.
     *
     * @param path         The path to register the handler for.
     * @param bodyType     The type of the request body.
     * @param responseType The type of object to be returned in the response.
     * @param listener     The function to be called to handle the request.
     * @param <Body>       The type of the request body.
     * @param <Response>   The type of the response body.
     */
    public <Body, Response> void registerPatchMethod(
            String path,
            Class<Body> bodyType,
            Class<Response> responseType,
            TriFunction<Map<String, String>, Map<String, String>, Body, HttpResponse> listener
    ){
        registerListener(MethodType.PATCH, path,
                new UriListener(path, MethodType.PATCH, bodyType, responseType, listener, logger, objectMapper));
    }

    /**
     * Registers a handler for DELETE requests at the specified path.
     *
     * @param path         The path to register the handler for.
     * @param responseType The type of object to be returned in the response.
     * @param listener     The function to be called to handle the request.
     * @param <Response>   The type of the response body.
     */
    public <Response> void registerDeleteMethod(
            String path,
            Class<Response> responseType,
            BiFunction<Map<String, String>, Map<String, String>, HttpResponse> listener
    ){
        registerListener(MethodType.DELETE, path,
                new UriListener(path, MethodType.DELETE, null, responseType, listener, logger, objectMapper));
    }

    /**
     * Starts the event loop using a Selector.
     * Handles new connections and incoming data.
     *
     * @throws IOException If an I/O error occurs while working with the Selector.
     */
    private void addSelector() throws IOException {
        // Создаём Selector для управления несколькими каналами
        Selector selector = Selector.open();
        channel.register(selector, SelectionKey.OP_ACCEPT); // Регистрируем канал для принятия подключений

        while (true) {
            selector.select(); // Блокируемся, пока не появятся события
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();

                if (key.isAcceptable()) {
                    // Принимаем новое подключение
                    SocketChannel clientChannel = channel.accept();
                    clientChannel.configureBlocking(false);
                    clientChannel.register(selector, SelectionKey.OP_READ); // Регистрируем канал для чтения
                    logger.info("New connection accepted: " + clientChannel.getRemoteAddress());
                } else if (key.isReadable()) {
                    // Обрабатываем входящие данные в отдельном потоке
                    SocketChannel clientChannel = (SocketChannel) key.channel();
                    try {
                        HttpRequest request = readRequest(clientChannel);
                        logger.info("Received request: " + request);
                        executors.submit(() -> handleClientRequest(request, clientChannel));
                    } catch (IOException e) {
                        logger.error("Error reading request: " + e.getMessage());
                        clientChannel.close();
                    }
                }

                keyIterator.remove(); // Удаляем обработанный ключ
            }
        }
    }

    /**
     * Reads an HTTP request from the client channel.
     *
     * @param clientChannel The client channel.
     * @return HttpRequest.
     * @throws IOException If an I/O error occurs while reading the data.
     */
    private HttpRequest readRequest(SocketChannel clientChannel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        StringBuilder requestBuilder = new StringBuilder();

        while (true) {
            int bytesRead = clientChannel.read(buffer);
            if (bytesRead == -1) {
                clientChannel.close();
                throw new IOException("Client closed connection");
            }

            buffer.flip();
            requestBuilder.append(new String(buffer.array(), 0, bytesRead));
            buffer.clear();

            if (requestBuilder.toString().contains("\r\n\r\n")) {
                String requestStr = requestBuilder.toString();
                return new HttpRequest(
                        HTTPRequestParser.getMethodType(requestStr),
                        HTTPRequestParser.getPath(requestStr),
                        HTTPRequestParser.getHeaders(requestStr),
                        HTTPRequestParser.getBody(requestStr)
                );
            }
        }
    }

    /**
     * Processes an HTTP request and generates a response.
     *
     * @param request      HttpRequest.
     * @param clientChannel The client channel.
     */
    private void handleClientRequest(HttpRequest request, SocketChannel clientChannel) {
        try {
            // Формируем ответ
            HttpResponse response;
            try {
                String listenerKey = generateListenerKey(request.method(), request.path());
                UriListener listener = listeners.get(listenerKey);
                if (listener == null) {
                    throw new HttpListenerNotFoundException("No listener was found for this path.");
                }
                Map<String, String> pathVariables = HTTPRequestParser.extractPathVariables(listener.path, request.path());

                switch (listener.type){
                    case GET, DELETE -> response = listener.execute(request.headers(), pathVariables);
                    case PUT, POST, PATCH -> response = listener.execute(request.headers(), pathVariables, request.body());
                    default -> throw new IllegalArgumentException("Unsupported HTTP method: " + request);
                }

            } catch (HttpListenerBadRequestException | IllegalArgumentException e) {
                logger.error("Listener not found: " + e.getMessage(), e);
                response = new HttpResponse(400, "Bad Request",
                        Map.of("Content-Type", "text/plain"), e.getMessage());
            } catch (HttpListenerNotFoundException e) {
                logger.error("Listener not found: " + e.getMessage(), e);
                response = new HttpResponse(404, "Not Found",
                        Map.of("Content-Type", "text/plain"), "Resource not found");
            } catch (Exception e){
                logger.error("Error processing request: " + e.getMessage(), e);
                response = new HttpResponse(500, "Internal Server Error",
                        Map.of("Content-Type", "text/plain"), "An error occurred: " + e.getMessage());
            }

            logger.info("Generated response: " + response);

            sendResponse(clientChannel, response);

            // Закрываем соединение после отправки ответа
            logger.info("Response sent to client: " + clientChannel.getRemoteAddress());
            clientChannel.close();
        } catch (IOException e) {
            logger.error("Error handling request: " + e.getMessage());
        }
    }

    /**
     * Sends an HTTP response to the client through the specified channel.
     * Generates a valid HTTP response, including:
     * - the status bar
     * - headlines
     * - the body of the response
     * Supports both text and binary data.
     *
     * @param clientChannel the channel for sending the response
     * @param response response data (status, headers, body)
     * @throws IOException when writing errors to the channel
     */
    private void sendResponse(SocketChannel clientChannel, HttpResponse response) throws IOException {
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("HTTP/1.1 ")
                .append(response.statusCode)
                .append(" ")
                .append(response.statusMessage)
                .append("\r\n");

        for (Map.Entry<String, String> header : response.headers.entrySet()) {
            responseBuilder.append(header.getKey())
                    .append(": ")
                    .append(header.getValue())
                    .append("\r\n");
        }

        byte[] bodyBytes;
        if (response.body instanceof byte[]) {
            bodyBytes = (byte[]) response.body;
        } else {
            String bodyStr = response.body.toString();
            bodyBytes = bodyStr.getBytes();
        }

        responseBuilder.append("Content-Length: ")
                .append(bodyBytes.length)
                .append("\r\n\r\n");

        ByteBuffer headerBuffer = ByteBuffer.wrap(responseBuilder.toString().getBytes());
        ByteBuffer bodyBuffer = ByteBuffer.wrap(bodyBytes);

        clientChannel.write(new ByteBuffer[]{headerBuffer, bodyBuffer});
    }

    /**
     * Sets the mapper for converting request and response bodies.
     *
     * @param mapper The mapper.
     */
    public void setBodyMapper(ObjectMapper mapper){
        this.objectMapper = mapper;
    }
}
