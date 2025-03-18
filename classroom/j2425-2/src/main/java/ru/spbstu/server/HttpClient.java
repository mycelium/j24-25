package ru.spbstu.server;

import com.google.gson.Gson;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpClient {

    private static Gson gson = new Gson();
    private static Logger logger = LoggerFactory.getLogger(HttpClient.class);

    private record AuthorizeApiResponse(Integer code, String accessToken, Integer quota){}

    private static String accessToken = null;

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
            MessageDigest alg = MessageDigest.getInstance("SHA-256");
            String passwordHash = Hex.toHexString(alg.digest(password.getBytes(StandardCharsets.UTF_8)));

            writer.write("/authorize " + gson.toJson(new AuthorizationRequest(login, passwordHash)) + "\n");
            writer.flush();
            String response = reader.readLine();
            logger.info("Клиент написал" + response);
            AuthorizeApiResponse token = gson.fromJson(response, AuthorizeApiResponse.class);
            if (token.code == 401){
                logger.error("Не смогли подключиться :( : {}", response);
            } else {
                accessToken = token.accessToken();
                logger.info("Всё хорошо, вот наш токен: {}", response );
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private record AccessToken(String accessToken){}

    public static void getUsers() {
        logger.info("Method authorize start executing");
        try (
                var socket = new Socket("127.0.0.1", 30001);
                var reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                var writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
        ) {
            writer.write("/get-users " + gson.toJson(new AccessToken(accessToken)) + "\n");
            writer.flush();
            logger.info("Клиент написал");
            String response = reader.readLine();
            logger.info("Получили ответ от сервера: {}", response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private record GenerateTextApiRequest(String accessToken, String text){}

    private record URLApiResponse(Integer code, String url){}

    public static void generateImage(
            String text
    ) {
        logger.info("Method generate text start executing");
        try (
                var socket = new Socket("127.0.0.1", 30001);
                var reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                var writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
        ) {
            writer.write("/generate " + gson.toJson(new GenerateTextApiRequest(accessToken, text)) + "\n");
            writer.flush();
            logger.info("Клиент написал");
            String response = reader.readLine();
            logger.info("Получили ответ от сервера: {}", response.replace("\\u003d", "="));
            URLApiResponse urlResponse = gson.fromJson(response, URLApiResponse.class);

            URL url = new URL(urlResponse.url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            Files.copy(connection.getInputStream(), Path.of("images/russian_l"), StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
