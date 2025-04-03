package ru.spbstu.telematics.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

//класс для преобразования входящего потока байтов в объект LiHttpRequest
public class LiRequestHandler {
    private final SocketChannel channel; //канал для взаимодействия с клиентом
    private final Router router; //маршрутизатор, который определяет обработчик для запроса
    private static final int MAX_HEADER_SIZE = 8192;
    private static final int MAX_BODY_SIZE = 1048576;

    //конструктор с параметрами
    public LiRequestHandler(SocketChannel channel, Router router) {
        this.channel = channel;
        this.router = router;
    }

    //основной метод взаимодействия через канал
    public void handle() throws IOException {
        //считываем поток байт и превращаем в символьный с явным указанием кодировки при чтении
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(channel.socket().getInputStream(), StandardCharsets.UTF_8))) {
            LiHttpRequest request = parseRequest(reader); //преобразуем данных в объект LiHttpRequest
            LiHttpResponse response = router.handle(request); //передаем запрос в маршрутизатор для получения ответа
            channel.write(java.nio.ByteBuffer.wrap(response.LiHttpResponseToByteArray())); //преобразуем LiHttpResponse в массив байтов и отправляем клиенту через канал
        }
    }

    //разбор Http запроса
    public LiHttpRequest parseRequest(BufferedReader reader) throws IOException {
        String requestLine = reader.readLine(); //читаем 1ую строку запроса
        if (requestLine == null || requestLine.isEmpty()) throw new IOException("Пустой запрос");
        if (requestLine.length() > MAX_HEADER_SIZE) throw new IOException("Слишком длинная 1ая строка запроса");

        String[] parts = requestLine.split(" ",3);
        if (parts.length != 3) {
            throw new IOException("Неверный формат запроса");
        }
        String method = parts[0];
        String path = parts[1];
        String protocol = parts[2];

        Map<String, String> headers = parseHeaders(reader); //чтение заголовков
        String body = null;
        if (("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)) && headers.containsKey("Content-Length")) {
            body = parseBody(reader,headers); //чтение тела, если есть
        }
        return new LiHttpRequest(method, path, protocol, headers, body);
    }

    //метод для парсинга заголовков
    private Map<String, String> parseHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String headerLine;
        while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
            if (headers.toString().length() + headerLine.length() > MAX_HEADER_SIZE) {
                throw new IOException("Превышен максимальный размер заголовка");
            }
            String[] headerParts = headerLine.split(":", 2);
            if (headerParts.length != 2) {
                throw new IOException("Неверный формат заголовка: " + headerLine);
            }
            headers.put(headerParts[0].trim(), headerParts[1].trim());
        }
        return headers;
    }

    private String parseBody(BufferedReader reader, Map<String,String> headers) throws IOException {
        int contentLength = Integer.parseInt(headers.get("Content-Length"));
        try {
            if (contentLength > 0 && contentLength <= MAX_BODY_SIZE) {
                char[] bodyData = new char[contentLength];
                int totalRead = 0;
                while (totalRead < contentLength) {
                    int read = reader.read(bodyData, totalRead, contentLength - totalRead);
                    if (read == -1) {
                        throw new IOException("Неожиданное завершение стрима");
                    }
                    totalRead += read;
                }
                return new String(bodyData);
            } else if (contentLength > MAX_BODY_SIZE) {
                throw new IOException("Тело запроса слишком большое");
            }
        } catch (NumberFormatException | IOException e) {
            throw new IOException("Неверный формат Content-Length", e);
        }
        return null;
    }
}


