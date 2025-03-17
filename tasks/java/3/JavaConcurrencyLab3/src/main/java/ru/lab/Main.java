package ru.lab;

import com.google.gson.Gson;
import ru.lab.server.HTTPServer;
import ru.lab.server.ObjectMapper;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class Main {


    public static void main(String[] args) {
        startServer();
        System.out.println("Hello world!");
    }

    private static void startServer(){
        try{
            PublicationRepository repository = new PublicationRepository();
            repository.initializeDatabase();
            HTTPServer server = new HTTPServer(
                    "localhost",
                    30001,
                    false,
                    10
            );


//            ObjectMapper mapper = new ObjectMapper() {
//                Gson gson = new Gson();
//                @Override
//                public <T> T deserialize(String s, Class<T> aClass) {
//                    return gson.fromJson(s, aClass);
//                }
//                @Override
//                public <T> String serialize(T t) {
//                    return gson.toJson(t);
//                }
//            };

//            server.setBodyMapper(mapper);
            setFirstRequest(server, repository);
            setSecondRequest(server, repository);
        } catch (IOException e){
            e.printStackTrace();
        }
        while (true){}
    }

    public record CreateTextPublicationApiRequest(String userUUID, String text){}
    public record UUIDApiModel(String uuid){}

    public record IDApiModel(Integer id){}
    public record PublicationApiModel(String userUUID, Integer publicationID, String text){}
    public record PublicationListApiResponse(List<PublicationApiModel> publications){}

    private static void setFirstRequest(HTTPServer server, PublicationRepository repository){
        server.registerPostMethod(
                "/first",
                CreateTextPublicationApiRequest.class,
                IDApiModel.class,
                (headers, pathVariables, body) -> {
                    try {
                        return repository.save(body);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    private static void setSecondRequest(HTTPServer server, PublicationRepository repository){
        server.registerPostMethod(
                "/second",
                UUIDApiModel.class,
                PublicationListApiResponse.class,
                (headers, pathVariables, body) -> {
                    try {
                        return new PublicationListApiResponse(repository.getPublicationsByUserUUID(body.uuid));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }
}