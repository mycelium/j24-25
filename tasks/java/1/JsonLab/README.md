# JSON Parser Library

## Описание

Библиотека `JsonParser` предоставляет функциональность для сериализации и десериализации Java-объектов в JSON и обратно. Реализация поддерживает работу с примитивными типами, объектами, коллекциями, массивами и enum'ами.

## Основные возможности

- **Сериализация** Java-объектов в JSON-строку
- **Десериализация** JSON-строк в Java-объекты
- Поддержка **вложенных объектов** и **массивов**
- Обработка **пользовательских классов**
- Поддержка **коллекций** (List, Map)
- Работа с **enum'ами** и **примитивными типами**
- Гибкая система **обработки ошибок**

## Использование

### Зависимости

Просто добавьте файл `JsonParser.java` в ваш проект.

### Примеры использования

#### 1. Сериализация объекта в JSON

```java
public class User {
    private String name;
    private int age;
    private List<String> hobbies;
    
    // конструкторы, геттеры, сеттеры
}

User user = new User("John", 30, Arrays.asList("Reading", "Swimming"));
String json = JsonParser.parseToString(user);
System.out.println(json);
// Вывод: {"name":"John","age":30,"hobbies":["Reading","Swimming"]}
```

#### 2. Десериализация JSON в объект

```java
String json = "{\"name\":\"John\",\"age\":30,\"hobbies\":[\"Reading\",\"Swimming\"]}";
User user = JsonParser.parseStringToClass(json, User.class);
```


#### 3. Работа с коллекциями
Сериализация списка
```java
List<Integer> numbers = Arrays.asList(1, 2, 3);
String json = JsonParser.parseToString(numbers);
// Результат: "[1,2,3]"
```
Десериализация списка
```java

String jsonArray = "[1, 2, 3]";
List<Integer> numbers = JsonParser.parseStringToClass(jsonArray, List.class);
```
#### 4. Работа с Map
Сериализация Map
```java

Map<String, Object> data = new HashMap<>();
data.put("key1", "value1");
data.put("key2", 123);
String json = JsonParser.parseToString(data);
// Результат: {"key1":"value1","key2":123}
```
Десериализация Map
```java

String jsonMap = "{\"name\":\"John\",\"age\":30}";
Map<String, Object> map = JsonParser.parseStringToClass(jsonMap, Map.class);
```
### Обработка ошибок
Библиотека выбрасывает следующие исключения:

- JsonException - базовое исключение для всех ошибок парсинга
- InvalidJsonStringException - неверный формат JSON-строки
- InvalidPairParsingException - ошибка парсинга пары ключ-значение
- MappingException - ошибка маппинга JSON на Java-объект-
- TypeMismatchException - несоответствие типов-
- FieldNotFoundException - поле не найдено в целевом классе

### Ограничения
- Не поддерживаются циклические ссылки в объектах
- Не поддерживаются пользовательские сериализаторы/десериализаторы
- Ограниченная поддержка полиморфизма
- При работе с generic-типами может потребоваться дополнительное приведение типов

## Дополнительные возможности
- Библиотека поддерживает поиск и обработку подклассов:

```java
List<Class<?>> subclasses = JsonParser.findSubclasses(ParentClass.class, "ru.spbstu.telematics.java");
```
