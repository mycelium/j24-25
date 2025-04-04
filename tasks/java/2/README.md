# HTTP Server
LiHttpServer - представляет собой реализацию части протокола HTTP 1.1 с использованием ServerSocketChannel (java.nio), написанную на Java. Поддерживает основные HTTP методы, обработку JSON, файлов, маршрутизацию запросов и может работать как с виртуальными, так и с обычными потоками.

### Функционал HTTP сервера
**Поддерживаемые HTTP методы:**
- GET
- POST
- PUT
- PATCH
- DELETE

**Работа с запросами:**
- Доступ к заголовкам через Map-интерфейс
- Парсинг тела запроса

**Основные возможности библиотеки:**
1. Создание HTTP сервера на указанном хосте и порту
2. Добавление обработчиков для конкретных путей и методов
3. Доступ к параметрам запроса:
   - Заголовки
   - Метод
   - Путь
   - Тело запроса
4. Формирование HTTP ответов

Предоставляется публичный API для:
- Конфигурации сервера
- Регистрации/создания обработчиков
- Взаимодействия с HTTP запросами и ответами

**Многопоточная обработка:**
- Поддержка многопоточной обработки запросов
- Количество потоков настраивается
- Параметр isVirtual для выбора типа Executor:
  - true - виртуальные потоки (Virtual Threads)
  - false - обычные потоки

## Основные компоненты
- **LiHttpRequest** - модель HTTP запроса
- **LiHttpResponse** - модель HTTP ответа
- **LiHttpServer** - основной класс сервера
- **Router** - маршрутизатор запросов
- **LiServerConfig** - конфигурация сервера
- **LiRequestHandler** - обработчик входящих запросов

## Использование

Создание экземпляра сервера:
```java
LiServerConfig config = LiServerConfig.defaultConfig(); //стандартная конфигурация сервера
Router router = new Router();
router.addDefaultRoutes(); 
LiHttpServer server = new LiHttpServer(config, router);
```

Запуск сервера:
```java
server.start();
```

Добавление обработчиков:
```java
router.addRoute("GET", "/hello", req -> 
    new LiHttpResponse(200, "Hello World!")
);

router.addRoute("POST", "/users", req -> {
    try {
        User user = req.parseJson(User.class);
        // Обработка пользователя
        return LiHttpResponse.json("{\"status\":\"created\"}");
    } catch (IOException e) {
        return new LiHttpResponse(400, "Invalid JSON");
    }
});

router.addRoute("GET", "/static/*", req -> {
    String filePath = req.getPath().substring("/static/".length());
    try {
        return LiHttpResponse.file(Paths.get("static", filePath));
    } catch (IOException e) {
        return new LiHttpResponse(404, "File not found");
    }
});
```

Более полные примеры использования сервера можно найти в классе LiHttpServerTest.

## Ограничения
- Поддерживается только HTTP/1.1
- Нет встроенной поддержки HTTPS

