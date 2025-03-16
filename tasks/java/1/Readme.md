# Библиотека SimpleJson

Класс SimpleJson — это легковесная библиотека для работы с JSON в Java. Она позволяет парсить JSON-строки в объекты Java и преобразовывать объекты Java в JSON-строки. Поддерживаются основные структуры JSON, включая примитивы, строки, массивы, списки, карты (Map) и вложенные объекты.

## Публичные методы

### 1. parse(String json)
Парсит JSON-строку в необработанную Java-структуру:
- Map<String, Object> для JSON-объектов.
- List<Object> для JSON-массивов.
- String, Number, Boolean или null для примитивных значений.

Сигнатура:
public static Object parse(String json);

Пример:
String json = "{\"name\": \"John\", \"age\": 30}";
Object result = SimpleJson.parse(json);
System.out.println(result); // Вывод: {name=John, age=30}

---

### 2. parseToMap(String json)
Парсит JSON-строку в Map<String, Object>. Если корневой элемент не является JSON-объектом, метод возвращает null.

Сигнатура:
public static Map<String, Object> parseToMap(String json);

Пример:
String json = "{\"name\": \"John\", \"age\": 30}";
Map<String, Object> result = SimpleJson.parseToMap(json);
System.out.println(result); // Вывод: {name=John, age=30}

---

### 3. parse(String json, Class<T> clazz)
Парсит JSON-строку в объект указанного класса с использованием рефлексии. Метод сопоставляет поля JSON с соответствующими полями класса.

Сигнатура:
public static <T> T parse(String json, Class<T> clazz);

Пример:
String json = "{\"name\": \"John\", \"age\": 30}";
Person person = SimpleJson.parse(json, Person.class);
System.out.println(person.getName()); // Вывод: John

---

### 4. toJson(Object obj)
Преобразует объект Java (включая коллекции, карты и пользовательские объекты) в JSON-строку.

Сигнатура:
public static String toJson(Object obj);

Пример:
Person person = new Person("John", 30);
String json = SimpleJson.toJson(person);
System.out.println(json); // Вывод: {"name":"John","age":30}

---

## Поддерживаемые возможности
- Примитивные типы и их обёртки (int, Integer, double, Double и т.п.).
- Строки (String).
- Массивы.
- Списки (List).
- Карты (Map<String, Object>).
- Вложенные объекты.

---

## Ограничения
- Не обрабатывает все синтаксические ошибки в JSON.
- Не поддерживает циклические зависимости.
- Не поддерживает нестандартные типы, которые невозможно представить в JSON.

---