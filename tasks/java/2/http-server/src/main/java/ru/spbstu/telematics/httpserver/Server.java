package ru.spbstu.telematics.httpserver;

import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final String host;
    private final int port;
    private final ExecutorService executor;
    private final boolean isVirtual;
    private ServerSocketChannel serverChannel;

    public Server(String host, int port, int threads, boolean isVirtual) {
        this.host = host;
        this.port = port;
        this.isVirtual = isVirtual;
        this.executor = Executors.newFixedThreadPool(threads);
    }

    public void start() {

    }

    public void stop() {

    }
}
