# JSON Parser

LiJson — это  библиотека для парсинга и сериализации JSON на языке Java. Предоставляет простой API для работы с JSON-объектами и массивами.

## Основные возможности

- **Парсинг JSON**: преобразование json-строк в Java-объекты, `Map<String, Object>` и пользовательские классы.
- **Сериализация JSON**: преобразование Java-объектов в json-строки.
- **Поддержка типов**: примитивы, упакованные типы, `null`, массивы и вложенные объекты.
- **Обработка коллекций**: поддержка `List`, `Map` и других коллекций.

## Использование

### Парсинг JSON
Для парсинга json-объекта/массива в Java-объект/`Map<String, Object>` используйте метод `parseCommon` класса `LiJsonParser`:  

Парсинг json-массива в `List<Object>`:
```java
String json = "[1, 2, 3, {\"key\": \"value\"}]";
LiJsonParser parser = new LiJsonParser(json);
Object result = parser.parseCommon();
\\result: [1, 2, 3, {"key": "value"}]
```

Парсинг json-объекта в `Map<String,Object>`:
```java
String json = "{\"name\": \"Din\", \"age\": 27, \"isFemale\": false}";
        LiJsonParser parser = new LiJsonParser(json);
        Object result = parser.parseCommon();
\\result: {"name": "Din", "age": 27, "isFemale": false}
```

Для парсинга json-объекта в пользовательский класс, используйте метод `parseJsObjectToClass` класса `LiJsonParser`:  

Парсинг json-объекта в пользовательский класс:
```java
String json = "{"
                + "\"name\": \"Din\","
                + "\"surname\": \"Don\","
                + "\"age\": 27,"
                + "\"isOnline\": true,"
                + "\"favMovies\": [\"The Shawshank Redemption\", \"Fight Club\"]"
                + "}";
LiJsonParser parser = new LiJsonParser(json);
LiJsonUser user = parser.parseJsObjectToClass(LiJsonUser.class);
\\result: {name: "Din", surname: "Don", age: 27, isOnline: true, favMovies: ["The Shawshank Redemption", "Fight Club"]}
```

### Сериализация JSON

Для сериализации Java-объекта в json-строку используйте метод `serializeToJson` класса `LiJsonSerializer`:

Сериализация `Map` в json-объект:
```java
LiJsonSerializer serializer = new LiJsonSerializer();
Map<String, Object> map = new HashMap<>();
map.put("key1", 1);
map.put("key2", "value2");
String result = serializer.serializeToJson(map);
\\result: "{\"key1\":1,\"key2\":\"value2\"}"
```

Сериализация `List` в json-массив:
```java
LiJsonSerializer serializer = new LiJsonSerializer();
List<Object> list = new ArrayList<>();
list.add("element1");
list.add(2);
String result = serializer.serializeToJson(list);
\\result: "[\"element1\",2]"
```

Сериализация пользовательского класса в json-объект:
```java
LiJsonSerializer serializer = new LiJsonSerializer();
LiJsonUser user = new LiJsonUser();
user.setName("Din");
user.setSurname("Don");
user.setAge(27);
user.setOnline(true);
user.setFavMovies(Arrays.asList("The Shawshank Redemption", "Fight Club"));
String result = serializer.serializeToJson(user);
\\result: "{\"name\":\"Din\",\"surname\":\"Don\",\"age\":27,\"isOnline\":true,\"favMovies\":[\"The Shawshank Redemption\",\"Fight Club\"]}"
```

## Ограничения
Не поддерживается:  
- обработка циклических зависимостей,  
- типы, не представляемые в JSON.
