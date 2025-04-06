package ru.spbstu.lab;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class SimpleJsonTest {

    /**
     * Тестовый класс для проверки десериализации.
     */
    public static class UserTest {
        public String name;
        public int age;
        public boolean isActive;
        public Double salary;
        public String[] tags;

        public UserTest() {
        }

        public UserTest(String name, int age, boolean isActive) {
            this.name = name;
            this.age = age;
            this.isActive = isActive;
        }

        public UserTest(String name, int age, boolean isActive, Double salary, String[] tags) {
            this.name = name;
            this.age = age;
            this.isActive = isActive;
            this.salary = salary;
            this.tags = tags;
        }
    }

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

    @JsonDeserialize(using = Paw.PawDeserializer.class)
    private static class Paw extends AnimalPart {
        boolean isFront = true;
        List<Claw> claws = new ArrayList<>();
        public Paw(boolean isFront, Claw... claws) {
            name = "paw";
            this.isFront = isFront;
            this.claws.addAll(Arrays.asList(claws));
        }
        @Override
        public String getName() {
            return isFront ? "Front paw" : "Back paw";
        }

        public static class PawDeserializer implements JsonDeserializer<Paw> {
            @Override
            public Paw deserialize(Map<String, Object> map) {
                // 1. Извлекаем значение isFront (с учетом возможных вариантов именования)
                boolean isFront = (boolean) map.getOrDefault("isFront", true);

                // 2. Десериализуем когти
                List<Claw> claws = new ArrayList<>();
                Object rawClaws = map.get("claws");

                if (rawClaws instanceof List<?> clawsList) {
                    for (Object clawData : clawsList) {
                        if (clawData instanceof Map) {
                            Claw claw = SimpleJson.parseFromMap((Map<String, Object>) clawData, Claw.class);
                            claws.add(claw);
                        }
                    }
                }

                // 3. Создаем объект Paw
                return new Paw(isFront, claws.toArray(new Claw[0]));
            }
        }
    }

    @JsonDeserialize(using = Cat.CatDeserializer.class)
    private static class Cat {
        List<AnimalPart> parts = new LinkedList<>();
        public Cat(Tail tail, Paw... paws) {
            super();
            parts.add(tail);
            parts.addAll(Arrays.asList(paws));
        }

        public static class CatDeserializer implements JsonDeserializer<Cat> {

            @Override
            public Cat deserialize(Map<String, Object> map) {
                List<Map<String, Object>> parts = (List<Map<String, Object>>) map.get("parts");
                // 1. Десериализация хвоста
                Tail tailData = parts.stream().filter((part) -> part.containsKey("lenght")).map((tail) ->
                        SimpleJson.parseFromMap(tail, Tail.class)
                ).findFirst().orElseThrow(
                        () -> new IllegalArgumentException("Cat JSON must contain 'tail'")
                );

                // 2. Десериализация лап
                List<Paw> pawList = parts.stream().filter((part) -> part.containsKey("claws")).map((paw) ->
                        SimpleJson.parseFromMap(paw, Paw.class)
                ).toList();

                // 2. Десериализация лап
                List<Paw> paws = new ArrayList<>();
                Object rawPaws = map.get("paws");
                if (rawPaws instanceof List) {
                    for (Object pawData : (List<?>) rawPaws) {
                        if (pawData instanceof Map) {
                            Paw paw = SimpleJson.parseFromMap((Map<String, Object>) pawData, Paw.class);
                            paws.add(paw);
                        }
                    }
                }

                // 3. Создаем объект Cat
                return new Cat(tailData, pawList.toArray(new Paw[0]));
            }
        }
    }

    public static class SuperUserTest extends UserTest {
        public String role;

        public SuperUserTest() {
            super();
        }

        public SuperUserTest(String name, int age, boolean isActive, String role) {
            super(name, age, isActive);
            this.role = role;
        }

        public SuperUserTest(String name, int age, boolean isActive, Double salary, String[] tags, String role) {
            super(name, age, isActive, salary, tags);
            this.role = role;
        }
    }

    /**
     * Тестирует parseToMap на простом JSON-объекте.
     */
    @Test
    public void testParseToMapSimple() {
        String json = "{\"name\":\"Alice\",\"age\":30,\"isActive\":true}";
        Map<String, Object> result = SimpleJson.parseToMap(json);

        Assertions.assertNotNull(result, "Результат не должен быть null");
        Assertions.assertEquals("Alice", result.get("name"));
        Assertions.assertEquals(Integer.valueOf(30), result.get("age"));
        Assertions.assertEquals(Boolean.valueOf(true), result.get("isActive"));
    }

    @Test
    public void testCatSerializationDeserialization() {
        // Создаём тестового кота с хвостом и 4 лапами (по 4 когтя на каждой):
        Cat cat = new Cat(
                new Tail(),
                new Paw(true, new Claw(), new Claw(), new Claw(), new Claw()),
                new Paw(true, new Claw(), new Claw(), new Claw(), new Claw()),
                new Paw(false, new Claw(), new Claw(), new Claw(), new Claw()),
                new Paw(false, new Claw(), new Claw(), new Claw(), new Claw())
        );

        // Сериализация в JSON
        String catJson = SimpleJson.toJson(cat);
        Assertions.assertNotNull(catJson, "JSON-представление кота не должно быть null");

        // Десериализация обратно в объект Cat
        Cat parsedCat = SimpleJson.parse(catJson, Cat.class);
        Assertions.assertNotNull(parsedCat, "Десериализованный кот не должен быть null");

        // Базовые проверки структуры:
        // - У кота должна быть 1 часть-хвост и 4 части-лапы => всего 5 частей.
        Assertions.assertEquals(5, parsedCat.parts.size(), "У кота должно быть 5 частей (1 хвост + 4 лапы)");

        // Дополнительно проверим, что первая часть действительно хвост:
        AnimalPart tail = parsedCat.parts.get(0);
        Assertions.assertInstanceOf(Tail.class, tail, "Первая часть должна быть хвостом");
        Assertions.assertEquals("Pretty fluffy tail", tail.getName(), "Название хвоста должно совпадать с исходным");

        // Проверим лапы:
        // - Следующие 4 части — это лапы
        for (int i = 1; i < 5; i++) {
            AnimalPart part = parsedCat.parts.get(i);
            Assertions.assertTrue(part instanceof Paw, "Ожидается, что часть №" + i + " будет лапой");
            Paw paw = (Paw) part;

            // Первая и вторая лапы — передние; третья и четвёртая — задние
            boolean expectedIsFront = (i == 1 || i == 2);
            Assertions.assertEquals(expectedIsFront, paw.isFront, "Признак передней/задней лапы должен совпадать");

            // Проверяем количество когтей:
            Assertions.assertEquals(4, paw.claws.size(), "У каждой лапы должно быть по 4 когтя");
            for (Claw claw : paw.claws) {
                // По условию Claw через getName() возвращает "Sharp claw"
                Assertions.assertEquals("Sharp claw", claw.getName(), "Название когтя после десериализации");
            }
        }
    }

    /**
     * Тестирует parseToMap на более сложном JSON с вложенными структурами.
     */
    @Test
    public void testParseToMapNested() {
        String json = "{\"team\":\"Developers\","
                + "\"members\":["
                + "   {\"name\":\"Bob\",\"age\":25,\"isActive\":false},"
                + "   {\"name\":\"Kate\",\"age\":22,\"isActive\":true}"
                + "]}";

        Map<String, Object> map = SimpleJson.parseToMap(json);

        Assertions.assertNotNull(map, "Результат не должен быть null");
        Assertions.assertEquals("Developers", map.get("team"));
        @SuppressWarnings("unchecked")
        List<Object> members = (List<Object>) map.get("members");
        Assertions.assertEquals(2, members.size());

        @SuppressWarnings("unchecked")
        Map<String, Object> member1 = (Map<String, Object>) members.get(0);
        Assertions.assertEquals("Bob", member1.get("name"));
        Assertions.assertEquals(Integer.valueOf(25), member1.get("age"));
        Assertions.assertEquals(Boolean.valueOf(false), member1.get("isActive"));

        @SuppressWarnings("unchecked")
        Map<String, Object> member2 = (Map<String, Object>) members.get(1);
        Assertions.assertEquals("Kate", member2.get("name"));
        Assertions.assertEquals(Integer.valueOf(22), member2.get("age"));
        Assertions.assertEquals(Boolean.valueOf(true), member2.get("isActive"));
    }

    /**
     * Тестирует parse(String json, Class<T> clazz)
     * на простом примере (User).
     */
    @Test
    public void testParseToObjectSimple() {
        String json = "{\"name\":\"Bob\",\"age\":25,\"isActive\":false}";
        UserTest user = SimpleJson.parse(json, UserTest.class);

        Assertions.assertNotNull(user, "Сконвертированный объект не должен быть null");
        Assertions.assertEquals("Bob", user.name);
        Assertions.assertEquals(25, user.age);
        Assertions.assertFalse(user.isActive);
    }

    /**
     * Тестирует parse(String json, Class<T> clazz)
     * при наличии массива в полях (tags).
     */
    @Test
    public void testParseToObjectWithArray() {
        String json = "{\"name\":\"Charlie\",\"age\":29,\"isActive\":true,"
                + "\"salary\":1234.56,\"tags\":[\"alpha\",\"beta\"]}";
        UserTest user = SimpleJson.parse(json, UserTest.class);

        Assertions.assertNotNull(user, "Сконвертированный объект не должен быть null");
        Assertions.assertEquals("Charlie", user.name);
        Assertions.assertEquals(29, user.age);
        Assertions.assertTrue(user.isActive);
        Assertions.assertEquals(1234.56, user.salary);
        Assertions.assertNotNull(user.tags);
        Assertions.assertArrayEquals(new String[]{"alpha", "beta"}, user.tags);
    }

    /**
     * Тестирует метод toJson(Object):
     * Преобразование простого объекта (User) в JSON-строку.
     */
    @Test
    public void testToJsonSimple() {
        UserTest user = new UserTest("Alice", 30, true);
        String json = SimpleJson.toJson(user);

        Assertions.assertTrue(json.contains("\"name\":\"Alice\""), "JSON должен содержать поле name");
        Assertions.assertTrue(json.contains("\"age\":30"), "JSON должен содержать поле age");
        Assertions.assertTrue(json.contains("\"isActive\":true"), "JSON должен содержать поле isActive");
    }

    /**
     * Тестирует метод toJson(Object) на коллекции пользователей.
     */
    @Test
    public void testToJsonCollection() {
        List<UserTest> users = new ArrayList<>();
        users.add(new UserTest("Tom", 40, false));
        users.add(new UserTest("Sarah", 28, true));

        String json = SimpleJson.toJson(users);

        Assertions.assertTrue(json.startsWith("["), "JSON должен начинаться с [");
        Assertions.assertTrue(json.endsWith("]"), "JSON должен заканчиваться на ]");
        Assertions.assertTrue(json.contains("\"name\":\"Tom\""));
        Assertions.assertTrue(json.contains("\"age\":40"));
        Assertions.assertTrue(json.contains("\"isActive\":false"));

        Assertions.assertTrue(json.contains("\"name\":\"Sarah\""));
        Assertions.assertTrue(json.contains("\"age\":28"));
        Assertions.assertTrue(json.contains("\"isActive\":true"));
    }

/**
 * Тест "туда и обратно": сериализация в JSON -> десериализация.
 * Проверяем, что данные сохраняются без искажений */
@Test
public void testRoundTrip() {
    UserTest original = new UserTest("Diana", 32, true, Double.valueOf(999.99), new String[]{"dev","admin"});
    String json = SimpleJson.toJson(original);
    UserTest parsed = SimpleJson.parse(json, UserTest.class);

    Assertions.assertEquals("Diana", parsed.name);
    Assertions.assertEquals(32, parsed.age);
    Assertions.assertTrue(parsed.isActive);
    Assertions.assertEquals(999.99, parsed.salary);
    Assertions.assertArrayEquals(new String[]{"dev", "admin"}, parsed.tags);
}

    /**
     * Тестирует parse(String) в качестве "сырых" данных
     * (Map или List) при неизвестной структуре на входе.
     */
    @Test
    public void testParseRawValue() {
        String json = "[123, \"hello\", false, {\"k\": \"v\"}]";
        Object parsed = SimpleJson.parse(json);
        Assertions.assertTrue(parsed instanceof List, "Ожидается список в корне");
        List<?> list = (List<?>) parsed;
        Assertions.assertEquals(4, list.size());

        Assertions.assertEquals(Integer.valueOf(123), list.get(0));
        Assertions.assertEquals("hello", list.get(1));
        Assertions.assertEquals(Boolean.valueOf(false), list.get(2));
        Assertions.assertTrue(list.get(3) instanceof Map);
    }

    /**
     * Тест: JSON-строка "null" -> parse -> null, и toJson(null) -> "null".
     */
    @Test
    public void testParseAndToJsonNull() {
        String jsonNull = "null";
        Object parsed = SimpleJson.parse(jsonNull);
        Assertions.assertNull(parsed, "Ожидается null");

        String generated = SimpleJson.toJson(null);
        Assertions.assertEquals("null", generated, "toJson(null) должно вернуть 'null'");
    }



    @Test
    public void testSuperClassFieldsSerialization() {
        SuperUserTest superUser = new SuperUserTest("Ivan", 35, true, 2000.0, new String[]{"java", "json"}, "Admin");

        String json = SimpleJson.toJson(superUser);

        Assertions.assertTrue(json.contains("\"name\":\"Ivan\""), "JSON должен содержать поле name");
        Assertions.assertTrue(json.contains("\"age\":35"), "JSON должен содержать поле age");
        Assertions.assertTrue(json.contains("\"isActive\":true"), "JSON должен содержать поле isActive");
        Assertions.assertTrue(json.contains("\"salary\":2000.0"), "JSON должен содержать поле salary");
        Assertions.assertTrue(json.contains("\"tags\":[\"java\",\"json\"]"), "JSON должен содержать теги");

        Assertions.assertTrue(json.contains("\"role\":\"Admin\""), "JSON должен содержать поле role");

        SuperUserTest parsed = SimpleJson.parse(json, SuperUserTest.class);

        Assertions.assertEquals("Ivan", parsed.name);
        Assertions.assertEquals(35, parsed.age);
        Assertions.assertTrue(parsed.isActive);
        Assertions.assertEquals(2000.0, parsed.salary);
        Assertions.assertArrayEquals(new String[]{"java", "json"}, parsed.tags);
        Assertions.assertEquals("Admin", parsed.role);
    }
}