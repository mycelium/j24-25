package ru.spbstu.telematics.httpserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Server {
    private final String host;
    private final int port;
    private final boolean isVirtual;
    private final ExecutorService executor;
    private ServerSocketChannel serverChannel;

    private Consumer<SocketChannel> connectionHandler = socket -> {
        // TODO: сделать обработчик
        try {
            socket.close();
        } catch (IOException ignored) {}
    };

    public Server(String host, int port, int threads, boolean isVirtual) {
        this.host = host;
        this.port = port;
        this.isVirtual = isVirtual;
        this.executor = Executors.newFixedThreadPool(threads);
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
            connectionHandler.accept(clientSocket);
        } catch (Exception e) {
            System.err.println("Ошибка в обработчике соединения: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException ignored) {}
        }
    }

    public void setConnectionHandler(Consumer<SocketChannel> handler) {
        this.connectionHandler = handler;
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
