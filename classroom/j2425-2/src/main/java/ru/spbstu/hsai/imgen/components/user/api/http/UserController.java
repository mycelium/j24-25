package ru.spbstu.hsai.imgen.components.user.api.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.hsai.imgen.components.user.api.http.dto.AccessTokenDTO;
import ru.spbstu.hsai.imgen.components.user.api.http.dto.AuthorizationDTO;
import ru.spbstu.hsai.imgen.components.user.api.http.dto.UserDTO;
import ru.spbstu.hsai.imgen.components.user.api.http.dto.UserListDTO;
import ru.spbstu.hsai.imgen.components.user.entities.UserEntity;
import ru.spbstu.hsai.imgen.components.user.service.UserService;
import ru.spbstu.server.AuthorizationRequest;

import java.util.Optional;

@RestController
public class UserController {
    static Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService service;

    @PostMapping(path = "/authorize")
    public @ResponseBody AuthorizationDTO authorize(
            @RequestBody AuthorizationRequest request
    ){
        logger.info("Получили запрос authorize с request: {}", request);

        Optional<String> optToken = service.authentificate(request.login(), request.password());

        if (optToken.isEmpty()){
            return new AuthorizationDTO(401, null, null);
        } else {
            String token = optToken.get();
            Optional<UserEntity> user = service.getUserBySessionID(token);
            return new AuthorizationDTO(200, token, user.get().getQuota());
        }
    }

    @GetMapping(path = "/users")
    public @ResponseBody UserListDTO getUserList(
            @RequestBody AccessTokenDTO request
    ){
        if (request != null && service.getUserBySessionID(request.accessToken()).isPresent()){
            return new UserListDTO(200, service.getAllUsers().stream()
                    .map(this::mapFromUserEntity).toList());
        }
        return new UserListDTO(401, null);
    }

    private UserDTO mapFromUserEntity(UserEntity object){
        return new UserDTO(
                object.getUserId(),
                object.getLogin(),
                object.getQuota()
        );
    }
}
