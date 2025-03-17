package ru.lab;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PublicationRepository {

    // Путь к базе данных SQLite
    private static final String DB_URL = "jdbc:sqlite:text_publications.db";


    private record PublicationDAO(Integer id, String userUUID, String text){}

    // Метод для сохранения записи в базу данных
    public Main.IDApiModel save(Main.CreateTextPublicationApiRequest request) throws SQLException {
        // SQL-запрос для вставки данных
        String sql = "INSERT INTO text_publications (userUUID, text) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Устанавливаем значения для параметров запроса
            pstmt.setString(1, request.userUUID());
            pstmt.setString(2, request.text());

            // Выполняем запрос
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating publication failed, no rows affected.");
            }
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    System.out.println("Запись успешно сохранена!");
                    return new Main.IDApiModel(generatedKeys.getInt(1));
                }
                else {
                    throw new SQLException("Creating publication failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Creating publication failed, no ID obtained.", e);
        }
    }

    // Метод для получения всех публикаций по userUUID
    public List<Main.PublicationApiModel> getPublicationsByUserUUID(String userUUID) throws SQLException {
        // SQL-запрос для выборки данных
        String sql = "SELECT publicationID, userUUID, text FROM text_publications WHERE userUUID = ?";

        // Список для хранения результатов
        List<Main.PublicationApiModel> publications = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Устанавливаем значение параметра userUUID
            pstmt.setString(1, userUUID);

            // Выполняем запрос и получаем результат
            ResultSet rs = pstmt.executeQuery();

            // Обрабатываем результат
            while (rs.next()) {
                Integer id = rs.getInt(1);
                String uuid = rs.getString(2);
                String text = rs.getString(3);

                // Создаем объект CreateTextPublicationApiRequest и добавляем его в список
                publications.add(new Main.PublicationApiModel(uuid, id, text));
            }

        } catch (SQLException e) {
            throw new SQLException("Error while getting publications by UserUUID.");
        }

        return publications;
    }

    // Метод для инициализации базы данных (создание таблицы, если её нет)
    public void initializeDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS text_publications (\n"
                + "    publicationID INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "    userUUID TEXT NOT NULL,\n"
                + "    text TEXT NOT NULL\n"
                + ");";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.execute();
            System.out.println("Таблица инициализирована (или уже существует).");

        } catch (SQLException e) {
            System.err.println("Ошибка при инициализации базы данных: " + e.getMessage());
        }
    }

}
