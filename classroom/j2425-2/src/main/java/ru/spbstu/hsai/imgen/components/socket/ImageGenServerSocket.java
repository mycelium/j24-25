package ru.spbstu.hsai.imgen.components.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.spbstu.hsai.imgen.components.image.api.socket.ImageGenController;
import ru.spbstu.hsai.imgen.components.user.api.socket.UserController;

import java.io.*;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageGenServerSocket {
    ServerSocket server;
    static Logger logger = LoggerFactory.getLogger(ImageGenServerSocket.class);
    private static ExecutorService executors = Executors.newFixedThreadPool(8);
    private UserController userController = UserController.getInstance();
    private ImageGenController imageGenController = ImageGenController.getInstance();

    public ImageGenServerSocket(
            int port
    ) throws IOException {
        server = new ServerSocket(port, 100);
        logger.info("ServerSocket created on port: {}", port);
        handleConnections();
    }

    private void handleConnections(){
        new Thread(() -> {
            while(true){
                try{
                    var clientSocket = server.accept();
                    final var in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    final var out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                    executors.submit(() -> {
                        try{
                            logger.info("Server handle connection");
                            String request = in.readLine();
                            logger.info("Server has got request: {}", request);
                            executors.submit(() -> selectMethod(request));
                            String response = selectMethod(request);

                            out.write(response);
                            out.flush();
                            logger.info("Был получен ответ на запрос: {}", response);
                        } catch (IOException e){
                            logger.error("Error while processing request", e);
                        } finally {
                            try {
                                clientSocket.close();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String selectMethod(String request){
        String[] splitedRequest = request.split(" ");
        return switch (splitedRequest[0]){
            case "/authorize" -> userController.authorize(splitedRequest[1]);
            case "/get-users" -> userController.getUserList(splitedRequest[1]);
            case "/generate" -> imageGenController.generate(request.substring(splitedRequest[0].length()));
            default -> throw new IllegalStateException("Unexpected value: " + splitedRequest[0]);
        };
    }

}
