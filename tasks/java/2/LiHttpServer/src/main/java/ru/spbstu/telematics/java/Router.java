package ru.spbstu.telematics.java;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

//маршрутизатор Http запроса
public class Router {
    //Хранилище маршрутов в виде двухуровневой мапы:
    //1 уровень - HTTP-метод (GET, POST и т.д.)
    //2 уровень - Путь URL (/home, /api и т.д.)
    //Значение - обработчик запроса (лямбда или метод)
    private final Map<String, Map<String, Function<LiHttpRequest,LiHttpResponse>>> routes = new HashMap<>();

    //метод для добавления нового маршрута
    public void addRoute(String method, String path, Function<LiHttpRequest,LiHttpResponse> handler){
        //проверяем, если для метода нет табл маршрутов, то создаем новую, добавляем для него путь и обработчик
        routes.computeIfAbsent(method, k -> new HashMap<>()).put(path,handler);
    }

    //метод для обработки входящего запроса
    public LiHttpResponse handle(LiHttpRequest request) {
        try {
            String method = request.getMethod();
            Map<String, Function<LiHttpRequest, LiHttpResponse>> methodRoutes =
                    routes.getOrDefault(method, Map.of());
            String path = request.getPath();

            //проверка точного совпадения
            if (methodRoutes.containsKey(path)) {
                return methodRoutes.get(path).apply(request);
            }

            //проверка wildcard маршрутов
            for (Map.Entry<String, Function<LiHttpRequest, LiHttpResponse>> entry :
                    methodRoutes.entrySet()) {

                if (entry.getKey().endsWith("/*")) {
                    String basePath = entry.getKey().substring(0, entry.getKey().length() - 1);
                    if (path.startsWith(basePath)) {
                        return entry.getValue().apply(request);
                    }
                }
            }

            return new LiHttpResponse(404, "Not found");
        } catch (Exception e) {
            return new LiHttpResponse(500, "Internal Server Error: " + e.getMessage());
        }
    }

    //стандартные маршруты
    public void addDefaultRoutes() {
        //проверка работоспособности сервера
        addRoute("GET", "/ping", req ->
                new LiHttpResponse(200, "Сервер запущен"));

        //главная страница
        addRoute("GET", "/", req ->
                new LiHttpResponse(200,
                        "<html>" +
                                "<head><title>Главная</title></head>" +
                                "<body><h1>Добро пожаловать!</h1></body>" +
                                "</html>"));

        //доступные методы
        addRoute("GET", "/help", req ->
                new LiHttpResponse(200,
                        "Доступные методы API:\n\n" +

                                "=== Основные маршруты ===\n" +
                                "GET    /          - Главная страница\n" +
                                "GET    /ping      - Проверка работы сервера (возвращает 'pong')\n" +
                                "GET    /help      - Эта справка\n\n" +

                                "=== Работа с пользователями ===\n" +
                                "GET    /users     - Получить список пользователей (JSON)\n" +
                                "POST   /users     - Создать нового пользователя (требуется тело запроса)\n" +
                                "GET    /users/id- Получить пользователя по ID\n" +
                                "PUT    /users/id- Полное обновление пользователя\n" +
                                "PATCH  /users/id- Частичное обновление пользователя\n" +
                                "DELETE /users/id- Удалить пользователя\n\n"));


        //GET /users (получить список пользователей)
        addRoute("GET", "/users", req ->
                LiHttpResponse.json("[{\"id\": 1, \"name\": \"Frank\"}]")
        );

        //POST /users (создать пользователя)
        addRoute("POST", "/users", req -> {
            String body = req.getBody();
            if (body == null || body.isEmpty()) {
                return new LiHttpResponse(400, "Тело запроса пустое");
            }
            return new LiHttpResponse(201, "Пользователь создан");
        });

        //PUT /users/{id} (обновить пользователя)
        addRoute("PUT", "/users/id", req -> {
            String path = req.getPath();
            String id = path.split("/")[2]; // Извлекаем id
            return new LiHttpResponse(200, "Пользователь " + id + " обновлён");
        });

        //PATCH /users/{id} (частичное обновление)
        addRoute("PATCH", "/users/id", req ->
                new LiHttpResponse(200, "Частичное обновление выполнено")
        );

        //DELETE /users/{id} (удалить пользователя)
        addRoute("DELETE", "/users/id", req ->
                new LiHttpResponse(200, "Пользователь удалён")
        );

        //проверка работоспособности сервера (JSON)
        addRoute("GET", "/health", req ->
                LiHttpResponse.json("{\"status\":\"OK\",\"timestamp\":" + System.currentTimeMillis() + "}"));


        //отдача статических файлов
        addRoute("GET", "/static/*", req -> {
            String requestedFile = req.getPath().substring("/static/".length());
            Path filePath = Paths.get("static_content", requestedFile).normalize();
            if (!filePath.startsWith("static_content/")) {
                return LiHttpResponse.json("{\"error\":\"Invalid path\"}").setStatusCode(400);
            }
            try {
                return LiHttpResponse.file(filePath);
            } catch (IOException e) {
                return LiHttpResponse.json("{\"error\":\"File not found\"}").setStatusCode(404);
            }
        });

        addRoute("POST", "/api/echo", req -> {
            try {
                String jsonBody = req.getBody();
                return LiHttpResponse.json("{\"received\":" + jsonBody + "}");
            } catch (Exception e) {
                return LiHttpResponse.json("{\"error\":\"Invalid JSON\"}").setStatusCode(400);
            }
        });
    }
}
