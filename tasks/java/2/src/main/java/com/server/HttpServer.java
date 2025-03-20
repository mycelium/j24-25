package com.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;

/**
 * A simple HTTP server that supports HTTP 1.1 methods GET, POST, PUT, PATCH, and DELETE.
 * Allows adding listeners for specific paths and methods, accessing request parameters,
 * and sending HTTP responses back.
 */
public class HttpServer {

    private final String host;
    private final int port;
    private final Map<Route, HttpHandler> handlers = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private volatile boolean running = true;

    /**
     * Constructs an HttpServer that listens on the specified host and port.
     *
     * @param host the host name
     * @param port the port number
     */
    public HttpServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Starts the HTTP server.
     */
    public void start() {
        try (ServerSocketChannel serverChannel = ServerSocketChannel.open()) {
            serverChannel.bind(new InetSocketAddress(host, port));
            serverChannel.configureBlocking(false);

            Selector selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (running) {
                selector.select();

                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iter = keys.iterator();

                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    iter.remove();

                    if (key.isAcceptable()) {
                        register(selector, serverChannel);
                    } else if (key.isReadable()) {
                        handleRequest(key);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }

    /**
     * Stops the HTTP server.
     */
    public void stop() {
        running = false;
    }

    /**
     * Adds a handler for a specific HTTP method and path.
     *
     * @param method  the HTTP method
     * @param path    the request path
     * @param handler the handler to process the request
     */
    public void addHandler(String method, String path, HttpHandler handler) {
        handlers.put(new Route(method.toUpperCase(), path), handler);
    }

    private void register(Selector selector, ServerSocketChannel serverChannel) throws IOException {
        SocketChannel client = serverChannel.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(8192));
    }

    private void handleRequest(SelectionKey key) {
        executor.submit(() -> {
            SocketChannel client = (SocketChannel) key.channel();
            ByteBuffer buffer = (ByteBuffer) key.attachment();
            try {
                int bytesRead = client.read(buffer);
                if (bytesRead == -1) {
                    client.close();
                    return;
                }
                buffer.flip();
                byte[] data = new byte[buffer.remaining()];
                buffer.get(data);
                String requestText = new String(data, StandardCharsets.UTF_8);
                buffer.clear();

                HttpRequest request = HttpRequest.parse(requestText);
                HttpResponse response = new HttpResponse();

                // Check for X-HTTP-Method-Override header
                String methodOverride = request.getHeaders().get("X-HTTP-Method-Override");
                if (methodOverride != null && methodOverride.equalsIgnoreCase("PATCH")) {
                    request.setMethod("PATCH");
                }

                HttpHandler handler = handlers.get(new Route(request.getMethod(), request.getPath()));
                if (handler != null) {
                    handler.handle(request, response);
                } else {
                    response.setStatusCode(404);
                    response.setReasonPhrase("Not Found");
                    response.setBody("404 Not Found");
                }

                ByteBuffer responseBuffer = ByteBuffer.wrap(response.toBytes());
                client.write(responseBuffer);
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
