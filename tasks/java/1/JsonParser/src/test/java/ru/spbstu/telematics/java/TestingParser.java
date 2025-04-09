package ru.spbstu.telematics.java;
import org.junit.jupiter.api.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ALL")
public class TestingParser {
    //примеры с наследованием
    //1. пример с котиками
    private static class AnimalPart {
        String name = "part";

        public String getName() {
            return name;
        }
    }

    private static class Tail extends AnimalPart {
        double lenght = 10.2;

        public Tail() {
            name = "Pretty fluffy tail";
        }
    }

    private static class Claw extends AnimalPart {
        public Claw() {
            name = "claw";
        }

        @Override
        public String getName() {
            return "Sharp claw";
        }
    }

    private static class Paw extends AnimalPart {
        boolean isFront = true;
        List<Claw> claws = new ArrayList<>();

        @JsonCreator
        public Paw(@JsonProperty("isFront") boolean isFront, @JsonProperty("claws") Claw... claws) {
            name = "paw";
            this.isFront = isFront;
            this.claws.addAll(Arrays.asList(claws));
        }

        @Override
        public String getName() {
            return isFront ? "Front paw" : "Back paw";
        }
    }

    private static class Cat {
        List<AnimalPart> parts = new LinkedList<>();

        @JsonCreator
        public Cat(@JsonProperty("tail") Tail tail,  @JsonProperty("paws") Paw... paws) {
            super();
            parts.add(tail);
            parts.addAll(Arrays.asList(paws));
        }
    }

    //дополнительный метод
    private Object transformCatsParts(Object parsedJson) {
        if (!(parsedJson instanceof Map)) return parsedJson;

        Map<String, Object> root = (Map<String, Object>) parsedJson;
        Object partsObj = root.get("parts");
        if (!(partsObj instanceof List)) return parsedJson;

        List<Map<String, Object>> parts = (List<Map<String, Object>>) partsObj;
        Map<String, Object> tail = null;
        List<Map<String, Object>> paws = new ArrayList<>();

        for (Map<String, Object> part : parts) {
            String name = (String) part.get("name");
            if (name == null) continue;

            if (name.equals("Pretty fluffy tail")) {
                tail = part;
            } else if (name.equals("paw") || name.equals("Front paw") || name.equals("Back paw")) {
                paws.add(part);
            }
        }

        Map<String, Object> transformed = new LinkedHashMap<>();
        if (tail != null) transformed.put("tail", tail);
        transformed.put("paws", paws);

        return transformed;
    }

    //тестирование примера с котиками
    @Test
    void testFullCat() {
        Cat originalCat = new Cat(
                new Tail(),
                new Paw(true, new Claw(), new Claw(), new Claw(), new Claw()),
                new Paw(true, new Claw(), new Claw(), new Claw(), new Claw()),
                new Paw(false, new Claw(), new Claw(), new Claw(), new Claw()),
                new Paw(false, new Claw(), new Claw(), new Claw(), new Claw())
        );

        // сериализуем кота в JSON
        JsonConverter converter = new JsonConverter();
        String json = converter.toJson(originalCat);
        assertNotNull(json);
        System.out.println("JSON:\n" + json);

        // десериализуем обратно
        JsonParser parser = new JsonParser();
        Object parsed = parser.parse(json);
        Object transformed = transformCatsParts(parsed); //доп метод
        Cat restoredCat = converter.fromJson(transformed, Cat.class);

        // проверяем структуру
        assertNotNull(restoredCat, "Кот не должен быть null");
        assertEquals(5, restoredCat.parts.size(), "Должно быть 5 частей (1 хвост + 4 лапы)");

        // проверяем хвост
        Tail tail = (Tail) restoredCat.parts.get(0);
        assertEquals("Pretty fluffy tail", tail.name);
        assertEquals(10.2, tail.lenght);

        // проверяем лапы и когти
        checkPaw((Paw) restoredCat.parts.get(1), true, 4);
        checkPaw((Paw) restoredCat.parts.get(2), true, 4);
        checkPaw((Paw) restoredCat.parts.get(3), false, 4);
        checkPaw((Paw) restoredCat.parts.get(4), false, 4);

        //сериализуем кота в json и сраниваваем с исходным json
        String json2 = converter.toJson(restoredCat);
        assertEquals(json2, json);


    }

    private void checkPaw(Paw paw, boolean expectedIsFront, int expectedClaws) {
        assertNotNull(paw, "Лапа не должна быть null");
        assertEquals("paw", paw.name);
        assertEquals(expectedIsFront, paw.isFront, "Неверное положение лапы");
        assertEquals(expectedClaws, paw.claws.size(), "Неверное количество когтей");

        for (Claw claw : paw.claws) {
            assertEquals("claw", claw.name, "Имя когтя не совпадает");
        }
    }

    //2. другой пример - с птицами
    private static class Wing extends AnimalPart {
        double span = 2.5;

        public Wing() {
            name = "bird wing";
        }

        @Override
        public String getName() {
            return "Bird wing";
        }
    }

    private static class Beak extends AnimalPart {
        public Beak() {
            name = "bird beak";
        }

        @Override
        public String getName() {
            return "Sharp beak";
        }
    }

    private static class Bird {
        List<AnimalPart> parts = new ArrayList<>();

        @JsonCreator
        public Bird(@JsonProperty("wing") Wing wing, @JsonProperty("beak") Beak beak) {
            parts.add(wing);
            parts.add(beak);
        }
    }

    // дополнительный метод
    private Object transformBirdParts(Object parsedJson) {
        if (!(parsedJson instanceof Map)) return parsedJson;

        Map<String, Object> root = (Map<String, Object>) parsedJson;
        Object partsObj = root.get("parts");
        if (!(partsObj instanceof List)) return parsedJson;

        List<Map<String, Object>> parts = (List<Map<String, Object>>) partsObj;
        Map<String, Object> wing = null;
        Map<String, Object> beak = null;

        for (Map<String, Object> part : parts) {
            String name = (String) part.get("name");
            if (name == null) continue;

            if (name.equals("bird wing")) {
                wing = part;
            } else if (name.equals("bird beak")) {
                beak = part;
            }
        }

        Map<String, Object> transformed = new LinkedHashMap<>();
        if (wing != null) transformed.put("wing", wing);
        if (beak != null) transformed.put("beak", beak);

        return transformed;
    }

    // тестирование примера с птицей
    @Test
    void testBirdSerialization() {
        Bird originalBird = new Bird(
                new Wing(),
                new Beak()
        );

        // cериализуем в json
        JsonConverter converter = new JsonConverter();
        String json = converter.toJson(originalBird);
        assertNotNull(json);
        System.out.println("JSON:\n" + json);

        // десериализуем обратно
        JsonParser parser = new JsonParser();
        Object parsed = parser.parse(json);
        Object transformed = transformBirdParts(parsed); // доп метод
        Bird restoredBird = converter.fromJson(transformed, Bird.class);

        // проверяем структуру
        assertNotNull(restoredBird, "Птица не должна быть null");
        assertEquals(2, restoredBird.parts.size(), "Должно быть 2 части (крыло и клюв)");

        // проверяем крыло
        Wing wing = (Wing) restoredBird.parts.get(0);
        assertEquals("bird wing", wing.name);
        assertEquals(2.5, wing.span);

        // проверяем клюв
        Beak beak = (Beak) restoredBird.parts.get(1);
        assertEquals("bird beak", beak.name);
    }

    //1. Json -> Java objects
    //1.1 в указанный класс
    private final JsonParser parser = new JsonParser();
    private final JsonConverter converter = new JsonConverter();

    // тестовые классы
    static class SimpleUser {
        public String name;
        public int age;
        public boolean active;
    }

    static class NestedObject {
        public SimpleUser user;
        public String status;
    }

    static class WithCollections {
        public List<Integer> numbers;
        public String[] tags;
    }

    @Test
    void testSimpleObjectConversion() throws Exception {
        String json = "{\"name\":\"Alice\",\"age\":25,\"active\":true}";
        Object parsed = parser.parse(json);

        SimpleUser user = converter.fromJson(parsed, SimpleUser.class);

        assertEquals("Alice", user.name);
        assertEquals(25, user.age);
        assertTrue(user.active);
    }

    @Test
    void testNestedObjectConversion() {
        String json = "{\"user\":{\"name\":\"Bob\",\"age\":30,\"active\":false},\"status\":\"guest\"}";
        Object parsed = parser.parse(json);

        NestedObject obj = converter.fromJson(parsed, NestedObject.class);

        assertEquals("Bob", obj.user.name);
        assertEquals(30, obj.user.age);
        assertFalse(obj.user.active);
        assertEquals("guest", obj.status);
    }

    @Test
    void testCollectionsConversion() {
        String json = "{\"numbers\":[1,2,3],\"tags\":[\"java\",\"json\"]}";
        Object parsed = parser.parse(json);

        WithCollections withCollections = converter.fromJson(parsed, WithCollections.class);

        assertEquals(Arrays.asList(1, 2, 3), withCollections.numbers);
        assertArrayEquals(new String[]{"java", "json"}, withCollections.tags);
    }

    @Test
    void testNullConversation() {
        String json = "{\"name\":null,\"age\":0}";
        Object parsed = parser.parse(json);

        SimpleUser user = converter.fromJson(parsed, SimpleUser.class);

        assertNull(user.name);
        assertEquals(0, user.age);
    }
    //1.2 парсинг строк
    @Test
    public void testSimpleString() {
        JsonParser parser = new JsonParser();
        String result = (String) parser.parse("\"hello world\"");
        assertEquals("hello world", result);

        //пустая строка
        String res2 = (String) parser.parse("\"\"");
        assertEquals("", res2);
    }

    @Test
    public void testEscapeCharacters() {
        JsonParser parser = new JsonParser();
        String result = (String) parser.parse("\"line1\\nline2\\ttab\"");
        assertEquals("line1\nline2\ttab", result);
    }

    @Test
    public void testUnicodeCharacter() {
        JsonParser parser = new JsonParser();
        String result = (String) parser.parse("\"\\u041F\\u0440\\u0438\\u0432\\u0435\\u0442\"");
        assertEquals("Привет", result);
    }

    //1.3 парсинг чисел
    @Test
    public void testInteger() {
        JsonParser parser = new JsonParser();
        Number result = (Number) parser.parse("42");
        assertEquals(Long.valueOf(42), result.longValue());
    }

    @Test
    public void testNegativeFloat() {
        JsonParser parser = new JsonParser();
        Number result = (Number) parser.parse("-12.34");
        assertEquals(-12.34, result.doubleValue());
    }

    @Test
    public void testExponentNotation() {
        JsonParser parser = new JsonParser();
        Number result = (Number) parser.parse("1.5e2");
        assertEquals(150.0, result.doubleValue());
    }

    //1.4 парсинг массивов
    @Test
    public void testEmptyArray() {
        JsonParser parser = new JsonParser();
        List<Object> result = (List<Object>) parser.parse("[]");
        assertTrue(result.isEmpty());
    }

    @Test
    public void testMixedTypeArray() {
        JsonParser parser = new JsonParser();
        List<Object> result = (List<Object>) parser.parse("[64, \"джава\", true, null]");

        assertEquals(4, result.size());
        assertEquals(Long.valueOf(64), result.get(0));
        assertEquals("джава", result.get(1));
        assertEquals(true, result.get(2));
        assertNull(result.get(3));
    }

    //1.5 парсинг объектов Map<String, Object>
    @Test
    public void testSimpleObject() {
        JsonParser parser = new JsonParser();
        Map<String, Object> res= (Map<String, Object>) parser.parse("{\"name\": \"Julia\", \"age\": 20}");

        assertEquals(2, res.size());
        assertEquals("Julia", res.get("name"));
        assertEquals(Long.valueOf(20), res.get("age"));
    }

    @Test
    public void testEmptyObject() {
        JsonParser parser = new JsonParser();
        Map<String, Object> result = (Map<String, Object>) parser.parse("{}");

        assertTrue(result.isEmpty());
    }

    @Test
    public void testNestedObject() {
        JsonParser parser = new JsonParser();
        Map<String, Object> result = (Map<String, Object>) parser.parse(
                "{\"user\": {\"id\": 1, \"name\": \"Bob\"}, \"active\": true}");

        assertEquals(2, result.size());

        Map<String, Object> user = (Map<String, Object>) result.get("user");
        assertEquals(Long.valueOf(1), user.get("id"));
        assertEquals("Bob", user.get("name"));

        assertEquals(true, result.get("active"));

    }

    //2. Java objects -> Json
    @Test
    void testSimpleObjectToJson() {
        SimpleUser user = new SimpleUser();
        user.name = "Nastya";
        user.age = 12;
        user.active = true;

        // в json
        JsonConverter converter = new JsonConverter();
        String json = converter.toJson(user);
        assertNotNull(json);

        // проверяем, что полученный json соответствует ожидаемому
        String expectedJson = "{\"name\":\"Nastya\",\"age\":12,\"active\":true}";
        assertEquals(expectedJson, json);
    }

    @Test
    void testNestedObjectToJson() {
        SimpleUser user = new SimpleUser();
        user.name = "Bob";
        user.age = 30;
        user.active = false;

        NestedObject nestedObject = new NestedObject();
        nestedObject.user = user;
        nestedObject.status = "guest";

        // сериализуем вложенный объект в json
        JsonConverter converter = new JsonConverter();
        String json = converter.toJson(nestedObject);
        assertNotNull(json);

        // проверяем, что полученный json соответствует ожидаемому
        String expectedJson = "{\"user\":{\"name\":\"Bob\",\"age\":30,\"active\":false},\"status\":\"guest\"}";
        assertEquals(expectedJson, json);
    }

    @Test
    void testCollectionsToJson() {
        WithCollections withCollections = new WithCollections();
        withCollections.numbers = Arrays.asList(1, 2, 3);
        withCollections.tags = new String[]{"java", "json"};

        // cериализуем объект с коллекциями в json
        JsonConverter converter = new JsonConverter();
        String json = converter.toJson(withCollections);
        assertNotNull(json);

        // проверяем, что полученный json соответствует ожидаемому
        String expectedJson = "{\"numbers\":[1,2,3],\"tags\":[\"java\",\"json\"]}";
        assertEquals(expectedJson, json);
    }

}

