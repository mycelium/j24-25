package org.jsonParserAyzek;

import static org.jsonParserAyzek.Main_JSON_Parser.JSONIntoMAP;
import static org.jsonParserAyzek.Main_JSON_Parser.JSONToObj;
import static org.jsonParserAyzek.Main_JSON_Parser.toJSON;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;

class Main_JSON_ParserTest {

    @Test
    void testParseSimpleJSONIntoMap() {
        String json = "{\"mark\":\"Volvo\",\"model\":\"X90\",\"v\":12,\"crashed_Painted\":false}";

        Map<String, Object> resultMap = JSONIntoMAP(json);
        assertNotNull(resultMap);
        assertEquals("Volvo", resultMap.get("mark"));
        assertEquals("X90", resultMap.get("model"));
        assertEquals(12, resultMap.get("v"));
        assertEquals(false, resultMap.get("crashed_Painted"));
    }

    @Test
    void testDeserializeFutureCar() throws Exception {
        String json = "{\"mark\":\"Volvo\",\"model\":\"X90\",\"v\":12,\"crashed_Painted\":false}";

        class FutureCar {
            public String mark;
            public String model;
            public int v;
            public boolean crashed_Painted;

            public FutureCar() {
            }

            public FutureCar(String mark, String model, int v, boolean crashed) {
                this.mark = mark;
                this.model = model;
                this.v = v;
                this.crashed_Painted = crashed;
            }
        }

        FutureCar futureCar = JSONToObj(json, FutureCar.class);
        assertNotNull(futureCar);
        assertEquals("Volvo", futureCar.mark);
        assertEquals("X90", futureCar.model);
        assertEquals(12, futureCar.v);
        assertFalse(futureCar.crashed_Painted);
    }

    @Test
    void testSerializeFutureCar() throws Exception {
        class FutureCar {
            public String mark;
            public String model;
            public int v;
            public boolean crashed_Painted;

            public FutureCar(String mark, String model, int v, boolean crashed) {
                this.mark = mark;
                this.model = model;
                this.v = v;
                this.crashed_Painted = crashed;
            }
        }

        FutureCar futureCar = new FutureCar("Volvo", "X90", 12, false);
        String json = toJSON(futureCar);

        assertNotNull(json);
        assertTrue(json.contains("\"mark\":\"Volvo\""));
        assertTrue(json.contains("\"model\":\"X90\""));
        assertTrue(json.contains("\"v\":12"));
        assertTrue(json.contains("\"crashed_Painted\":false"));
    }

    @Test
    void testCatSerializationAndDeserialization() throws Exception {
        Main_JSON_Parser.Cat catOriginal = new Main_JSON_Parser.Cat(
                new Main_JSON_Parser.Tail(),
                new Main_JSON_Parser.Paw(true,
                        new Main_JSON_Parser.Claw(), new Main_JSON_Parser.Claw(), new Main_JSON_Parser.Claw(), new Main_JSON_Parser.Claw()),
                new Main_JSON_Parser.Paw(true,
                        new Main_JSON_Parser.Claw(), new Main_JSON_Parser.Claw(), new Main_JSON_Parser.Claw(), new Main_JSON_Parser.Claw()),
                new Main_JSON_Parser.Paw(false,
                        new Main_JSON_Parser.Claw(), new Main_JSON_Parser.Claw(), new Main_JSON_Parser.Claw(), new Main_JSON_Parser.Claw()),
                new Main_JSON_Parser.Paw(false,
                        new Main_JSON_Parser.Claw(), new Main_JSON_Parser.Claw(), new Main_JSON_Parser.Claw(), new Main_JSON_Parser.Claw())
        );

        String catJson = toJSON(catOriginal);
        System.out.println("catJson = " + catJson);
        assertTrue(catJson.contains("\"lenght\":10.2"),
                "Должно быть поле '\"lenght\":10.2' (опечатка 'lenght')");
        assertTrue(catJson.contains("\"Pretty fluffy tail\""),
                "Должен быть хвост с именем 'Pretty fluffy tail'");
        assertTrue(catJson.contains("\"paw\""),
                "Должна встречаться строка 'paw' (лапа)");
        assertTrue(catJson.contains("\"claw\""),
                "Должна встречаться строка 'claw' (когти)");
        assertTrue(catJson.contains("\"parts\""),
                "Должно быть поле 'parts'");

        Main_JSON_Parser.Cat catParsed = JSONToObj(catJson, Main_JSON_Parser.Cat.class);
        assertNotNull(catParsed);
        assertNotNull(catParsed.parts);
        assertEquals(5, catParsed.parts.size(),
                "Список parts должен содержать 5 элементов");
        Main_JSON_Parser.AnimalPart part0 = catParsed.parts.get(0);
        assertEquals("Pretty fluffy tail", part0.name,
                "Первый элемент должен иметь name='Pretty fluffy tail'");
        for (int i = 1; i < 5; i++) {
            Main_JSON_Parser.AnimalPart pawPart = catParsed.parts.get(i);
            assertEquals("paw", pawPart.name,
                    "Элемент №" + i + " должен иметь name='paw' (т.к. это лапа)");
        }
    }
}
