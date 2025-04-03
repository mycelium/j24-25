package ru.lab.parser;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для классов с наследованием
 */

public class JSONParserInheritanceTest {

    // Базовый класс для тестирования
    static class ParentClass {
        private String parentField1;
        protected int parentField2;
        public boolean parentField3;

        ParentClass(String parentField1, int parentField2, boolean parentField3){
            this.parentField1 = parentField1;
            this.parentField2 = parentField2;
            this.parentField3 = parentField3;
        }
    }

    // Дочерний класс для тестирования
    static class ChildClass extends ParentClass {
        private String childField1;
        protected double childField2;
        public ArrayList<String> childField3;

        ChildClass(String childField1, double childField2, ArrayList<String> childField3,
                   String parentField1, int parentField2, boolean parentField3) {
            super(parentField1, parentField2, parentField3);
            this.childField1 = childField1;
            this.childField2 = childField2;
            this.childField3 = childField3;
        }
    }

    // Класс с глубоким наследованием
    static class GrandParentClass {
        private String grandParentField;
        GrandParentClass(String grandParentField){
            this.grandParentField = grandParentField;
        }
    }

    static class ParentClass2 extends GrandParentClass {
        protected int parentField;

        ParentClass2(int parentField, String grandParentField) {
            super(grandParentField);
            this.parentField = parentField;
        }
    }

    static class ChildClass2 extends ParentClass2 {
        public boolean childField;

        ChildClass2(boolean childField, int parentField, String grandParentField) {
            super(parentField, grandParentField);
            this.childField = childField;
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
        public Cat(@ParamName("tail") Tail tail, Paw... paws) {
            super();
            parts.add(tail);
            parts.addAll(Arrays.asList(paws));
        }
    }


    @Test
    void testSimpleInheritanceSerialization() {
        ChildClass child = new ChildClass("childValue1", 3.14, new ArrayList<>(List.of("a", "b", "c")), "parentValue1", 42, true);
        String json = JSONParser.convertEntityToJSON(child);

        assertTrue(json.contains("\"parentField1\":\"parentValue1\""));
        assertTrue(json.contains("\"parentField2\":42"));
        assertTrue(json.contains("\"parentField3\":true"));
        assertTrue(json.contains("\"childField1\":\"childValue1\""));
        assertTrue(json.contains("\"childField2\":3.14"));
        assertTrue(json.contains("\"childField3\":[\"a\",\"b\",\"c\"]"));
    }

    @Test
    void testSimpleInheritanceDeserialization() {
        String json = "{" +
                "\"parentField1\":\"newParentValue1\"," +
                "\"parentField2\":99," +
                "\"parentField3\":false," +
                "\"childField1\":\"newChildValue1\"," +
                "\"childField2\":2.71," +
                "\"childField3\":[\"x\",\"y\",\"z\"]" +
                "}";

        ChildClass child = JSONParser.readJsonToEntity(json, ChildClass.class);

        assertEquals("newParentValue1", getFieldValue(child, "parentField1"));
        assertEquals(99, getFieldValue(child, "parentField2"));
        assertEquals(false, getFieldValue(child, "parentField3"));
        assertEquals("newChildValue1", getFieldValue(child, "childField1"));
        assertEquals(2.71, getFieldValue(child, "childField2"));
        assertEquals(List.of("x", "y", "z"), getFieldValue(child, "childField3"));
    }

    @Test
    void testDeepInheritanceSerialization() {
        ChildClass2 child = new ChildClass2(false, 100, "grandParentValue");
        String json = JSONParser.convertEntityToJSON(child);

        assertTrue(json.contains("\"grandParentField\":\"grandParentValue\""));
        assertTrue(json.contains("\"parentField\":100"));
        assertTrue(json.contains("\"childField\":false"));
    }

    @Test
    void testDeepInheritanceDeserialization() {
        String json = "{" +
                "\"grandParentField\":\"newGrandParentValue\"," +
                "\"parentField\":200," +
                "\"childField\":true" +
                "}";

        ChildClass2 child = JSONParser.readJsonToEntity(json, ChildClass2.class);

        assertEquals("newGrandParentValue", getFieldValue(child, "grandParentField"));
        assertEquals(200, getFieldValue(child, "parentField"));
        assertEquals(true, getFieldValue(child, "childField"));
    }

    // Вспомогательный метод для получения значения поля через рефлексию
    private static Object getFieldValue(Object obj, String fieldName) {
        try {
            Class<?> clazz = obj.getClass();
            while (clazz != null) {
                try {
                    Field field = clazz.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    return field.get(obj);
                } catch (NoSuchFieldException e) {
                    clazz = clazz.getSuperclass();
                }
            }
            throw new NoSuchFieldException(fieldName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void parseCats(){
        String json = "{\"parts\":[{\"lenght\":10.2,\"name\":\"Pretty fluffy tail\"},{\"claws\":[{\"name\":\"claw\"}," +
                "{\"name\":\"claw\"},{\"name\":\"claw\"},{\"name\":\"claw\"}],\"isFront\":true,\"name\":\"paw\"}," +
                "{\"claws\":[{\"name\":\"claw\"},{\"name\":\"claw\"},{\"name\":\"claw\"},{\"name\":\"claw\"}]," +
                "\"isFront\":true,\"name\":\"paw\"},{\"claws\":[{\"name\":\"claw\"},{\"name\":\"claw\"}," +
                "{\"name\":\"claw\"},{\"name\":\"claw\"}],\"isFront\":false,\"name\":\"paw\"},{\"claws\":[" +
                "{\"name\":\"claw\"},{\"name\":\"claw\"},{\"name\":\"claw\"},{\"name\":\"claw\"}]," +
                "\"isFront\":false,\"name\":\"paw\"}]}";

        JSONParser.addDeserializer((map) -> {
            List<HashMap<String, Object>> parts = (List) map.get("parts");
            Tail tail = null;
            List<Paw> paws = new ArrayList<>();
            for(var part: parts){
                try{
                    tail = JSONParser.fillClazzWithMap(part, Tail.class);
                } catch (Exception e){}
                try{
                    paws.addLast(JSONParser.fillClazzWithMap(part, Paw.class));
                } catch (Exception e){}
            }
            return new Cat(tail, paws.toArray(Paw[]::new));
        }, Cat.class);

        JSONParser.addDeserializer((map) -> {
            String name = (String) map.get("name");
            Boolean isFront = (Boolean) map.get("isFront");
            List<Claw> claws = ((List<Map<String, Object>>) map.get("claws")).stream().map((claw) ->
                    JSONParser.fillClazzWithMap(claw, Claw.class)
            ).toList();
            return new Paw(isFront, claws.toArray(Claw[]::new));
        }, Paw.class);

        Cat cat = JSONParser.readJsonToEntity(json, Cat.class);

        // Проверки
        assertNotNull(cat);
        assertNotNull(cat.parts);
        assertEquals(5, cat.parts.size()); // 1 хвост + 4 лапы

        // Проверка хвоста
        AnimalPart tail = cat.parts.get(0);
        assertTrue(tail instanceof Tail);
        assertEquals("Pretty fluffy tail", tail.getName());
        assertEquals(10.2, ((Tail) tail).lenght);

        // Проверка лап
        for (int i = 1; i < 5; i++) {
            AnimalPart part = cat.parts.get(i);
            assertTrue(part instanceof Paw);
            Paw paw = (Paw) part;

            if (i < 3) {
                assertTrue(paw.isFront); // Первые две лапы - передние
                assertEquals("Front paw", paw.getName());
            } else {
                assertFalse(paw.isFront); // Последние две - задние
                assertEquals("Back paw", paw.getName());
            }

            assertNotNull(paw.claws);
            assertEquals(4, paw.claws.size()); // По 4 когтя на каждой лапе
            for (Claw claw : paw.claws) {
                assertEquals("Sharp claw", claw.getName());
            }
        }
    }
}