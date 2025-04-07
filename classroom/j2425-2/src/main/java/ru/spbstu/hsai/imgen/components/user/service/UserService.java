package ru.spbstu.hsai.imgen.components.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.spbstu.hsai.imgen.components.user.dao.UserDao;
import ru.spbstu.hsai.imgen.components.user.entities.UserEntity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {
    private Map<String, UserEntity> authConnections = new ConcurrentHashMap<>();

    @Autowired
    private UserDao userDao;

    static Logger logger = LoggerFactory.getLogger(UserService.class);

    public Collection<UserEntity> getAllUsers(){
        return userDao.findAll();
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
        return userDao.findByLoginAndPassword(login, passwordHash);
    }


}
