package ru.spbstu.server;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class HttpClient {

    private static Gson gson = new Gson();
    private static Logger logger = LoggerFactory.getLogger(HttpClient.class);

    public static void authorize(
            String login,
            String password
    ) {
        logger.info("Method authorize start executing");
        try (
            var socket = new Socket("127.0.0.1", 30001);
            var reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            var writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
        ) {
            writer.write("/authorization " + gson.toJson(new AuthorizationRequest(login, password)) + "\n");
            writer.flush();
            logger.info("Клиент написал");
            String response = reader.readLine();
            logger.info("Получили ответ от сервера: {}", response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
