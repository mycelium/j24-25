package ru.spbstu.telematics.httpserver;

import ru.spbstu.telematics.httpserver.exceptions.SameRouteException;
import ru.spbstu.telematics.httpserver.exceptions.ServerStartupException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        Server server = new Server("localhost", 9999, 4, false);

        try {
            server.addHandler("GET", "/hello", request -> {
                HttpResponse response = new HttpResponse();
                response.setStatus(200);
                response.setBody("Hello from server!");
                return response;
            });
        } catch (SameRouteException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            try {
                server.start();
            } catch (IOException | ServerStartupException e) {
                e.printStackTrace();
            }
        }).start();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Клиент отправляет HTTP-запрос
        try (Socket socket = new Socket("localhost", 9999)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            out.print("GET /hello HTTP/1.1\r\n");
            out.print("Host: localhost\r\n");
            out.print("Connection: close\r\n");
            out.print("\r\n");
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
//        String rawRequest =
//                "POST /submit HTTP/1.1\r\n" +
//                "Host: localhost\r\n" +
//                "Content-Type: text\r\n" +
//                "Content-Length: 2\r\n" +
//                "\r\n" +
//                "Hello World";
//
//        var request = HttpRequest.parse(rawRequest);
//        System.out.println("Path: " + request.getPath());
    }
}
