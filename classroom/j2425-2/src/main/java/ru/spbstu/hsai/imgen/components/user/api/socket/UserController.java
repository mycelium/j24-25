package ru.spbstu.hsai.imgen.components.user.api.socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.spbstu.hsai.imgen.components.user.api.socket.dto.AccessTokenDTO;
import ru.spbstu.hsai.imgen.components.user.api.socket.dto.AuthorizationDTO;
import ru.spbstu.hsai.imgen.components.user.api.socket.dto.UserDTO;
import ru.spbstu.hsai.imgen.components.user.api.socket.dto.UserListDTO;
import ru.spbstu.hsai.imgen.components.user.entities.UserEntity;
import ru.spbstu.hsai.imgen.components.user.service.UserService;
import ru.spbstu.server.AuthorizationRequest;

import java.util.Optional;

public class UserController {
    static Gson gson = new GsonBuilder().serializeNulls().create();
    static Logger logger = LoggerFactory.getLogger(UserController.class);
    private UserService service = UserService.getInstance();

    public String authorize(String body){
        logger.info("Получили запрос authorize с body: {}", body);
        AuthorizationRequest auth = gson.fromJson(body, AuthorizationRequest.class);

        Optional<String> optToken = service.authentificate(auth.login(), auth.password());

        if (optToken.isEmpty()){
            return gson.toJson(new AuthorizationDTO(401, null, null));
        } else {
            String token = optToken.get();
            Optional<UserEntity> user = service.getUserBySessionID(token);
            return gson.toJson(new AuthorizationDTO(200, token, user.get().getQuota()));
        }
    }

    public String getUserList(String body){
        AccessTokenDTO token = gson.fromJson(body, AccessTokenDTO.class);
        if (token != null && service.getUserBySessionID(token.accessToken()).isPresent()){
            return gson.toJson(new UserListDTO(200, service.getAllUsers().stream()
                    .map(this::mapFromUserEntity).toList()));
        }
        return gson.toJson(new UserListDTO(401, null));
    }

    private UserController(){

    }

    private static volatile UserController instance;
    private static final Object monitor = new Object();

    public static UserController getInstance() {
        if (instance == null) {
            synchronized (monitor) {
                if (instance == null) {
                    instance = new UserController();
                }
            }
        }
        return instance;
    }

    private UserDTO mapFromUserEntity(UserEntity object){
        return new UserDTO(
                object.getUserId(),
                object.getLogin(),
                object.getQuota()
        );
    }
}
