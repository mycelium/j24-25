package ru.spbstu.hsai.imgen.components.user.service;

import ru.spbstu.hsai.imgen.components.user.entities.UserEntity;

import java.util.Collection;
import java.util.Optional;

public interface UserDao {

    public Collection<UserEntity> getAllUsers();

    public Optional<UserEntity> getUserByLoginAndPassword(String login, String password);
}
