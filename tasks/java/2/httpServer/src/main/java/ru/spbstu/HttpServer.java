package ru.spbstu;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    private ServerSocketChannel serverChannel;
    private ExecutorService executor;
    private final ConcurrentHashMap<RouteKey, RequestHandler> routes = new ConcurrentHashMap<>();
    private boolean isRunning;

    // Запись маршрута как record
    private record RouteKey(String method, String path) {
    }

    @FunctionalInterface
    public interface RequestHandler {
        void handle(HttpRequest request, HttpResponse response);
    }

    public void start(String host, int port, int threadCount, boolean isVirtual) throws IOException {
        serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(host, port));
        executor = isVirtual
                ? Executors.newVirtualThreadPerTaskExecutor()
                : Executors.newFixedThreadPool(threadCount);
        isRunning = true;

        System.out.println("Server started on " + host + ":" + port);

        while (isRunning) {
            try {
                SocketChannel clientChannel = serverChannel.accept();
                executor.submit(() -> handleConnection(clientChannel));
            } catch (IOException e) {
                if (!isRunning) break;
                e.printStackTrace();
            }
        }
    }

    private void handleConnection(SocketChannel clientChannel) {
        try (clientChannel) {
            HttpRequest request = HttpRequest.parse(clientChannel);
            HttpResponse response = new HttpResponse();

            RouteKey key = new RouteKey(request.method(), request.path());
            RequestHandler handler = routes.getOrDefault(key, this::defaultHandler);

            handler.handle(request, response);
            sendResponse(clientChannel, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void defaultHandler(HttpRequest request, HttpResponse response) {
        response.setStatus(404, "Not Found");
        response.setBody("<h1>404 Not Found</h1>");
        response.addHeader("Content-Type", "text/html");
    }

    public void addRoute(String method, String path, RequestHandler handler) {
        routes.put(new RouteKey(method, path), handler);
    }

    public void stop() {
        isRunning = false;
        executor.close();
        try {
            serverChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}