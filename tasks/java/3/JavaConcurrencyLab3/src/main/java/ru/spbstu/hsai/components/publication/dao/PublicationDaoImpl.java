package ru.spbstu.hsai.components.publication.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.spbstu.Main;
import ru.spbstu.hsai.components.publication.entities.PublicationEntity;
import ru.spbstu.hsai.components.publication.service.PublicationDao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PublicationDaoImpl implements PublicationDao {

    private static final String DB_URL = "jdbc:sqlite:text_publications.db?journal_mode=WAL&synchronous=NORMAL&busy_timeout=5000";
    private static final String SQL_GET_PUBLICATIONS_BY_USER_UUID = "SELECT publicationID, userUUID, text FROM text_publications WHERE userUUID = ?";
    private static final String SQL_CREATE_PUBLICATION = "INSERT INTO text_publications (userUUID, text) VALUES (?, ?)";

    static Logger logger = LoggerFactory.getLogger(PublicationDaoImpl.class);

    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:text_publications.db");
        config.setUsername(""); // SQLite не требует логина
        config.setPassword("");
        config.setMaximumPoolSize(10); // Максимум соединений
        config.setConnectionTimeout(30000); // 30 сек. таймаут
        config.setIdleTimeout(600000); // 10 мин. бездействия
        config.setMaxLifetime(1800000); // 30 мин. жизни соединения

        dataSource = new HikariDataSource(config);
    }

    @Override
    public Optional<PublicationEntity> createPublication(String userUUID, String publicationText) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_CREATE_PUBLICATION, Statement.RETURN_GENERATED_KEYS)) {

            // Устанавливаем значения для параметров запроса
            pstmt.setString(1, userUUID);
            pstmt.setString(2, publicationText);

            // Выполняем запрос
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                logger.error("Creating publication failed, no rows affected.");
            }
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    logger.info("Запись успешно сохранена!");
                    return Optional.of(
                            new PublicationEntity(
                                    generatedKeys.getInt(1),
                                    userUUID,
                                    publicationText
                            )
                    );
                }
                else {
                    logger.error("Creating publication failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            logger.error("Creating publication failed, no ID obtained.", e);
        }
        return Optional.empty();
    }

    @Override
    public Collection<PublicationEntity> getPublicationsByUserUUID(String userUUID) {
        // Список для хранения результатов
        List<PublicationEntity> publications = new ArrayList<>();
        long start = System.currentTimeMillis();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_GET_PUBLICATIONS_BY_USER_UUID)) {

            // Устанавливаем значение параметра userUUID
            pstmt.setString(1, userUUID);

            // Выполняем запрос и получаем результат
            ResultSet rs = pstmt.executeQuery();

            // Обрабатываем результат
            while (rs.next()) {
                Integer id = rs.getInt(1);
                String uuid = rs.getString(2);
                String text = rs.getString(3);

                // Создаем объект PublicationEntity и добавляем его в список
                publications.add(new PublicationEntity(id, uuid, text));
            }
            long duration = System.currentTimeMillis() - start;
            System.out.println("Query took: " + duration + "ms");
        } catch (SQLException e) {
            logger.error("Error while getting publications by UserUUID.", e);
            return Collections.emptyList();
        }
        return publications;
    }

    private void initializeDB(){
        String sql = "CREATE TABLE IF NOT EXISTS text_publications (\n"
                + "    publicationID INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "    userUUID TEXT NOT NULL,\n"
                + "    text TEXT NOT NULL\n"
                + ");";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.execute();
            System.out.println("Table initialized.");

        } catch (SQLException e) {
            System.err.println("Error while creating DB: " + e.getMessage());
        }

        String sqlIndex = "CREATE INDEX IF NOT EXISTS userUUID ON text_publications(userUUID);";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlIndex)) {

            pstmt.execute();
            System.out.println("Index on text_publications created.");

        } catch (SQLException e) {
            System.err.println("Error while creating index on text_publications: " + e.getMessage());
        }
    }

    private PublicationDaoImpl(){
        initializeDB();
    }

    private static volatile PublicationDao instance;
    private static final Object monitor = new Object();

    public static PublicationDao getInstance() {
        if (instance == null) {
            synchronized (monitor) {
                if (instance == null) {
                    instance = new PublicationDaoImpl();
                }
            }
        }
        return instance;
    }
}
