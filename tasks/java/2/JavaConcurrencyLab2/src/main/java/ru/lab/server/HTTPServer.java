package ru.lab.server;

import org.apache.log4j.Logger;
import ru.lab.json_parser.JSONParser;
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

    private final List<UriListener> listeners = new LinkedList<>();
    private ObjectMapper objectMapper = new HttpServerBaseMapper();

    private final Logger logger = Logger.getLogger(HTTPServer.class);

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
            BiFunction<Map<String, String>, Map<String, String>, Response> listener
    ){
        listeners.add(new UriListener(path, MethodType.GET, null, responseType, listener, logger, objectMapper));
    }

    /**
     * Registers a handler for POST requests at the specified path.
     *
     * @param path         The path to register the handler for.
     * @param bodyType     The type of the request body.
     * @param responseType The type of object to be returned in the response.
     * @param listener     The function to be called to handle the request.
     * @param <Body>       The type of the request body.
     * @param <Response>   The type of the response.
     */
    public <Body, Response> void registerPostMethod(
            String path,
            Class<Body> bodyType,
            Class<Response> responseType,
            TriFunction<Map<String, String>, Map<String, String>, Body, Response> listener    ){
        listeners.add(new UriListener(path, MethodType.POST, bodyType, responseType, listener, logger, objectMapper));
    }

    /**
     * Registers a handler for PUT requests at the specified path.
     *
     * @param path         The path to register the handler for.
     * @param bodyType     The type of the request body.
     * @param responseType The type of object to be returned in the response.
     * @param listener     The function to be called to handle the request.
     * @param <Body>       The type of the request body.
     * @param <Response>   The type of the response.
     */
    public <Body, Response> void registerPutMethod(
            String path,
            Class<Body> bodyType,
            Class<Response> responseType,
            TriFunction<Map<String, String>, Map<String, String>, Body, Response> listener    ){
        listeners.add(new UriListener(path, MethodType.PUT, bodyType, responseType, listener, logger, objectMapper));
    }

    /**
     * Registers a handler for PATCH requests at the specified path.
     *
     * @param path         The path to register the handler for.
     * @param bodyType     The type of the request body.
     * @param responseType The type of object to be returned in the response.
     * @param listener     The function to be called to handle the request.
     * @param <Body>       The type of the request body.
     * @param <Response>   The type of the response.
     */
    public <Body, Response> void registerPatchMethod(
            String path,
            Class<Body> bodyType,
            Class<Response> responseType,
            TriFunction<Map<String, String>, Map<String, String>, Body, Response> listener
    ){
        listeners.add(new UriListener(path, MethodType.PATCH, bodyType, responseType, listener, logger, objectMapper));
    }

    /**
     * Registers a handler for DELETE requests at the specified path.
     *
     * @param path         The path to register the handler for.
     * @param responseType The type of object to be returned in the response.
     * @param listener     The function to be called to handle the request.
     * @param <Response>   The type of the response.
     */
    public <Response> void registerDeleteMethod(
            String path,
            Class<Response> responseType,
            BiFunction<Map<String, String>, Map<String, String>, Response> listener
    ){
        listeners.add(new UriListener(path, MethodType.DELETE, null, responseType, listener, logger, objectMapper));
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
                    String request = getRequest(clientChannel);
                    logger.info("Received request: " + request);
                    executors.submit(() -> handleClientRequest(request, clientChannel));
                }

                keyIterator.remove(); // Удаляем обработанный ключ
            }
        }
    }

    /**
     * Reads an HTTP request from the client channel.
     *
     * @param clientChannel The client channel.
     * @return A string containing the HTTP request.
     * @throws IOException If an I/O error occurs while reading the data.
     */
    private String getRequest(SocketChannel clientChannel) throws IOException{
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        StringBuilder requestBuilder = new StringBuilder();

        while (true) {
            int bytesRead = clientChannel.read(buffer);
            if (bytesRead == -1) {
                // Клиент закрыл соединение
                clientChannel.close();
                logger.info("Connection closed by client: " + clientChannel.getRemoteAddress());
                throw new IOException("Client closed connection");
            }

            buffer.flip();
            requestBuilder.append(new String(buffer.array(), 0, bytesRead));
            buffer.clear();

            // Проверяем, достигнут ли конец запроса
            if (requestBuilder.toString().contains("\r\n\r\n")) {
                // Обрабатываем запрос
                return requestBuilder.toString();
            }
        }
    }

    /**
     * Processes an HTTP request and generates a response.
     *
     * @param request      A string containing the HTTP request.
     * @param clientChannel The client channel.
     */
    private void handleClientRequest(String request, SocketChannel clientChannel) {
        try {
            // Формируем ответ
            String response;
            String path = "";
            try {
                // извлекаем параметры запроса
                MethodType methodType = HTTPRequestParser.getMethodType(request);
                path = HTTPRequestParser.getPath(request);
                Map<String, String> headers = HTTPRequestParser.getHeaders(request);
                String body = HTTPRequestParser.getBody(request);
                UriListener listener = getUriListener(methodType, path);
                Map<String, String> pathVariables = HTTPRequestParser.extractPathVariables(listener.path, path);
                switch (methodType){
                    case GET, DELETE -> response = handleLessBodyRequests(headers, pathVariables, listener);
                    case PUT, POST, PATCH -> response = handleBodyRequests(headers, pathVariables, body, listener);
                    default -> throw new IllegalArgumentException("Unsupported HTTP method: " + request);
                }
            } catch (HttpListenerBadRequestException | IllegalArgumentException e) {
                logger.error("Listener not found: " + e.getMessage(), e);
                response = createErrorResponse(400, "Bad Request", e.getMessage() + " " + path);
            } catch (HttpListenerNotFoundException e) {
                logger.error("Listener not found: " + e.getMessage(), e);
                response = createErrorResponse(404, "Not Found", "Resource not found: " + path);
            } catch (Exception e){
                logger.error("Error processing request: " + e.getMessage(), e);
                response = createErrorResponse(500, "Internal Server Error", "An error occurred: " + e.getMessage());
            }

            logger.info("Generated response: " + response);

            ByteBuffer responseBuffer = ByteBuffer.wrap(handleRequest(response).getBytes());
            clientChannel.write(responseBuffer);

            // Закрываем соединение после отправки ответа
            logger.info("Response sent to client: " + clientChannel.getRemoteAddress());
            clientChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Processes requests that do not contain a body (GET, DELETE).
     *
     * @param headers      The request headers.
     * @param pathVariables The path variables.
     * @param listener     The request handler.
     * @return The response to the request.
     */
    private String handleLessBodyRequests(Map<String, String> headers, Map<String, String> pathVariables, UriListener listener){
        return listener.execute(headers, pathVariables);
    }

    /**
     * Processes requests that contain a body (POST, PUT, PATCH).
     *
     * @param headers      The request headers.
     * @param pathVariables The path variables.
     * @param body         The request body.
     * @param listener     The request handler.
     * @return The response to the request.
     */
    private String handleBodyRequests(Map<String, String> headers, Map<String, String> pathVariables, String body, UriListener listener){
        return listener.execute(headers, pathVariables, body);
    }

    /**
     * Returns the handler for the specified method and path.
     *
     * @param methodType The HTTP method type.
     * @param path       The request path.
     * @return The request handler.
     * @throws HttpListenerNotFoundException If no handler is found.
     */
    private UriListener getUriListener(MethodType methodType, String path){
        for (UriListener listener : listeners) {
            if (listener.type.equals(methodType) && listener.path.equals(path)
                    && HTTPRequestParser.comparePathWithPathTemplate(listener.path, path)) {
                return listener;
            }
        }
        throw new HttpListenerNotFoundException("No listener was found for this path.");
    }

    /**
     * Generates an HTTP response based on a string.
     *
     * @param response The string containing the response.
     * @return The HTTP response.
     */
    private static String handleRequest(String response) {
        // Простейший парсинг HTTP-запроса
        return "HTTP/1.1 200 OK\r\n\r\n" + response;
    }

    /**
     * Generates an HTTP error response.
     *
     * @param statusCode   The HTTP status code.
     * @param statusMessage The status message corresponding to the status code.
     * @param errorMessage The error message.
     * @return The HTTP error response.
     */
    private String createErrorResponse(int statusCode, String statusMessage, String errorMessage) {
        return "HTTP/1.1 " + statusCode + " " + statusMessage + "\r\n" +
                "Content-Type: text/plain\r\n" +
                "Content-Length: " + errorMessage.length() + "\r\n" +
                "\r\n" +
                errorMessage;
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
