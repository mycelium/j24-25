package org.httpServerAyzek.http;

import org.httpServerAyzek.http.handler.HttpHandler;
import org.httpServerAyzek.http.util.HttpReqParser;
import java.net.*;
import java.nio.channels.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class HttpServer {
    private String host;
    private int port;
    private boolean isVirtual;
    private ExecutorService executor;
    private Map<String, Map<String, HttpHandler>> handlers = new ConcurrentHashMap<>();

    public HttpServer(String host, int port, int threadCount, boolean isVirtual) {
        this.host = host;
        this.port = port;
        this.isVirtual = isVirtual;
        if (isVirtual) {
            executor = Executors.newThreadPerTaskExecutor(Thread.ofVirtual().factory());
        } else {
            executor = Executors.newFixedThreadPool(threadCount);
        }
    }

    public void addListener(String path, String method, HttpHandler handler) {
        method = method.toUpperCase();
        handlers.computeIfAbsent(method, k -> new ConcurrentHashMap<>()).put(path, handler);
    }

    public void start() throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(host, port));
        serverChannel.configureBlocking(false);
        Selector selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("HTTP Server started on " + host + ":" + port);

        while (true) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    SocketChannel clientChannel = serverChannel.accept();
                    if (clientChannel != null) {
                        clientChannel.configureBlocking(true);
                        executor.submit(() -> {
                            try {
                                handleClient(clientChannel);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } finally {
                                try {
                                    clientChannel.close();
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    private void handleClient(SocketChannel clientChannel) throws IOException {
        InputStream in = Channels.newInputStream(clientChannel);
        OutputStream out = Channels.newOutputStream(clientChannel);
        HttpReqParser request;
        try {
            request = HttpReqParser.parse(in);
        } catch (IOException ex) {
            HttpRes response = new HttpRes();
            response.setStatusCode(400);
            response.setReasonPhrase("Oops! Bad Request :(");
            response.setBody("Oops ops! Bad Request :(");
            response.send(out);
            return;
        }
        HttpRes response = new HttpRes();
        HttpHandler handler = null;
        Map<String, HttpHandler> methodHandlers = handlers.get(request.getMethod().toUpperCase());
        if (methodHandlers != null) {
            handler = methodHandlers.get(request.getPath());
        }
        if (handler != null) {
            handler.handle(request, response);
        } else {
            response.setStatusCode(404);
            response.setReasonPhrase("Not Found!");
            response.setBody("Not Found!");
        }
        response.send(out);
    }
}
