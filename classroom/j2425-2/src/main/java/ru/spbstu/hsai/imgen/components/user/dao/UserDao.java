package ru.spbstu.hsai.imgen.components.user.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.spbstu.hsai.imgen.components.user.entities.UserEntity;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface UserDao extends CrudRepository<UserEntity, Integer> {

    public Collection<UserEntity> findAll();

    public Optional<UserEntity> findByLoginAndPassword(String login, String password);
}
