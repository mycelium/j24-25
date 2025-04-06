package ru.spbstu.telematics.httpserver;

import ru.spbstu.telematics.httpserver.exceptions.SameRouteException;
import ru.spbstu.telematics.httpserver.exceptions.ServerShutdownException;
import ru.spbstu.telematics.httpserver.exceptions.ServerStartupException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class represents an HTTP server. It handles server startup, shutdown,
 * request handling, and route management. The server listens for incoming connections,
 * processes HTTP requests, and sends back HTTP responses.
 */
public class Server {
    private final String host;
    private final int port;
    private final boolean isVirtual;
    private final ExecutorService executor;
    private ServerSocketChannel serverChannel;

    private final Map<RequestKey, HttpHandler> handlers = new ConcurrentHashMap<>();

    /**
     * Constructs a new Server instance with the specified host, port, number of threads,
     * and whether the server should use virtual threads for concurrent request handling.
     *
     * @param host The hostname or IP address to bind the server.
     * @param port The port number to bind the server.
     * @param threads The number of threads in the thread pool for handling requests.
     *                This is used only if the server is not virtual.
     * @param isVirtual A flag indicating whether to use virtual threads for request handling.
     *                  If true, the server will use a virtual thread per task executor,
     *                  otherwise a fixed thread pool will be used.
     */
    public Server(String host, int port, int threads, boolean isVirtual) {
        this.host = host;
        this.port = port;
        this.isVirtual = isVirtual;
        this.executor = isVirtual ? Executors.newVirtualThreadPerTaskExecutor() : Executors.newFixedThreadPool(threads);
    }

    /**
     * Registers a handler for a specific HTTP method and path combination.
     * If a handler for the same method and path already exists, a SameRouteException is thrown.
     *
     * @param method The HTTP method (e.g., "GET", "POST").
     * @param path The path of the route.
     * @param handler The handler that will process requests for the specified method and path.
     * @throws SameRouteException If a handler already exists for the same method and path.
     */
    public void addHandler(String method, String path, HttpHandler handler) throws SameRouteException {
        RequestKey key = new RequestKey(method, path);
        if (handlers.containsKey(key)) {
            throw new SameRouteException(method, path);
        }
        handlers.put(key, handler);
    }

    /**
     * Starts the server, binds it to the specified host and port, and listens for incoming connections.
     * It processes the requests concurrently using a thread pool.
     *
     * @throws IOException If an error occurs while starting the server.
     * @throws ServerStartupException If an error occurs during server startup.
     */
    public void start() throws IOException, ServerStartupException {
        try {
            serverChannel = ServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress(host, port));
            System.out.println("Сервер запущен на " + host + ":" + port);

            while (!Thread.currentThread().isInterrupted()) {
                SocketChannel clientSocket = serverChannel.accept();
                if (clientSocket != null) {
                    executor.submit(() -> handleConnection(clientSocket));
                }
            }
        } catch (IOException e) {
            // Чтобы не было исключения при остановке сервера
            if (serverChannel == null || !serverChannel.isOpen()) {
                System.out.println("Сервер завершил работу.");
            } else {
                throw new ServerStartupException("Ошибка при запуске сервера", e);
            }
        }
    }


    /**
     * Handles an incoming client connection. Reads the HTTP request, processes it,
     * and sends an appropriate HTTP response back to the client.
     *
     * @param clientSocket The socket channel representing the client connection.
     */
    private void handleConnection(SocketChannel clientSocket) {
        try {
            // Чтение запроса из канала
            ByteBuffer buffer = ByteBuffer.allocate(8192);
            int bytesRead = clientSocket.read(buffer);
            if (bytesRead <= 0) {
                clientSocket.close();
                return;
            }
            buffer.flip();
            String requestData = StandardCharsets.UTF_8.decode(buffer).toString();

            // Парсинг HTTP запроса
            HttpRequest request = HttpRequest.parse(requestData);
            RequestKey key = new RequestKey(request.getMethod(), request.getPath());
            HttpHandler handler = handlers.get(key);
            HttpResponse response;

            if (handler != null) {
                response = handler.handle(request);
            } else {
                response = new HttpResponse();
                response.setStatus(404);
                response.setBody("Not Found");
            }

            // Отправка ответа клиенту
            clientSocket.write(ByteBuffer.wrap(response.toBytes()));
        } catch (Exception e) {
            System.err.println("Ошибка в обработке соединения: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException ignored) {}
        }
    }

    /**
     * Stops the server and shuts down the resources, including the thread pool and server socket channel.
     *
     * @throws ServerShutdownException If an error occurs while shutting down the server.
     */
    public void stop() throws ServerShutdownException {
        try {
            if (serverChannel != null && serverChannel.isOpen()) {
                serverChannel.close();
            }
            executor.shutdown();
            System.out.println("Сервер остановлен.");
        } catch (IOException e) {
            throw new ServerShutdownException("Ошибка при остановке сервера: ", e);
        }
    }
}
