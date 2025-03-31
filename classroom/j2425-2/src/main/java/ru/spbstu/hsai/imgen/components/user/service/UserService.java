package ru.spbstu.hsai.imgen.components.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.spbstu.hsai.imgen.components.user.dao.UserDaoImpl;
import ru.spbstu.hsai.imgen.components.user.entities.UserEntity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserService {
    private Map<String, UserEntity> authConnections = new ConcurrentHashMap<>();
    private UserDao userDao = UserDaoImpl.getInstance();
    static Logger logger = LoggerFactory.getLogger(UserService.class);

    public Collection<UserEntity> getAllUsers(){
        return userDao.getAllUsers();
    }

    public Optional<String> authentificate(String login, String password){
        Optional<UserEntity> user = getUserByLoginAndPassword(login, password);
        final var uuid = UUID.randomUUID().toString();
        if (user.isPresent()){
            authConnections.put(uuid, user.get());
            return Optional.of(uuid);
        }
        return Optional.empty();
    }

    public Optional<UserEntity> getUserBySessionID(String sessionID){
        return Optional.ofNullable(authConnections.get(sessionID));
    }

    public Optional<UserEntity> getUserByLoginAndPassword(String login, String passwordHash){
        return userDao.getUserByLoginAndPassword(login, passwordHash);
    }

    private static volatile UserService instance;
    private static final Object monitor = new Object();

    public static UserService getInstance() {
        if (instance == null) {
            synchronized (monitor) {
                if (instance == null) {
                    instance = new UserService();
                }
            }
        }
        return instance;
    }

}
