package ru.spbstu.jsonparser;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class JsonParserTest {

    @Test
    void testFromJsonToMapSimple() {
        String json = "{\"name\":\"Mark\",\"age\":22}";
        Map<String, Object> map = JsonParser.fromJsonToMap(json);
        assertEquals("Mark", map.get("name"));
        assertEquals(22, map.get("age"));
    }

    @Test
    void testFromJsonToMapNested() {
        String json = "{\"person\":{\"name\":\"Mark\",\"age\":22}}";
        Map<String, Object> map = JsonParser.fromJsonToMap(json);
        assertInstanceOf(Map.class, map.get("person"));
        Map<String, Object> person = (Map<String, Object>) map.get("person");
        assertEquals("Mark", person.get("name"));
        assertEquals(22, person.get("age"));
    }

    @Test
    void testFromJsonToMapWithArray() {
        String json = "{\"skills\":[\"Teamwork\",\"Leadership\"]}";
        Map<String, Object> map = JsonParser.fromJsonToMap(json);
        assertInstanceOf(List.class, map.get("skills"));
        List<String> skills = (List<String>) map.get("skills");
        assertEquals(Arrays.asList("Teamwork", "Leadership"), skills);
    }

    @Test
    void testFromJsonToClassSimple() {
        String json = "{\"name\":\"Mark\",\"age\":22,\"isStudent\":false}";
        User user = JsonParser.fromJsonToClass(json, User.class);
        assertEquals("Mark", user.getName());
        assertEquals(22, user.getAge());
        assertFalse(user.getIsStudent());
    }

    @Test
    void testFromJsonToClassWithArray() {
        String json = "{\"name\":\"Mark\",\"hobbies\":[\"reading\",\"coding\"]}";
        User user = JsonParser.fromJsonToClass(json, User.class);
        assertArrayEquals(new String[]{"reading", "coding"}, user.getHobbies());
    }

    @Test
    void testFromJsonToClassWithCollection() {
        String json = "{\"name\":\"Mark\",\"skills\":[\"Teamwork\",\"Leadership\"]}";
        User user = JsonParser.fromJsonToClass(json, User.class);
        assertEquals(new HashSet<>(Arrays.asList("Teamwork", "Leadership")), user.getSkills());
    }

    @Test
    void testFromObjToJsonSimple() {
        User user = new User();
        user.setName("Mark");
        user.setAge(22);
        String json = JsonParser.fromObjToJson(user);
        assertTrue(json.contains("\"name\":\"Mark\""));
        assertTrue(json.contains("\"age\":22"));
    }

    @Test
    void testFromObjToJsonWithArray() {
        User user = new User();
        user.setHobbies(new String[]{"reading", "coding"});
        String json = JsonParser.fromObjToJson(user);
        assertTrue(json.contains("\"hobbies\":[\"reading\",\"coding\"]"));
    }

    @Test
    void testFromObjToJsonWithCollection() {
        User user = new User();
        user.setSkills(new HashSet<>(Arrays.asList("Teamwork", "Leadership")));
        String json = JsonParser.fromObjToJson(user);
        assertTrue(json.contains("\"skills\":[\"Leadership\",\"Teamwork\"]"));
    }

    @Test
    void testComplexJson() {
        String json = "{\"name\":\"Mark\",\"age\":22,\"isStudent\":false,\"hobbies\":[\"reading\",\"coding\"],\"skills\":[\"Teamwork\",\"Leadership\"],\"address\":{\"city\":\"Saint Petersburg\",\"region number\":\"78\"}}";
        Map<String, Object> map = JsonParser.fromJsonToMap(json);
        assertEquals("Mark", map.get("name"));
        assertEquals(22, map.get("age"));
        assertFalse((Boolean) map.get("isStudent"));
        assertEquals(Arrays.asList("reading", "coding"), map.get("hobbies"));
        assertEquals(Arrays.asList("Teamwork", "Leadership"), map.get("skills"));
        assertInstanceOf(Map.class, map.get("address"));
        Map<String, Object> address = (Map<String, Object>) map.get("address");
        assertEquals("Saint Petersburg", address.get("city"));
        assertEquals("78", address.get("region number"));
    }

    @Test
    void testInheritedFieldsSerialization() {
        ExtendedUser extendedUser = new ExtendedUser();
        extendedUser.setName("Mark");
        extendedUser.setAge(22);
        extendedUser.setDepartment("IT");

        String json = JsonParser.fromObjToJson(extendedUser);

        assertTrue(json.contains("\"name\":\"Mark\""));
        assertTrue(json.contains("\"age\":22"));
        assertTrue(json.contains("\"department\":\"IT\""));
    }

    @Test
    void testInheritedFieldsDeserialization() {
        String json = "{\"name\":\"Mark\",\"age\":22,\"department\":\"IT\"}";

        ExtendedUser extendedUser = JsonParser.fromJsonToClass(json, ExtendedUser.class);

        assertEquals("Mark", extendedUser.getName());
        assertEquals(22, extendedUser.getAge());
        assertEquals("IT", extendedUser.getDepartment());
    }


    @Test
    void scaryCatSerialization() {
        Cat cat = new Cat(new Tail(),
                new Paw(true, new Claw(), new Claw(), new Claw(), new Claw()),
                new Paw(true, new Claw(), new Claw(), new Claw(), new Claw()),
                new Paw(false, new Claw(), new Claw(), new Claw(), new Claw()),
                new Paw(false, new Claw(), new Claw(), new Claw(), new Claw())
        );

        // Сериализуем кота в JSON
        String json = JsonParser.fromObjToJson(cat);

        // Десериализуем обратно в Map для проверки
        Map<String, Object> map = JsonParser.fromJsonToMap(json);

        // Проверяем наличие всех частей
        assertTrue(map.containsKey("parts"));
        assertInstanceOf(List.class, map.get("parts"));
        List<Map<String, Object>> parts = (List<Map<String, Object>>) map.get("parts");

        // Должно быть 5 частей (1 хвост + 4 лапы)
        assertEquals(5, parts.size());

        // Проверяем хвост
        Map<String, Object> tail = parts.getFirst();
        assertEquals("Pretty fluffy tail", tail.get("name"));
        assertEquals(10.2, tail.get("lenght"));

        // Проверяем лапы
        for (int i = 1; i < parts.size(); i++) {
            Map<String, Object> paw = parts.get(i);
            // Проверяем базовые поля лапы
            assertEquals("paw", paw.get("name"));
            assertNotNull(paw.get("isFront"));

            // Проверяем когти
            assertTrue(paw.containsKey("claws"));
            assertInstanceOf(List.class, paw.get("claws"));
            List<Map<String, Object>> claws = (List<Map<String, Object>>) paw.get("claws");
            assertEquals(4, claws.size());

            for (Map<String, Object> claw : claws) {
                assertEquals("claw", claw.get("name"));
            }
        }
    }


    @Test
    void testCatSerializationDeserialization() {
        Cat originalCat = new Cat(
                new Tail(),
                new Paw(true, new Claw(), new Claw(), new Claw(), new Claw()),
                new Paw(true, new Claw(), new Claw(), new Claw(), new Claw()),
                new Paw(false, new Claw(), new Claw(), new Claw(), new Claw()),
                new Paw(false, new Claw(), new Claw(), new Claw(), new Claw())
        );

        // Сериализуем в JSON
        String json = JsonParser.fromObjToJson(originalCat);
        assertEquals("{\"parts\":[{\"lenght\":10.2,\"name\":\"Pretty fluffy tail\"},{\"isFront\":true,\"claws\":[{\"name\":\"claw\"},{\"name\":\"claw\"},{\"name\":\"claw\"},{\"name\":\"claw\"}],\"name\":\"paw\"},{\"isFront\":true,\"claws\":[{\"name\":\"claw\"},{\"name\":\"claw\"},{\"name\":\"claw\"},{\"name\":\"claw\"}],\"name\":\"paw\"},{\"isFront\":false,\"claws\":[{\"name\":\"claw\"},{\"name\":\"claw\"},{\"name\":\"claw\"},{\"name\":\"claw\"}],\"name\":\"paw\"},{\"isFront\":false,\"claws\":[{\"name\":\"claw\"},{\"name\":\"claw\"},{\"name\":\"claw\"},{\"name\":\"claw\"}],\"name\":\"paw\"}]}", json);

        // Десериализуем обратно в объект Cat
        Cat deserializedCat = JsonParser.fromJsonToClass(json, Cat.class);
        assertNotNull(deserializedCat);

        // Проверяем структуру объекта
        assertEquals(5, deserializedCat.parts.size()); // 1 хвост + 4 лапы

        // Проверяем хвост
        assertInstanceOf(Tail.class, deserializedCat.parts.getFirst());
        Tail deserializedTail = (Tail) deserializedCat.parts.getFirst();
        assertEquals("Pretty fluffy tail", deserializedTail.getName());
        assertEquals(10.2, deserializedTail.lenght);

        // Проверяем лапы
        for (int i = 1; i < deserializedCat.parts.size(); i++) {
            assertInstanceOf(Paw.class, deserializedCat.parts.get(i));
            Paw paw = (Paw) deserializedCat.parts.get(i);

            // Проверяем положение лапы (первые две - передние)
            boolean expectedFront = i <= 2;
            assertEquals(expectedFront, paw.isFront);

            // Проверяем имя через getName()
            assertEquals(expectedFront ? "Front paw" : "Back paw", paw.getName());

            // Проверяем когти
            assertEquals(4, paw.claws.size());
            for (Claw claw : paw.claws) {
                assertInstanceOf(Claw.class, claw);
                assertEquals("Sharp claw", claw.getName());
            }
        }

        // Сериализуем повторно и сравниваем JSON
        String reSerializedJson = JsonParser.fromObjToJson(deserializedCat);
        assertEquals(json, reSerializedJson);
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

        public static class PawDeserializer implements JsonParser.JsonDeserializer<Paw> {
            @Override
            public Paw deserialize(Map<String, Object> map) {
                boolean isFront = (Boolean) map.get("isFront");
                List<Claw> claws = ((List<Map<String, Object>>) map.get("claws")).stream()
                        .map(clawMap -> JsonParser.convertMapToObject(clawMap, Claw.class))
                        .toList();
                return new Paw(isFront, claws.toArray(new Claw[0]));
            }
        }


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

    @JsonDeserialize(using = Cat.CatDeserializer.class)
    private static class Cat {

        public static class CatDeserializer implements JsonParser.JsonDeserializer<Cat> {
            @Override
            public Cat deserialize(Map<String, Object> map) {
                List<Map<String, Object>> parts = (List<Map<String, Object>>) map.get("parts");
                Tail tail = null;
                List<Paw> paws = new ArrayList<>();

                for (Map<String, Object> part : parts) {
                    if (part.containsKey("lenght")) {
                        tail = JsonParser.convertMapToObject(part, Tail.class);
                    } else if (part.containsKey("isFront")) {
                        paws.add(JsonParser.convertMapToObject(part, Paw.class));
                    }
                }
                return new Cat(tail, paws.toArray(new Paw[0]));
            }
        }


        List<AnimalPart> parts = new LinkedList<>();
        public Cat(Tail tail, Paw... paws) {
            super();
            parts.add(tail);
            parts.addAll(Arrays.asList(paws));
        }
    }

}