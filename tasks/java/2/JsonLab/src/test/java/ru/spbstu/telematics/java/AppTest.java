package ru.spbstu.telematics.java;

import org.junit.jupiter.api.Test;
import ru.spbstu.telematics.java.exceptions.JsonException;
import ru.spbstu.telematics.java.exceptions.MappingException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

enum TestEnum { VALUE1, VALUE2 }
class SimpleEntity {
    public String name;
    public int age;
    public boolean active;
    public TestEnum enumValue;
}

class NestedEntity {
    public SimpleEntity nested;
    public String description;
}

class CollectionEntity {
    public List<String> items;
    public int[] numbers;
}


public class AppTest {

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

        public Paw(boolean isFront, Claw... claws) {
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

        public Cat(Tail tail, Paw... paws) {
            super();
            parts.add(tail);
            parts.addAll(Arrays.asList(paws));
        }
    }

    @Test
    void parseString() throws JsonException, JsonException {
        String result = JsonParser.parseStringToClass("\"test\"", String.class);
        assertEquals("test", result);
    }

    @Test
    void parseInteger() throws JsonException {
        Integer result = JsonParser.parseStringToClass("42", Integer.class);
        assertEquals(42, result);
    }

    @Test
    void parseDouble() throws JsonException {
        Double result = JsonParser.parseStringToClass("3.14", Double.class);
        assertEquals(3.14, result, 0.001);
    }

    @Test
    void parseBoolean() throws JsonException {
        Boolean result = JsonParser.parseStringToClass("true", Boolean.class);
        assertTrue(result);
    }

    @Test
    void parseEnum() throws JsonException {
        TestEnum result = JsonParser.parseStringToClass("\"VALUE1\"", TestEnum.class);
        assertEquals(TestEnum.VALUE1, result);
    }

    @Test
    void parseSimpleObject() throws JsonException {
        String json = "{\"name\":\"John\",\"age\":30,\"active\":true,\"enumValue\":\"VALUE2\"}";
        SimpleEntity entity = JsonParser.parseStringToClass(json, SimpleEntity.class);

        assertEquals("John", entity.name);
        assertEquals(30, entity.age);
        assertTrue(entity.active);
        assertEquals(TestEnum.VALUE2, entity.enumValue);
    }

    @Test
    void parseNestedObject() throws JsonException {
        String json = "{\"nested\":{\"name\":\"Alice\",\"age\":25,\"active\":false},\"description\":\"test\"}";
        NestedEntity entity = JsonParser.parseStringToClass(json, NestedEntity.class);

        assertEquals("Alice", entity.nested.name);
        assertEquals(25, entity.nested.age);
        assertFalse(entity.nested.active);
        assertEquals("test", entity.description);
    }

    @Test
    void parseInvalidJsonThrowsException() {
        String invalidJson = "{name:\"John\"}"; // Нет кавычек у ключа
        assertThrows(JsonException.class, () ->
                JsonParser.parseStringToClass(invalidJson, SimpleEntity.class));
    }

    @Test
    void parseStringList() throws JsonException {
        String json = "[\"one\",\"two\",\"three\"]";
        List<String> result = JsonParser.parseStringToClass(json, List.class);

        assertEquals(3, result.size());
        assertEquals("two", result.get(1));
    }

    @Test
    void parseIntegerArray() throws JsonException {
        String json = "[1,2,3]";
        int[] result = JsonParser.parseStringToClass(json, int[].class);

        assertArrayEquals(new int[]{1, 2, 3}, result);
    }

    @Test
    void parseObjectWithCollections() throws JsonException {
        String json = "{\"items\":[\"a\",\"b\"],\"numbers\":[1,2,3]}";
        CollectionEntity entity = JsonParser.parseStringToClass(json, CollectionEntity.class);

        assertEquals(2, entity.items.size());
        assertEquals("b", entity.items.get(1));
        assertArrayEquals(new int[]{1, 2, 3}, entity.numbers);
    }

    @Test
    void nullInputThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                JsonParser.parseStringToClass(null, String.class));
    }

    @Test
    void invalidTypeThrowsException() {
        String json = "{\"name\":\"John\"}";
        assertThrows(MappingException.class, () ->
                JsonParser.parseStringToClass(json, Integer.class));
    }

    @Test
    void missingFieldThrowsException() {
        String json = "{\"unknown\":\"field\"}";
        assertThrows(MappingException.class, () ->
                JsonParser.parseStringToClass(json, SimpleEntity.class));
    }

    @Test
    void invalidEnumValueThrowsException() {
        String json = "{\"enumValue\":\"INVALID\"}";
        assertThrows(JsonException.class, () ->
                JsonParser.parseStringToClass(json, SimpleEntity.class));
    }

    @Test
    void emptyObject() throws JsonException {
        String json = "{}";
        SimpleEntity entity = JsonParser.parseStringToClass(json, SimpleEntity.class);
        assertNotNull(entity);
    }

    @Test
    void emptyArray() throws JsonException {
        String json = "[]";
        List<?> result = JsonParser.parseStringToClass(json, List.class);
        assertTrue(result.isEmpty());
    }

    @Test
    void nullValue() throws JsonException {
        String json = "{\"name\":null}";
        SimpleEntity entity = JsonParser.parseStringToClass(json, SimpleEntity.class);
        assertNull(entity.name);
    }

    @Test
    void nestedEmptyObjects() throws JsonException {
        String json = "{\"nested\":{},\"description\":\"test\"}";
        NestedEntity entity = JsonParser.parseStringToClass(json, NestedEntity.class);
        assertNotNull(entity.nested);
        assertEquals("test", entity.description);
    }

    @Test
    void complexParsingScenario() throws JsonException {
        String json = """
                {
                    "nested": {
                        "name": "Test",
                        "age": 10,
                        "active": true,
                        "enumValue": "VALUE1"
                    },
                    "description": "complex test"
                }""";

        NestedEntity result = JsonParser.parseStringToClass(json, NestedEntity.class);

        assertEquals("Test", result.nested.name);
        assertEquals(10, result.nested.age);
        assertTrue(result.nested.active);
        assertEquals(TestEnum.VALUE1, result.nested.enumValue);
        assertEquals("complex test", result.description);
    }

    // Тестовые классы для проверки наследования
    static class Parent {
        private String parentField = "parent_value";

        public String getParentField() {
            return parentField;
        }
    }

    static class Child extends Parent {
        private String childField = "child_value";

        public String getChildField() {
            return childField;
        }
    }

    static class ComplexObject {
        private transient String transientField = "should_not_serialize";
        private static String staticField = "static_value";
        private List<String> list = Arrays.asList("a", "b");
        private Map<String, Integer> map = new HashMap<>() {{
            put("key1", 1);
            put("key2", 2);
        }};
    }

    // 1. Тестирование примитивных типов и строк
    @Test
    void testPrimitivesAndStrings() throws Exception {
        assertEquals("123", JsonParser.parseToString(123));
        assertEquals("123.45", JsonParser.parseToString(123.45));
        assertEquals("true", JsonParser.parseToString(true));
        assertEquals("\"test\"", JsonParser.parseToString("test"));
        assertEquals("null", JsonParser.parseToString(null));
    }

    // 2. Тестирование массивов
    @Test
    void testArrays() throws Exception {
        int[] intArray = {1, 2, 3};
        assertEquals("[1,2,3]", JsonParser.parseToString(intArray));

        String[] strArray = {"a", "b"};
        assertEquals("[\"a\",\"b\"]", JsonParser.parseToString(strArray));

        Object[] objArray = {1, "test", true};
        assertEquals("[1,\"test\",true]", JsonParser.parseToString(objArray));
    }

    // 3. Тестирование коллекций
    @Test
    void testCollections() throws Exception {
        List<String> list = Arrays.asList("a", "b");
        assertEquals("[\"a\",\"b\"]", JsonParser.parseToString(list));

        Set<Integer> set = new HashSet<>(Arrays.asList(1, 2));
        String setJson = JsonParser.parseToString(set);
        assertTrue(setJson.equals("[1,2]") || setJson.equals("[2,1]"));
    }

    // 4. Тестирование Map
    @Test
    void testMaps() throws Exception {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("key1", 1);
        map.put("key2", "value");
        String expected1 = "{\"key1\":1,\"key2\":\"value\"}";
        String expected2 = "{\"key2\":\"value\",\"key1\":1}";
        String actual = JsonParser.parseToString(map);
        assertTrue(actual.equals(expected1) || actual.equals(expected2));
    }

    // 5. Тестирование объектов с наследованием
    @Test
    void testInheritance() throws Exception {
        Child child = new Child();
        String json = JsonParser.parseToString(child);

        assertTrue(json.contains("\"parentField\":\"parent_value\""));
        assertTrue(json.contains("\"childField\":\"child_value\""));
        assertTrue(json.startsWith("{") && json.endsWith("}"));
    }

    // 7. Тестирование экранирования спецсимволов
    @Test
    void testStringEscaping() throws Exception {
        String test = "Line1\nLine2\tTab\"Quote\\Backslash";
        String expected = "\"Line1\\nLine2\\tTab\\\"Quote\\\\Backslash\"";
        assertEquals(expected, JsonParser.parseToString(test));
    }

    // 8. Тестирование enum
    @Test
    void testEnum() throws Exception {
        enum TestEnum {VALUE1, VALUE2}
        assertEquals("\"VALUE1\"", JsonParser.parseToString(TestEnum.VALUE1));
    }

    // Базовый класс-родитель
    static class ParentClass {
        private String parentField;
        private int parentNumber;

        public String getParentField() {
            return parentField;
        }

        public int getParentNumber() {
            return parentNumber;
        }
    }

    // Класс-наследник
    static class ChildClass extends ParentClass {
        private String childField;
        private List<String> childList;

        public String getChildField() {
            return childField;
        }

        public List<String> getChildList() {
            return childList;
        }
    }

    // Класс с глубокой иерархией наследования
    static class GrandparentClass {
        private boolean grandparentFlag;

        public boolean isGrandparentFlag() {
            return grandparentFlag;
        }
    }

    static class ParentWithGrandparent extends GrandparentClass {
        private double parentValue;

        public double getParentValue() {
            return parentValue;
        }
    }

    static class ChildWithDeepInheritance extends ParentWithGrandparent {
        private String childName;

        public String getChildName() {
            return childName;
        }
    }

    // 1. Тест простого наследования
    @Test
    void testSimpleInheritanceParsing() throws Exception {
        String json = "{" +
                "\"parentField\":\"parentValue\"," +
                "\"parentNumber\":42," +
                "\"childField\":\"childValue\"," +
                "\"childList\":[\"item1\",\"item2\"]" +
                "}";

        ChildClass result = JsonParser.parseStringToClass(json, ChildClass.class);

        // Проверка полей родителя
        assertEquals("parentValue", result.getParentField());
        assertEquals(42, result.getParentNumber());

        // Проверка полей наследника
        assertEquals("childValue", result.getChildField());
        assertEquals(Arrays.asList("item1", "item2"), result.getChildList());
    }

    // 2. Тест глубокой иерархии наследования
    @Test
    void testDeepInheritanceParsing() throws Exception {
        String json = "{" +
                "\"grandparentFlag\":true," +
                "\"parentValue\":3.14," +
                "\"childName\":\"TestName\"" +
                "}";

        ChildWithDeepInheritance result = JsonParser.parseStringToClass(json, ChildWithDeepInheritance.class);

        // Проверка полей через несколько уровней наследования
        assertTrue(result.isGrandparentFlag());
        assertEquals(3.14, result.getParentValue(), 0.001);
        assertEquals("TestName", result.getChildName());
    }

    // 3. Тест с частичными данными (не все поля заполнены)
    @Test
    void testPartialDataParsing() throws Exception {
        String json = "{\"parentNumber\":100,\"childField\":\"partial\"}";

        ChildClass result = JsonParser.parseStringToClass(json, ChildClass.class);

        assertEquals(100, result.getParentNumber());
        assertEquals("partial", result.getChildField());
        assertNull(result.getParentField()); // Не было в JSON
        assertNull(result.getChildList());   // Не было в JSON
    }

    // 4. Тест с дополнительными полями в JSON (которых нет в классе)
    @Test
    void testExtraFieldsInJson() throws Exception {
        String json = "{" +
                "\"parentField\":\"value\"," +
                "\"childField\":\"child\"," +
                "\"unknownField\":\"shouldBeIgnored\"" +
                "}";

        assertThrows(MappingException.class, () -> {
            JsonParser.parseStringToClass(json, ChildClass.class);
        });
        // Поле unknownField должно быть проигнорировано без ошибок
    }

    // 5. Тест обработки null-значений
    @Test
    void testNullValuesHandling() throws Exception {
        String json = "{" +
                "\"parentField\":null," +
                "\"childField\":\"notNull\"," +
                "\"childList\":null" +
                "}";

        ChildClass result = JsonParser.parseStringToClass(json, ChildClass.class);

        assertNull(result.getParentField());
        assertEquals("notNull", result.getChildField());
        assertNull(result.getChildList());
    }

    // 6. Тест ошибки при несовместимых типах
    @Test
    void testTypeMismatchError() {
        String json = "{\"parentNumber\":\"notANumber\"}";

        assertThrows(MappingException.class, () -> {
            JsonParser.parseStringToClass(json, ChildClass.class);
        });
    }

    // 7. Тест вложенных объектов в наследовании
    @Test
    void testNestedObjectsInInheritance() throws Exception {
        class Nested {
            String nestedField;
        }
        class Parent {
            Nested parentNested;
        }
        class Child extends Parent {
            String childField;
        }

        String json = "{" +
                "\"parentNested\":{\"nestedField\":\"nestedValue\"}," +
                "\"childField\":\"childValue\"" +
                "}";

        Child result = JsonParser.parseStringToClass(json, Child.class);

        assertEquals("nestedValue", result.parentNested.nestedField);
        assertEquals("childValue", result.childField);
    }

    @Test
    void testFromAnton() throws JsonException {
        Cat cat = new Cat(new Tail(),
                new Paw(true, new Claw(), new Claw(), new Claw(), new Claw()),
                new Paw(true, new Claw(), new Claw(), new Claw(), new Claw()),
                new Paw(false, new Claw(), new Claw(), new Claw(), new Claw()),
                new Paw(false, new Claw(), new Claw(), new Claw(), new Claw())
        );
        String result = JsonParser.parseToString(cat);
        var newCat = JsonParser.parseStringToClass(result, cat.getClass());
        assertNotNull(newCat);

        // Проверяем структуру объекта
        assertEquals(5, newCat.parts.size()); // 1 хвост + 4 лапы

        // Исправлено: get(0) вместо getFirst()
        assertInstanceOf(Tail.class, newCat.parts.get(0));
        Tail deserializedTail = (Tail) newCat.parts.get(0);
        assertEquals("Pretty fluffy tail", deserializedTail.getName());
        assertEquals(10.2, deserializedTail.lenght);

        // Проверяем лапы
        for (int i = 1; i < newCat.parts.size(); i++) {
            assertInstanceOf(Paw.class, newCat.parts.get(i));
            Paw paw = (Paw) newCat.parts.get(i);

            boolean expectedFront = i <= 2;
            assertEquals(expectedFront, paw.isFront);
            assertEquals(expectedFront ? "Front paw" : "Back paw", paw.getName());

            assertEquals(4, paw.claws.size());
            for (Claw claw : paw.claws) {
                assertInstanceOf(Claw.class, claw);
                assertEquals("Sharp claw", claw.getName());
            }
        }
    }
}

