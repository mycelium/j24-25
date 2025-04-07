package lab1.CatSON;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class ReadObjectToJSONTests {

    private static class AnimalPart {
        String name = "part";

        public String getName() {
            return name;
        }
    }

    @SuppressWarnings("unused")
    private static class Tail extends AnimalPart {
        private double lenght = 10.2;

        public Tail() {
            name = "Pretty fluffy tail";
        }
    }

    @SuppressWarnings("unused")
    private static class Claw extends AnimalPart {
        public Claw() {
            name = "claw";
        }

        @Override
        public String getName() {
            return "Sharp claw";
        }
    }

    @SuppressWarnings("unused")
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

    @SuppressWarnings("unused")
    private static class Cat {
        List<AnimalPart> parts = new LinkedList<>();

        public Cat(Tail tail, Paw... paws) {
            super();
            parts.add(tail);
            parts.addAll(Arrays.asList(paws));
        }
    }

    @Test
    void catToJSOn() {
        CatSON CatSON = new CatSON();

        Cat cat = new Cat(new Tail(),
                new Paw(true, new Claw(), new Claw(), new Claw(), new Claw()),
                new Paw(true, new Claw(), new Claw(), new Claw(), new Claw()),
                new Paw(false, new Claw(), new Claw(), new Claw(), new Claw()),
                new Paw(false, new Claw(), new Claw(), new Claw(), new Claw())
        );
        assertEquals(
                "{\"parts\":[{\"lenght\":10.2,\"name\":\"Pretty fluffy tail\"}," +
                        "{\"isFront\":true,\"claws\":[{\"name\":\"claw\"},{\"name\":\"claw\"}," +
                        "{\"name\":\"claw\"},{\"name\":\"claw\"}],\"name\":\"paw\"}," +
                        "{\"isFront\":true,\"claws\":[{\"name\":\"claw\"},{\"name\":\"claw\"}," +
                        "{\"name\":\"claw\"},{\"name\":\"claw\"}],\"name\":\"paw\"}," +
                        "{\"isFront\":false,\"claws\":[{\"name\":\"claw\"},{\"name\":\"claw\"}," +
                        "{\"name\":\"claw\"},{\"name\":\"claw\"}],\"name\":\"paw\"}," +
                        "{\"isFront\":false,\"claws\":[{\"name\":\"claw\"},{\"name\":\"claw\"}," +
                        "{\"name\":\"claw\"},{\"name\":\"claw\"}],\"name\":\"paw\"}]}", CatSON.toJson(cat));
    }





    @Test
    void nullToJson() {
        CatSON CatSON = new CatSON();

        assertEquals("null", CatSON.toJson(null));
    }

    @Test
    void stringToJson() {
        CatSON CatSON = new CatSON();

        assertEquals("\"Hello, world!\"", CatSON.toJson("Hello, world!"));
        assertEquals("\"Привет, мир!\"", CatSON.toJson("Привет, мир!"));

        assertEquals("\"\\\"\"", CatSON.toJson("\"")); // Quotation mark \"
        assertEquals("\"\\\\\"", CatSON.toJson("\\")); // Reverse solidus \\
        assertEquals("\"\\b\"", CatSON.toJson("\b")); // Backspace \b
        assertEquals("\"\\f\"", CatSON.toJson("\f")); // Formfeed \f
        assertEquals("\"\\n\"", CatSON.toJson("\n")); // Linefeed \n
        assertEquals("\"\\r\"", CatSON.toJson("\r")); // Carriage return \r
        assertEquals("\"\\t\"", CatSON.toJson("\t")); // Horizontal tab \t
    }

    @Test
    void stringArrayToJson() {
        CatSON CatSON = new CatSON();

        assertEquals("[]", CatSON.toJson(new String[]{}));
        assertEquals("[\"one\",\"two\",\"three\",null]", CatSON.toJson(new String[]{"one", "two", "three", null}));
        assertEquals("[\"\",\" \"]", CatSON.toJson(new String[]{"", " "}));
    }

    @SuppressWarnings("unused")
    static class WithBagOfPrimitives {
        private byte byteValue = 100;
        private short shortValue = 1000;
        private int intValue = 100000;
        private long longValue = 1000000000000L;
        private float floatValue = 2.71828f;
        private double doubleValue = 2.718281828459045;
        private char charValue = 'a';
        private boolean booleanValue = true;
    }

    @Test
    void objectWithBagOfPrimitivesToJson() {
        CatSON CatSON = new CatSON();

        String expectedJson = "{\"byteValue\":100," +
                "\"shortValue\":1000," +
                "\"intValue\":100000," +
                "\"longValue\":1000000000000," +
                "\"floatValue\":2.71828," +
                "\"doubleValue\":2.718281828459045," +
                "\"charValue\":\"a\"," +
                "\"booleanValue\":true}";

        assertEquals(expectedJson, CatSON.toJson(new WithBagOfPrimitives()));
    }

    @SuppressWarnings("unused")
    static class WithBagOfTransientPrimitives {
        private byte byteValue = 100;
        private transient short shortValue = 1000;
        private int intValue = 100000;
        private transient long longValue = 1000000000000L;
        private float floatValue = 2.71828f;
        private transient double doubleValue = 2.718281828459045;
        private char charValue = 'a';
        private transient boolean booleanValue = true;
    }

    @Test
    void objectWithBagOfTransientPrimitivesToJson() {
        CatSON CatSON = new CatSON();

        String expectedJson = "{\"byteValue\":100," +
                "\"intValue\":100000," +
                "\"floatValue\":2.71828," +
                "\"charValue\":\"a\"}";

        assertEquals(expectedJson, CatSON.toJson(new WithBagOfTransientPrimitives()));
    }

    @SuppressWarnings("unused")
    static class WithNestedObject {
        private String name = "OuterObject";
        private Nested nested = new Nested();

        static class Nested {
            private int nestedValue = 42;
        }
    }

    @Test
    void objectWithNestedObjectToJson() {
        CatSON CatSON = new CatSON();

        String expectedJson = "{\"name\":\"OuterObject\",\"nested\":{\"nestedValue\":42}}";

        assertEquals(expectedJson, CatSON.toJson(new WithNestedObject()));
    }

    @SuppressWarnings("unused")
    static class WithNestedArrayOfObjects {
        private Nested[] nestedArray = {new Nested(1), new Nested(2), new Nested(3)};

        static class Nested {
            private int nestedValue;

            Nested(int v) {
                this.nestedValue = v;
            }
        }
    }

    @Test
    void objectWithNestedArrayOfObjectsToJson() {
        CatSON CatSON = new CatSON();

        String expectedJson = "{\"nestedArray\":[{\"nestedValue\":1},{\"nestedValue\":2},{\"nestedValue\":3}]}";

        assertEquals(expectedJson, CatSON.toJson(new WithNestedArrayOfObjects()));
    }

    @SuppressWarnings("unused")
    static class WithNullField {
        private String name = "OuterObject";
        private Object nullField = null;

    }

    @Test
    void objectWithNullFieldToJson() {
        CatSON CatSON = new CatSON();

        String expectedJson = "{\"name\":\"OuterObject\",\"nullField\":null}";

        assertEquals(expectedJson, CatSON.toJson(new WithNullField()));
    }

    @SuppressWarnings("unused")
    static class WithTransientNestedObject {
        private String name = "OuterObject";
        private transient Nested nested = new Nested();

        static class Nested {
            private int nestedValue = 42;
        }
    }

    @Test
    void objectWithTransientNestedObjectToJson() {
        CatSON CatSON = new CatSON();

        String expectedJson = "{\"name\":\"OuterObject\"}";

        assertEquals(expectedJson, CatSON.toJson(new WithTransientNestedObject()));
    }

    @SuppressWarnings("unused")
    static class WithThreeLevelsOfNested {
        private boolean flag = true;
        private Level2 level2 = new Level2();

        static class Level2 {
            private String name = "Level 2";
            private Level3 level3 = new Level3();
        }

        static class Level3 {
            private int value = 300;
        }
    }

    @Test
    void objectWithThreeLevelsOfNestedToJson() {
        CatSON CatSON = new CatSON();

        String expectedJson = "{\"flag\":true," +
                "\"level2\":{\"name\":\"Level 2\"," +
                "\"level3\":{\"value\":300}}}";

        assertEquals(expectedJson, CatSON.toJson(new WithThreeLevelsOfNested()));
    }

    @SuppressWarnings("unused")
    static class WithDifferentAccessModifiers {
        public int publicValue = 10;
        private String privateValue = "private";
        protected boolean protectedValue = true;
        int defaultValue = 42;
    }

    @Test
    void objectWithDifferentAccessModifiersToJson() {
        CatSON CatSON = new CatSON();

        String expectedJson = "{\"publicValue\":10," +
                "\"privateValue\":\"private\"," +
                "\"protectedValue\":true," +
                "\"defaultValue\":42}";

        assertEquals(expectedJson, CatSON.toJson(new WithDifferentAccessModifiers()));
    }

    @SuppressWarnings("unused")
    static class WithMethods {
        private int id = 123;
        private String name = "Test";

        public String getName() {
            return name;
        }

        private int getId() {
            return id;
        }

        protected boolean isValid() {
            return true;
        }
    }

    @Test
    void objectWithMethodsToJson() {
        CatSON CatSON = new CatSON();

        String expectedJson = "{\"id\":123,\"name\":\"Test\"}";

        assertEquals(expectedJson, CatSON.toJson(new WithMethods()));
    }

    @SuppressWarnings("unused")
    static class WithStaticFields {
        private static int privateStaticValue = 42;
        public static int publicStaticValue = 42;
        static int staticValue = 42;
        int value = 42;
    }

    @Test
    void objectWithStaticFields() {
        CatSON CatSON = new CatSON();

        assertEquals("{\"value\":42}", CatSON.toJson(new WithStaticFields()));
    }

    @SuppressWarnings("unused")
    static class WithNestedNonStaticClass {
        private String name = "OuterObject";
        private Nested nested = new Nested();

        class Nested {
            private int nestedValue = 42;
        }
    }

    @Test
    void objectWithNestedNonStaticClass() {
        CatSON CatSON = new CatSON();

        String expectedJson = "{\"name\":\"OuterObject\",\"nested\":null}";

        assertEquals(expectedJson, CatSON.toJson(new WithNestedNonStaticClass()));
    }

    @SuppressWarnings("unused")
    class NonStaticMemberClass {
        private int value = 42;
    }

    @Test
    void objectNonStaticMemberClass() {
        CatSON CatSON = new CatSON();

        assertEquals("null", CatSON.toJson(new NonStaticMemberClass()));
    }

    @Test
    void localClassObjectToJson() {
        CatSON CatSON = new CatSON();

        @SuppressWarnings("unused")
        class Local {
            private int value = 42;
        }

        assertEquals("null", CatSON.toJson(new Local()));
    }

    @Test
    void anonimousClassObjectToJson() {
        CatSON CatSON = new CatSON();

        @SuppressWarnings("unused")
        Object anonymousObject = new Object() {
            private int id = 1;
            private String name = "Anonymous";
        };

        assertEquals("null", CatSON.toJson(anonymousObject));
    }

    @SuppressWarnings("unused")
    static class ArrayElement {
        private String name = "Element";
        private int id;

        ArrayElement(int id) {
            this.id = id;
        }
    }

    @Test
    void arrayOfObjectsToJson() {
        CatSON CatSON = new CatSON();

        ArrayElement[] arr = {new ArrayElement(1), new ArrayElement(2), new ArrayElement(3)};
        String expectedJson = "[{\"name\":\"Element\",\"id\":1},"
                + "{\"name\":\"Element\",\"id\":2},"
                + "{\"name\":\"Element\",\"id\":3}]";
        assertEquals(expectedJson, CatSON.toJson(arr));
    }
}