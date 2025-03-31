package spbstu.lab;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class HttpServer {

    private static class RouteKey {
        private final String method;
        private final String path;

        public RouteKey(String method, String path) {
            this.method = method.toUpperCase(Locale.ROOT);
            this.path = path;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RouteKey)) return false;
            RouteKey other = (RouteKey) o;
            return method.equals(other.method) && path.equals(other.path);
        }

        @Override
        public int hashCode() {
            return Objects.hash(method, path);
        }
    }

    /**
     * Represents an HTTP request as parsed by the server.
     */
    public static class HttpRequest {
        private final String method;
        private final String path;
        private final String httpVersion;
        private final Map<String, String> headers;
        private final byte[] body;

        public HttpRequest(String method, String path, String httpVersion,
                           Map<String, String> headers, byte[] body) {
            this.method = method;
            this.path = path;
            this.httpVersion = httpVersion;
            this.headers = headers;
            this.body = body;
        }

        public String method() {
            return method;
        }

        public String path() {
            return path;
        }

        public String httpVersion() {
            return httpVersion;
        }

        public Map<String, String> headers() {
            return headers;
        }

        public byte[] body() {
            return body;
        }


        public String bodyAsString() {
            return new String(body);
        }
    }

    /**
     * Represents an HTTP response to be sent back to the client.
     */
    public static class HttpResponse {
        private int statusCode;
        private final Map<String, String> headers = new LinkedHashMap<>();
        private byte[] body = new byte[0];

        public HttpResponse() {
            this.statusCode = 200;
            headers.put("Content-Type", "text/plain; charset=UTF-8");
        }

        public HttpResponse setStatusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public HttpResponse setHeader(String key, String value) {
            headers.put(key, value);
            return this;
        }

        public HttpResponse setBody(byte[] body) {
            this.body = body;
            return this;
        }

        public HttpResponse setBody(String bodyString) {
            this.body = bodyString.getBytes();
            return this;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public byte[] getBody() {
            return body;
        }
    }

    /**
     * Functional interface for handling requests.
     */
    @FunctionalInterface
    public interface Handler {
        HttpResponse handle(HttpRequest request);
    }

    private final String host;
    private final int port;
    private final int numThreads;
    private final boolean isVirtual;

    private final Map<RouteKey, Handler> routes = new ConcurrentHashMap<>();
    private ServerSocketChannel serverSocketChannel;
    private ExecutorService executorService;
    private volatile boolean running = false;


    public HttpServer(String host, int port, int numThreads, boolean isVirtual) {
        this.host = host;
        this.port = port;
        this.numThreads = numThreads;
        this.isVirtual = isVirtual;
    }

    /**
     * Register a handler for a given METHOD + path combination.
     */
    public void addHandler(String method, String path, Handler handler) {
        routes.put(new RouteKey(method, path), handler);
    }

    /**
     * Start the server in a new thread.
     */
    public synchronized void start() throws IOException {
        if (running) return;

        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(host, port));
        serverSocketChannel.configureBlocking(true);

        if (isVirtual) {
            executorService = Executors.newFixedThreadPool(numThreads, Thread.ofVirtual().factory());
        } else {
            executorService = Executors.newFixedThreadPool(numThreads);
        }

        running = true;
        Thread acceptThread = new Thread(this::acceptLoop, "HttpServer-Acceptor");
        acceptThread.start();
        System.out.println("HTTP Server started on " + host + ":" + port);
    }

    /**
     * Stop the server.
     */
    public synchronized void stop() {
        running = false;
        if (serverSocketChannel != null) {
            try {
                serverSocketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (executorService != null) {
            executorService.shutdown();
        }
        System.out.println("HTTP Server stopped.");
    }

    /**
     * Main accept loop: accept new connections, hand off to a worker thread.
     */
    private void acceptLoop() {
        while (running) {
            try {
                SocketChannel clientChannel = serverSocketChannel.accept();
                if (clientChannel != null) {
                    executorService.submit(() -> handleClient(clientChannel));
                }
            } catch (IOException e) {
                if (running) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Handle a client connection: parse request, dispatch to route, write response.
     */
    private void handleClient(SocketChannel clientChannel) {
        try (clientChannel) {
            HttpRequest request = parseRequest(clientChannel);
            if (request == null) {
                return;
            }

            RouteKey routeKey = new RouteKey(request.method(), request.path());
            Handler handler = routes.getOrDefault(routeKey,
                    req -> new HttpResponse().setStatusCode(404).setBody("Not Found"));

            HttpResponse response = handler.handle(request);

            writeResponse(clientChannel, response);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parse the HTTP request from the client channel.
     */
    private HttpRequest parseRequest(SocketChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(8192);
        StringBuilder rawData = new StringBuilder();

        channel.read(buffer);
        buffer.flip();
        while (buffer.hasRemaining()) {
            rawData.append((char) buffer.get());
        }

        String[] parts = rawData.toString().split("\r\n\r\n", 2);
        String headerPart = parts[0];
        String bodyPart = parts.length > 1 ? parts[1] : "";

        String[] lines = headerPart.split("\r\n");
        if (lines.length < 1) {
            return null;
        }
        String[] requestLine = lines[0].split(" ");
        if (requestLine.length < 3) {
            return null;
        }
        String method = requestLine[0];
        String path = requestLine[1];
        String httpVersion = requestLine[2];

        Map<String, String> headers = new HashMap<>();
        for (int i = 1; i < lines.length; i++) {
            int idx = lines[i].indexOf(":");
            if (idx != -1) {
                String key = lines[i].substring(0, idx).trim();
                String value = lines[i].substring(idx + 1).trim();
                headers.put(key, value);
            }
        }

        byte[] bodyBytes = bodyPart.getBytes();

        return new HttpRequest(method, path, httpVersion, headers, bodyBytes);
    }

    /**
     * Write the HttpResponse back to the client.
     */
    private void writeResponse(SocketChannel channel, HttpResponse response) throws IOException {
        String statusLine = "HTTP/1.1 " + response.getStatusCode() + " " + getStatusText(response.getStatusCode());
        StringBuilder sb = new StringBuilder();
        sb.append(statusLine).append("\r\n");

        byte[] body = response.getBody();
        if (!response.getHeaders().containsKey("Content-Length")) {
            response.setHeader("Content-Length", String.valueOf(body.length));
        }
        for (Map.Entry<String, String> entry : response.getHeaders().entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }
        sb.append("\r\n");

        byte[] headerBytes = sb.toString().getBytes();
        ByteBuffer outBuffer = ByteBuffer.allocate(headerBytes.length + body.length);
        outBuffer.put(headerBytes).put(body);
        outBuffer.flip();

        channel.write(outBuffer);
    }

    private String getStatusText(int statusCode) {
        switch (statusCode) {
            case 200:
                return "OK";
            case 201:
                return "Created";
            case 204:
                return "No Content";
            case 400:
                return "Bad Request";
            case 401:
                return "Unauthorized";
            case 403:
                return "Forbidden";
            case 404:
                return "Not Found";
            case 500:
                return "Internal Server Error";
            default:
                return "Unknown";
        }
    }
}