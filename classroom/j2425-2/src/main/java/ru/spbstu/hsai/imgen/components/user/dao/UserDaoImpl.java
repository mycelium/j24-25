package ru.spbstu.hsai.imgen.components.user.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.spbstu.hsai.imgen.components.user.entities.UserEntity;
import ru.spbstu.hsai.imgen.components.user.service.UserDao;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {

    final String DATABASE_URL = "jdbc:sqlite:users.db";

    static Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);


    private UserDaoImpl() {
        initializeDB();
    }


    @Override
    public Collection<UserEntity> getAllUsers() {
        String sql = "SELECT userID, login, password, quota FROM users;";

        List<UserEntity> users = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement statement = conn.prepareStatement(sql)
        ) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Integer userID = rs.getInt(1);
                String login = rs.getString(2);
                String password = rs.getString(3);
                Integer quota = rs.getInt(4);

                users.add(new UserEntity(userID, login, quota));
            }
            logger.info("Executed getUsers");
            return users;
        } catch (SQLException e) {
            logger.error("", e);
        }
        return users;
    }

    @Override
    public Optional<UserEntity> getUserByLoginAndPassword(String login, String passwordHash) {
        String sql = "SELECT userID, login, password, quota FROM users WHERE login = ? AND password = ?;";

        try(Connection conn = DriverManager.getConnection(DATABASE_URL);
            PreparedStatement statement = conn.prepareStatement(sql)
        ){
            statement.setString(1, login);
            statement.setString(2, passwordHash);
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                Integer userID = rs.getInt(1);
                Integer quota = rs.getInt(4);
                logger.info("Executed get user by login and password hash");
                return Optional.of(new UserEntity(userID, login, quota));
            }
        }catch (SQLException e){
            logger.error("", e);
        }
        return Optional.empty();
    }


    public void initializeDB() {
        String sql = "CREATE TABLE IF NOT EXISTS users (\n"
                + "userID INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "login TEXT NOT NULL,\n"
                + "password TEXT NOT NULL,\n"
                + "quota INTEGER NOT NULL\n"
                + ");";
        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement statement = conn.prepareStatement(sql);
        ) {
            statement.execute();
            logger.info("Database initialized");
        } catch (SQLException e) {
            logger.error("", e);
        }

    }


    private static volatile UserDao instance;
    private static final Object monitor = new Object();

    public static UserDao getInstance() {
        if (instance == null) {
            synchronized (monitor) {
                if (instance == null) {
                    instance = new UserDaoImpl();
                }
            }
        }
        return instance;
    }

}
