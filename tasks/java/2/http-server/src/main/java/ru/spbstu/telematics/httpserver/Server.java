package ru.spbstu.telematics.httpserver;

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

public class Server {
    private final String host;
    private final int port;
    private final boolean isVirtual;
    private final ExecutorService executor;
    private ServerSocketChannel serverChannel;

    // Карта обработчиков: ключ – комбинация метода и пути, значение – обработчик запроса
    private final Map<RequestKey, HttpHandler> handlers = new ConcurrentHashMap<>();

    public Server(String host, int port, int threads, boolean isVirtual) {
        this.host = host;
        this.port = port;
        this.isVirtual = isVirtual;
        this.executor = Executors.newFixedThreadPool(threads);
    }

    public void addHandler(String method, String path, HttpHandler handler) {
        handlers.put(new RequestKey(method, path), handler);
    }

    public void start() {
        try {
            serverChannel = ServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress(host, port));
            System.out.println("Сервер запущен на " + host + ":" + port);

            while (true) {
                SocketChannel clientSocket = serverChannel.accept();
                executor.submit(() -> handleConnection(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Ошибка при запуске сервера: " + e.getMessage());
        } finally {
            stop();
        }
    }

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

    public void stop() {
        try {
            if (serverChannel != null) {
                serverChannel.close();
            }
            executor.shutdown();
            System.out.println("Сервер остановлен.");
        } catch (IOException e) {
            System.err.println("Ошибка при остановке сервера: " + e.getMessage());
        }
    }
}
