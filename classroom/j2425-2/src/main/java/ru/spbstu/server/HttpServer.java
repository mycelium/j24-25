package ru.spbstu.server;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.util.UUID;

public class HttpServer implements Closeable {
    ServerSocket server;
    static Gson gson = new Gson();

    static Logger logger = LoggerFactory.getLogger(HttpServer.class);

    public HttpServer(
            int port
    ) throws IOException {
        server = new ServerSocket(port, 100);
        logger.info("ServerSocket created on port: {}", port);
        handleConnections();
    }

    private void handleConnections(){
        new Thread(() -> {
            while(true){
                try(var clientSocket = server.accept();
                    var in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    var out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));){
                    logger.info("Server handle connection");
                    String request = in.readLine();
                    logger.info("Server has got request: {}", request);

                    String response = selectMethod(request);

                    out.write("Привет, вот твой токен: " + response);
                    out.flush();
                    logger.info("Был получен ответ на запрос: {}", response);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private String selectMethod(String request){
        String[] splitedRequest = request.split(" ");
        return switch (splitedRequest[0]){
            case "/authorization" -> authorize(splitedRequest[1]);
            case "/get-users" -> getUserList(splitedRequest[1]);
            case "/generate" -> generate(splitedRequest[1]);
            default -> throw new IllegalStateException("Unexpected value: " + splitedRequest[0]);
        };
    }

    private String authorize(String body){
        // /authorization {"login":"login","password":"password"}
        logger.info("Получили запрос authorize с body: {}", body);
        AuthorizationRequest auth = gson.fromJson(body, AuthorizationRequest.class);
        return UUID.randomUUID().toString();
    }

    private String generate(String body){
        // /generate {"token":"token","prompt":"string"}
        return "";
    }

    private String getUserList(String body){
        // /get-users {"token":"token"}
        return "";
    }

    @Override
    public void close() throws IOException {
        server.close();
    }
}
