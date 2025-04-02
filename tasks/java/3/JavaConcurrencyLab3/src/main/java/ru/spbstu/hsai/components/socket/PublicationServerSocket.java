package ru.spbstu.hsai.components.socket;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lab.server.HTTPServer;
import ru.lab.server.ObjectMapper;
import ru.spbstu.hsai.components.publication.api.PublicationController;
import ru.spbstu.hsai.components.publication.api.dto.CreatePublicationDTO;
import ru.spbstu.hsai.components.publication.api.dto.PublicationDTO;
import ru.spbstu.hsai.components.publication.api.dto.UUIDDTO;

import java.io.IOException;
import java.util.Map;

public class PublicationServerSocket {
    private HTTPServer server;
    private Gson gson = new Gson();
    private static Logger logger = LoggerFactory.getLogger(PublicationServerSocket.class);
    private PublicationController publicationController = PublicationController.getInstance();

    public PublicationServerSocket(){
        try{
            server = new HTTPServer(
                    "localhost",
                    30001,
                    false,
                    10
            );
            setMapper();
            setFirstRequest(server, publicationController);
            setSecondRequest(server, publicationController);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void start(){
        server.start();
    }

    private void setMapper(){
        server.setBodyMapper(new ObjectMapper(){
            @Override
            public <T> T deserialize(String json, Class<T> clazz) {
                return gson.fromJson(json, clazz);
            }

            @Override
            public <T> String serialize(T object) {
                return gson.toJson(object);
            }
        });
    }

    private static void setFirstRequest(HTTPServer server, PublicationController controller){
        server.registerPostMethod(
                "/create",
                CreatePublicationDTO.class,
                PublicationDTO.class,
                (headers, pathVariables, body) -> new HTTPServer.HttpResponse(
                            201,
                            "OK",
                            Map.of("Content-Type", "application/json"),
                            controller.createPublication(body)
                    )
        );
    }

    private static void setSecondRequest(HTTPServer server, PublicationController controller){
        server.registerPostMethod(
                "/get",
                UUIDDTO.class,
                PublicationDTO.class,
                (headers, pathVariables, body) -> {
                    return new HTTPServer.HttpResponse(
                        200,
                        "OK",
                        Map.of("Content-Type", "application/json"),
                        controller.getPublicationsByUserUUID(body)
                );}
        );
    }
}
