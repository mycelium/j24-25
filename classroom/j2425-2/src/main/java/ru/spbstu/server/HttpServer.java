package ru.spbstu.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.bouncycastle.jcajce.provider.asymmetric.mldsa.MLDSAKeyFactorySpi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.spbstu.db.UserRepository;

import java.io.*;
import java.net.*;
import java.util.*;

public class HttpServer implements Closeable {
    ServerSocket server;
    static Gson gson = new GsonBuilder().serializeNulls().create();
    static HashMap<String, UserApiResponse> authConnections = new HashMap<>();
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

                    out.write(response);
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
            case "/authorize" -> authorize(splitedRequest[1]);
            case "/get-users" -> getUserList(splitedRequest[1]);
            case "/generate" -> generate(request.substring(splitedRequest[0].length()));
            default -> throw new IllegalStateException("Unexpected value: " + splitedRequest[0]);
        };
    }

    public record UserApiResponse(Integer userID, String login, Integer quota){}

    private String authorize(String body){
        // /authorization {"login":"login","password":"password"}
        logger.info("Получили запрос authorize с body: {}", body);
        AuthorizationRequest auth = gson.fromJson(body, AuthorizationRequest.class);
        UserRepository repa = new UserRepository();

        UserApiResponse user = repa.getUser(auth.login(), auth.password());
        if (user == null){

            return gson.toJson(new AuthorizeApiResponse(401, null, null));
        } else {
            String token = UUID.randomUUID().toString();
            authConnections.put(token, user);
            return gson.toJson(new AuthorizeApiResponse(200, token, user.quota()));
        }
    }

    private record AuthorizeApiResponse(Integer code, String accessToken, Integer quota){}

    private record GenerateTextApiRequest(String accessToken, String text){}

    // {"data":["Рыжий кот сидит на окне и смотрит на снежинки и ждёт весну",1231900767,1024,1024,true,1],"event_data":null,"fn_index":3,"trigger_id":10,"session_hash":"4hc2g6kcoli"}

    private record GenerateImageApiRequest(
            List<Object> data,
            String event_data,
            Integer fn_index,
            Integer trigger_id,
            String session_hash
    ){}

    private record URLApiResponse(Integer code, String url){}

    private String generate(String body){
        // /generate {"token":"token","text":"string"}
        GenerateTextApiRequest bodyObject = gson.fromJson(body, GenerateTextApiRequest.class);
        if (bodyObject != null && authConnections.containsKey(bodyObject.accessToken)){
            try {
                URL url = new URL("https://llmhacker-realtime-flux-modified-flux-schnell-for-ja-p.hf.space/run/predict");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                try(OutputStream os = connection.getOutputStream()) {
                    byte[] input = gson.toJson(new GenerateImageApiRequest(
                            List.of(bodyObject.text,1231900767,1024,1024,true,1),
                            null,
                            3,
                            10,
                            "4hc2g6kcoli"
                    )).getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    HashMap<String, Object> map = gson.fromJson(response.toString(), HashMap.class);
                    String imageUrl = (String) ((Map<String, Object>) ((List<Object>) map.get("data")).get(0)).get("url");
                    return gson.toJson(new URLApiResponse(200, URLDecoder.decode(imageUrl)));
                }
            }catch (MalformedURLException e){
                throw new RuntimeException(e);
            } catch (ProtocolException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return "";
    }

    private record AccessToken(String accessToken){}

    private record GetUserListApiResponse(Integer code, List<UserApiResponse> users){}

    private String getUserList(String body){
        // /get-users {"token":"token"}
        AccessToken token = gson.fromJson(body, AccessToken.class);
        if (token != null && authConnections.containsKey(token.accessToken)){
            UserRepository repa = new UserRepository();
            repa.initializeDB();
            return gson.toJson(new GetUserListApiResponse(200, repa.getUsers()));
        }
        return gson.toJson(new GetUserListApiResponse(403, null));
    }

    @Override
    public void close() throws IOException {
        server.close();
    }
}
