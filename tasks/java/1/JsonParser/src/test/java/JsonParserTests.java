import ru.spbstu.hsai.jsparser.JsonSerializer;
import ru.spbstu.hsai.jsparser.JsonDeserializer;
import org.junit.jupiter.api.Test;
import ru.spbstu.hsai.jsparser.custom.JsonDeserialize;


import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class JsonParserTests {

    @Test
    public void testSimpleJsonToMap() {
        String json = "{\"name\":\"Alice\",\"age\":30,\"isStudent\":false}";
        Map<String, Object> map = JsonDeserializer.jsonToMap(json);

        assertEquals("Alice", map.get("\"name\""));
        assertEquals(30L, map.get("\"age\""));
        assertEquals(false, map.get("\"isStudent\""));
    }

    @Test
    public void testNestedJsonToMap() {
        String json = "{\"user\":{\"name\":\"Bob\",\"age\":25}}";
        Map<String, Object> map = JsonDeserializer.jsonToMap(json);

        Map<String, Object> userMap = (Map<String, Object>) map.get("\"user\"");
        assertEquals("Bob", userMap.get("\"name\""));
        assertEquals(25L, userMap.get("\"age\""));
    }

    @Test
    public void testNestedJsonWithArray() {
        String json = "{\"data\":{\"numbers\":[1,2,3],\"flag\":true}}";
        Map<String, Object> map = JsonDeserializer.jsonToMap(json);

        Map<String, Object> dataMap = (Map<String, Object>) map.get("\"data\"");
        List<Object> numbers = (List<Object>) dataMap.get("\"numbers\"");
        assertEquals(List.of(1L, 2L, 3L), numbers);
        assertEquals(true, dataMap.get("\"flag\""));
    }


    @Test
    public void testDeserializationToSimpleClass() {
        String json = "{\"name\":\"Charlie\",\"age\":40,\"active\":true}";
        Person person = JsonDeserializer.jsonToClass(json, Person.class);

        assertEquals("Charlie", person.name);
        assertEquals(40, person.age);
        assertTrue(person.active);
    }

    @Test
    public void testDeserializationWithCollectionMapArray() {
        String json = """
            {
                "numbers": [10.2, 2.13, 5.61],
                "metadata": {"key1":"value1","key2":"value2"},
                "tags": ["alpha", "beta"]
            }
            """;

        DataContainer data = JsonDeserializer.jsonToClass(json, DataContainer.class);
        assertEquals("value1", data.metadata.get("\"key1\""));
        assertArrayEquals(new String[]{"alpha", "beta"}, data.tags);
        assertIterableEquals(List.of(10.2, 2.13, 5.61), data.numbers);
    }

    @Test
    public void testSerializationDeserialization() {
        Book original = new Book("My Book", 123, List.of("Author1", "Author2"));

        String json = JsonSerializer.classToJson(original);
        Book copy = JsonDeserializer.jsonToClass(json, Book.class);

        assertEquals(original.title, copy.title);
        assertEquals(original.pages, copy.pages);
        assertEquals(original.authors, copy.authors);
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

    @JsonDeserialize(using = Cat.CatDeserializer.class)
    private static class Cat {
        List<AnimalPart> parts = new LinkedList<>();
        public Cat(Tail tail, Paw... paws) {
            super();
            parts.add(tail);
            parts.addAll(Arrays.asList(paws));
        }

        public static class CatDeserializer {
            public static Cat deserialize(Map<String, Object> jsonMap) {
                try {
                    List<Map<String, Object>> partsData = (List<Map<String, Object>>) jsonMap.get("\"parts\"");

                    Map<String, Object> tailMap = (Map<String, Object>) partsData.getFirst();
                    Tail tail = new Tail();
                    if (tailMap != null) {
                        if (tailMap.containsKey("\"lenght\"")) {
                            tail.lenght = (Double) tailMap.get("\"lenght\"");
                        }
                        if (tailMap.containsKey("\"name\"")) {
                            tail.name = (String) tailMap.get("\"name\"");
                        }
                    }

                    List<Map<String, Object>> pawsList = partsData.subList(1, partsData.size());
                    Paw[] paws = new Paw[pawsList != null ? pawsList.size() : 0];

                    for (int i = 0; pawsList != null && i < pawsList.size(); i++) {
                        Map<String, Object> pawMap = pawsList.get(i);
                        boolean isFront = pawMap.containsKey("\"isFront\"") &&
                                Boolean.TRUE.equals(pawMap.get("\"isFront\""));

                        List<Map<String, Object>> clawsList = (List<Map<String, Object>>) pawMap.get("\"claws\"");
                        Claw[] claws = new Claw[clawsList != null ? clawsList.size() : 0];

                        for (int j = 0; clawsList != null && j < clawsList.size(); j++) {
                            claws[j] = new Claw();
                            Map<String, Object> clawMap = clawsList.get(j);
                            if (clawMap.containsKey("\"name\"")) {
                                claws[j].name = (String) clawMap.get("\"name\"");
                            }
                        }

                        paws[i] = new Paw(isFront, claws);
                    }
                    return new Cat(tail, paws);

                } catch (Exception e) {
                    throw new RuntimeException("Failed to deserialize Cat");
                }
            }
        }
    }

    Cat cat = new Cat(
            new Tail(),
            new Paw(true, new Claw(), new Claw(), new Claw(), new Claw()),
            new Paw(true, new Claw(), new Claw(), new Claw(), new Claw()),
            new Paw(false, new Claw(), new Claw(), new Claw(), new Claw()),
            new Paw(false, new Claw(), new Claw(), new Claw(), new Claw())
    );

    @Test
    void testCatSerializationToMap() {

        String json = JsonSerializer.classToJson(cat);

        Map<String, Object> map = JsonDeserializer.jsonToMap(json);

        assertTrue(map.containsKey("\"parts\""));
        assertInstanceOf(List.class, map.get("\"parts\""));
        List<Map<String, Object>> parts = (List<Map<String, Object>>) map.get("\"parts\"");

        assertEquals(5, parts.size());

        Map<String, Object> tail = parts.getFirst();
        assertEquals("Pretty fluffy tail", tail.get("\"name\""));
        assertEquals(10.2, tail.get("\"lenght\""));

        for (int i = 1; i < parts.size(); i++) {
            Map<String, Object> paw = parts.get(i);

            assertEquals("paw", paw.get("\"name\""));
            assertNotNull(paw.get("\"isFront\""));

            assertTrue(paw.containsKey("\"claws\""));
            assertInstanceOf(List.class, paw.get("\"claws\""));
            List<Map<String, Object>> claws = (List<Map<String, Object>>) paw.get("\"claws\"");
            assertEquals(4, claws.size());

            for (Map<String, Object> claw : claws) {
                assertEquals("claw", claw.get("\"name\""));
            }
        }
    }
    @Test
    void testCatSerializationToCat() {
        String json = JsonSerializer.classToJson(cat);
        assertNotNull(json);

        String expectedJson = "{\"parts\":[{\"lenght\":10.2,\"name\":\"Pretty fluffy tail\"},{\"isFront\":true,\"claws\":[{\"name\":\"claw\"},{\"name\":\"claw\"},{\"name\":\"claw\"},{\"name\":\"claw\"}],\"name\":\"paw\"},{\"isFront\":true,\"claws\":[{\"name\":\"claw\"},{\"name\":\"claw\"},{\"name\":\"claw\"},{\"name\":\"claw\"}],\"name\":\"paw\"},{\"isFront\":false,\"claws\":[{\"name\":\"claw\"},{\"name\":\"claw\"},{\"name\":\"claw\"},{\"name\":\"claw\"}],\"name\":\"paw\"},{\"isFront\":false,\"claws\":[{\"name\":\"claw\"},{\"name\":\"claw\"},{\"name\":\"claw\"},{\"name\":\"claw\"}],\"name\":\"paw\"}]}";

        assertEquals(expectedJson, json);

        Cat deserializedCat = JsonDeserializer.jsonToClass(json, Cat.class);
        assertNotNull(deserializedCat);

        assertEquals(5, deserializedCat.parts.size());

        AnimalPart tail = deserializedCat.parts.getFirst();
        assertInstanceOf(Tail.class, tail);
        Tail deserializedTail = (Tail) tail;
        assertEquals("Pretty fluffy tail", deserializedTail.getName());
        assertEquals(10.2, deserializedTail.lenght);

        for (int i = 1; i < deserializedCat.parts.size(); i++) {
            AnimalPart part = deserializedCat.parts.get(i);
            assertInstanceOf(Paw.class, part);
            Paw paw = (Paw) part;

            if (i <= 2) {
                assertTrue(paw.isFront);
            } else {
                assertFalse(paw.isFront);
            }

            assertEquals(i<=2 ? "Front paw" : "Back paw", paw.getName());

            assertNotNull(paw.claws);
            assertEquals(4, paw.claws.size());
            for (Claw claw : paw.claws) {
                assertEquals("Sharp claw", claw.getName());
            }
        }

        String reSerializedJson = JsonSerializer.classToJson(deserializedCat);
        assertEquals(json, reSerializedJson);
    }

}

