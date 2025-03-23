package ru.spbstu.httpserver;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    private ServerSocketChannel serverChannel;
    private ExecutorService executor;
    private final ConcurrentHashMap<RouteKey, RequestHandler> routes = new ConcurrentHashMap<>();
    private boolean isRunning;
    private final Map<String, Map<String, Object>> dataStore = new ConcurrentHashMap<>();

    public Map<String, Map<String, Object>> getDataStore() {
        return  dataStore;
    }

    // Запись маршрута как record
    private record RouteKey(String method, String pathPattern) {
        public boolean matches(String method, String path) {
            return this.method.equals(method) &&
                    (pathPattern.startsWith("/data/") ? path.startsWith("/data/") : pathPattern.equals(path));
        }

        public String extractId(String path) {
            if (pathPattern.startsWith("/data/") && path.startsWith("/data/")) {
                return path.split("/")[2]; // Извлекаем ID из пути
            }
            return null;
        }
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
        try (clientChannel;
             BufferedReader reader = new BufferedReader(new InputStreamReader(Channels.newInputStream(clientChannel)));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Channels.newOutputStream(clientChannel)))) {

            System.out.println("New connection received");

            HttpRequest request = HttpRequest.parse(reader);
            System.out.println("Request parsed: " + request.method() + " " + request.path());

            HttpResponse response = new HttpResponse();

            // Поиск подходящего обработчика
            RequestHandler handler = null;
            String id = null;
            for (Map.Entry<RouteKey, RequestHandler> entry : routes.entrySet()) {
                RouteKey key = entry.getKey();
                if (key.matches(request.method(), request.path())) {
                    handler = entry.getValue();
                    id = key.extractId(request.path());
                    break;
                }
            }

            if (handler == null) {
                defaultHandler(request, response);
            } else {
                if (id != null) {
                    // Передаём ID в запрос
                    request.headers().put("X-Resource-ID", id);
                }
                handler.handle(request, response);
            }

            sendResponse(writer, response);
            System.out.println("Response sent: " + response.getStatus());
        } catch (IOException e) {
            System.err.println("Error handling connection: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendResponse(BufferedWriter writer, HttpResponse response) throws IOException {
        // Записываем статусную строку
        writer.write("HTTP/1.1 " + response.getStatus() + "\r\n");

        // Записываем заголовки
        for (Map.Entry<String, String> entry : response.getHeaders().entrySet()) {
            writer.write(entry.getKey() + ": " + entry.getValue() + "\r\n");
        }

        // Пустая строка между заголовками и телом
        writer.write("\r\n");

        // Записываем тело ответа
        writer.write(response.getBody());

        // Очищаем буфер и отправляем данные
        writer.flush();
    }

    private void defaultHandler(HttpRequest request, HttpResponse response) {
        response.setStatus(404, "Not Found");
        response.setBody("<h1>404 Not Found</h1>");
        response.addHeader("Content-Type", "text/html");
    }

    public void addRoute(String method, String pathPattern, RequestHandler handler) {
        routes.put(new RouteKey(method, pathPattern), handler);
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