package ru.spbstu.hsai.httpserver;

import ru.spbstu.hsai.httpserver.common.HttpMethods;
import ru.spbstu.hsai.httpserver.common.HttpStatus;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class HttpServer {
    private final InetSocketAddress address;
    private final ExecutorService executor;
    private final Map<String, HttpHandler> handlers = new ConcurrentHashMap<>();
    private volatile boolean isRunning = false;
    Logger log = Logger.getLogger(HttpServer.class.getName());

    public HttpServer(String host, int port, int threads, boolean isVirtual) {
        this.address = new InetSocketAddress(host, port);
        this.executor = isVirtual
                ? Executors.newFixedThreadPool(threads, Thread.ofVirtual().factory())
                : Executors.newFixedThreadPool(threads);
    }

    public void addHandler(HttpMethods method, String path, HttpHandler handler) {
        if (handlers.get(method.name() + ":" + path) == null){
            handlers.put(method.name() + ":" + path, handler);
        }
        else throw new RuntimeException("This method is already exists!");
    }

    public void start() throws IOException {
        isRunning = true;
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(address);

        log.info("Server start on host: " + address.getHostName() + " and port: " + address.getPort());

        while (isRunning) {
            SocketChannel client = serverSocket.accept();
            executor.submit(() -> handleClient(client));
        }
    }

    private void handleClient(SocketChannel client) {
        try (client) {
            client.configureBlocking(true);
            BufferedReader reader = new BufferedReader(Channels.newReader(client, StandardCharsets.UTF_8));
            HttpRequest request = HttpRequest.parse(reader);

            String key = request.getMethod() + ":" + request.getPath();
            HttpHandler handler = handlers.get(key);
            HttpResponse response = new HttpResponse();

            if (handler != null) {
                handler.handle(request, response);
            } else {
                response.setStatus(HttpStatus.NotFound);
                response.setBody("Not Found");
            }

            client.write(ByteBuffer.wrap(response.toBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
