package ru.spbstu.db;

import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.spbstu.server.HttpServer;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    final String DATABASE_URL = "jdbc:sqlite:users.db";

    static Logger logger = LoggerFactory.getLogger(UserRepository.class);

    public record UserDAO(Integer userID, String login, String password, Integer quota){}

    public List<HttpServer.UserApiResponse> getUsers() {
        String sql = "SELECT userID, login, password, quota FROM users;";

        List<HttpServer.UserApiResponse> users = new ArrayList<>();

        try(Connection conn = DriverManager.getConnection(DATABASE_URL);
            PreparedStatement statement = conn.prepareStatement(sql)
        ){
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                Integer userID = rs.getInt(1);
                String login = rs.getString(2);
                String password = rs.getString(3);
                Integer quota = rs.getInt(4);

                users.add(new HttpServer.UserApiResponse(userID, login, quota));
            }
            logger.info("Executed getUsers");
            return users;
        }catch (SQLException e){
            logger.error("", e);
        }
        return users;
    }

    public HttpServer.UserApiResponse getUser(String login, String passwordHash){
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
                return new HttpServer.UserApiResponse(userID, login, quota);
            }
        }catch (SQLException e){
            logger.error("", e);
        }
        return null;
    }

    public void initializeDB() {
        String sql = "CREATE TABLE IF NOT EXISTS users (\n"
                + "userID INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "login TEXT NOT NULL,\n"
                + "password TEXT NOT NULL,\n"
                + "quota INTEGER NOT NULL\n"
                + ");";

        String passwordHash1 = "";
        String passwordHash2 = "";
        String passwordHash3 = "";

        try{
            MessageDigest alg = MessageDigest.getInstance("SHA-256");
            passwordHash1 = Hex.toHexString(alg.digest("Qwerty1234sol".getBytes(StandardCharsets.UTF_8)));
            passwordHash2 = Hex.toHexString(alg.digest("Qwerty4321sol".getBytes(StandardCharsets.UTF_8)));
            passwordHash3 = Hex.toHexString(alg.digest("Qwerty5555sol".getBytes(StandardCharsets.UTF_8)));

        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }

        List<UserDAO> users = new ArrayList<>();
        users.add(new UserDAO(0, "Arseniy1", passwordHash1, 10));
        users.add(new UserDAO(0, "Arseniy2", passwordHash2, 5));
        users.add(new UserDAO(0, "Arseniy3", passwordHash3, 3));

        String usersInitialize = """
                    INSERT INTO users (login, password, quota) VALUES (?, ?, ?);
                """;

        try(Connection conn = DriverManager.getConnection(DATABASE_URL);
            PreparedStatement statement = conn.prepareStatement(sql);
        ){
            statement.execute();

            logger.info("Database initialized");
        }catch (SQLException e){
            logger.error("", e);
        }

        try(Connection conn = DriverManager.getConnection(DATABASE_URL);
            PreparedStatement statementInsert = conn.prepareStatement(usersInitialize)
        ){

            for (UserDAO user: users){
                statementInsert.setString(1, user.login);
                statementInsert.setString(2, user.password);
                statementInsert.setInt(3, user.quota);
                statementInsert.execute();
            }

            logger.info("Data inserted");
        }catch (SQLException e){
            logger.error("", e);
        }
    }

}
