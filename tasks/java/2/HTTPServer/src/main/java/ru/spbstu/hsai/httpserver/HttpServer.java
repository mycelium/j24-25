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

public class HttpServer implements AutoCloseable{
    private ServerSocketChannel serverSocket;
    private final InetSocketAddress address;
    private final ExecutorService executor;
    private final Map<String, HttpHandler> handlers = new ConcurrentHashMap<>();
    private volatile boolean isRunning = false;
    private final List<Route> routeList = new ArrayList<>();
    Logger log = Logger.getLogger(HttpServer.class.getName());

    public HttpServer(String host, int port, int threads, boolean isVirtual) {
        this.address = new InetSocketAddress(host, port);
        this.executor = isVirtual
                ? Executors.newFixedThreadPool(threads, Thread.ofVirtual().factory())
                : Executors.newFixedThreadPool(threads);
    }

    @Override
    public void close() throws Exception {
        isRunning = false;
        executor.close();
        try {
            if (serverSocket != null && serverSocket.isOpen()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private record Route(HttpMethods method, String pattern, HttpHandler handler) {

        public Map<String, String> matchPath(String actualPath) {
                String[] patternParts = pattern.split("/");
                String[] pathParts = actualPath.split("/");

                if (patternParts.length != pathParts.length) return null;

                Map<String, String> pathParams = new HashMap<>();
                for (int i = 0; i < patternParts.length; i++) {
                    if (patternParts[i].startsWith(":")) {
                        String paramName = patternParts[i].substring(1);
                        pathParams.put(paramName, pathParts[i]);
                    } else if (!patternParts[i].equals(pathParts[i])) {
                        return null;
                    }
                }
                return pathParams;
            }
        }


    public void addHandler(HttpMethods method, String pathPattern, HttpHandler handler) {
        for (Route r : routeList) {
            if (r.method == method && r.pattern.equals(pathPattern)) {
                throw new RuntimeException("Handler already exists for this method and path!");
            }
        }
        routeList.add(new Route(method, pathPattern, handler));
    }


    public void start() throws IOException {
        isRunning = true;
        serverSocket = ServerSocketChannel.open();
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
            log.info("New connection");

            HttpRequest request = HttpRequest.parse(reader);
            log.info("Request is parsed with " + request.getMethod() + " method and " + request.getPath() + " path");

            HttpHandler matchedHandler = null;
            Map<String, String> pathParams = null;

            log.info(request.getPath());
            for (Route route : routeList) { //ищем подходящий хендлер
                if (!route.method.name().equals(request.getMethod())) continue;
                pathParams = route.matchPath(request.getPath());
                if (pathParams != null) {
                    matchedHandler = route.handler;
                    break;
                }
            }

            HttpResponse response = new HttpResponse();

            if (matchedHandler != null) {
                request.setPathParams(pathParams);
                matchedHandler.handle(request, response);
            } else {
                response.setStatus(HttpStatus.NotFound);
                response.setBody("Not Found");
            }
            client.write(ByteBuffer.wrap(response.toBytes()));
            log.info("Response sent with status: " + response.getStatus().getCode() + ": " + response.getStatus().getInfo());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
