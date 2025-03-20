package dev.tishenko;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TsonTest {
    @Test
    void nullToJson() {
        Tson tson = new Tson();

        assertEquals("null", tson.toJson(null));
    }

    @Test
    void stringToJson() {
        Tson tson = new Tson();

        assertEquals("\"Hello, world!\"", tson.toJson("Hello, world!"));
        assertEquals("\"Привет, мир!\"", tson.toJson("Привет, мир!"));

        assertEquals("\"\\\"\"", tson.toJson("\"")); // Quotation mark \"
        assertEquals("\"\\\\\"", tson.toJson("\\")); // Reverse solidus \\
        assertEquals("\"\\b\"", tson.toJson("\b")); // Backspace \b
        assertEquals("\"\\f\"", tson.toJson("\f")); // Formfeed \f
        assertEquals("\"\\n\"", tson.toJson("\n")); // Linefeed \n
        assertEquals("\"\\r\"", tson.toJson("\r")); // Carriage return \r
        assertEquals("\"\\t\"", tson.toJson("\t")); // Horizontal tab \t
    }

    @Test
    void stringArrayToJson() {
        Tson tson = new Tson();

        assertEquals("[]", tson.toJson(new String[] {}));
        assertEquals("[\"one\",\"two\",\"three\",null]", tson.toJson(new String[] { "one", "two", "three", null }));
        assertEquals("[\"\",\" \"]", tson.toJson(new String[] { "", " " }));
    }

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
        Tson tson = new Tson();

        String expectedJson = "{\"byteValue\":100," +
                "\"shortValue\":1000," +
                "\"intValue\":100000," +
                "\"longValue\":1000000000000," +
                "\"floatValue\":2.71828," +
                "\"doubleValue\":2.718281828459045," +
                "\"charValue\":\"a\"," +
                "\"booleanValue\":true}";

        assertEquals(expectedJson, tson.toJson(new WithBagOfPrimitives()));
    }

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
        Tson tson = new Tson();

        String expectedJson = "{\"byteValue\":100," +
                "\"intValue\":100000," +
                "\"floatValue\":2.71828," +
                "\"charValue\":\"a\"}";

        assertEquals(expectedJson, tson.toJson(new WithBagOfTransientPrimitives()));
    }

    static class WithNestedObject {
        private String name = "OuterObject";
        private Nested nested = new Nested();

        static class Nested {
            private int nestedValue = 42;
        }
    }

    @Test
    void objectWithNestedObjectToJson() {
        Tson tson = new Tson();

        String expectedJson = "{\"name\":\"OuterObject\",\"nested\":{\"nestedValue\":42}}";

        assertEquals(expectedJson, tson.toJson(new WithNestedObject()));
    }

    static class WithNestedArrayOfObjects {
        private Nested[] nestedArray = { new Nested(1), new Nested(2), new Nested(3) };

        static class Nested {
            private int nestedValue;

            Nested(int v) {
                this.nestedValue = v;
            }
        }
    }

    @Test
    void objectWithNestedArrayOfObjectsToJson() {
        Tson tson = new Tson();

        String expectedJson = "{\"nestedArray\":[{\"nestedValue\":1},{\"nestedValue\":2},{\"nestedValue\":3}]}";

        assertEquals(expectedJson, tson.toJson(new WithNestedArrayOfObjects()));
    }

    static class WithNullField {
        private String name = "OuterObject";
        private Object nullField = null;

    }

    @Test
    void objectWithNullFieldToJson() {
        Tson tson = new Tson();

        String expectedJson = "{\"name\":\"OuterObject\",\"nullField\":null}";

        assertEquals(expectedJson, tson.toJson(new WithNullField()));
    }

    static class WithTransientNestedObject {
        private String name = "OuterObject";
        private transient Nested nested = new Nested();

        static class Nested {
            private int nestedValue = 42;
        }
    }

    @Test
    void objectWithTransientNestedObjectToJson() {
        Tson tson = new Tson();

        String expectedJson = "{\"name\":\"OuterObject\"}";

        assertEquals(expectedJson, tson.toJson(new WithTransientNestedObject()));
    }

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
        Tson tson = new Tson();

        String expectedJson = "{\"flag\":true," +
                "\"level2\":{\"name\":\"Level 2\"," +
                "\"level3\":{\"value\":300}}}";

        assertEquals(expectedJson, tson.toJson(new WithThreeLevelsOfNested()));
    }

    static class WithDifferentAccessModifiers {
        public int publicValue = 10;
        private String privateValue = "private";
        protected boolean protectedValue = true;
        int defaultValue = 42;
    }

    @Test
    void objectWithDifferentAccessModifiersToJson() {
        Tson tson = new Tson();

        String expectedJson = "{\"publicValue\":10," +
                "\"privateValue\":\"private\"," +
                "\"protectedValue\":true," +
                "\"defaultValue\":42}";

        assertEquals(expectedJson, tson.toJson(new WithDifferentAccessModifiers()));
    }

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
        Tson tson = new Tson();

        String expectedJson = "{\"id\":123,\"name\":\"Test\"}";

        assertEquals(expectedJson, tson.toJson(new WithMethods()));
    }

    static class WithNestedNonStaticClass {
        private String name = "OuterObject";
        private Nested nested = new Nested();

        class Nested {
            private int nestedValue = 42;
        }
    }

    @Test
    void objectWithNestedNonStaticClass() {
        Tson tson = new Tson();

        String expectedJson = "{\"name\":\"OuterObject\",\"nested\":null}";

        assertEquals(expectedJson, tson.toJson(new WithNestedNonStaticClass()));
    }

    class NonStaticMemberClass {
        private int value = 42;
    }

    @Test
    void objectNonStaticMemberClass() {
        Tson tson = new Tson();

        assertEquals("null", tson.toJson(new NonStaticMemberClass()));
    }

    @Test
    void localClassObjectToJson() {
        Tson tson = new Tson();

        class Local {
            private int value = 42;
        }

        assertEquals("null", tson.toJson(new Local()));
    }

    @Test
    void anonimousClassObjectToJson() {
        Tson tson = new Tson();

        Object anonymousObject = new Object() {
            private int id = 1;
            private String name = "Anonymous";
        };

        assertEquals("null", tson.toJson(anonymousObject));
    }

    static class ArrayElement {
        private String name = "Element";
        private int id;

        ArrayElement(int id) {
            this.id = id;
        }
    }

    @Test
    void arrayOfObjectsToJson() {
        Tson tson = new Tson();

        ArrayElement[] arr = { new ArrayElement(1), new ArrayElement(2), new ArrayElement(3) };
        String expectedJson = "[{\"name\":\"Element\",\"id\":1},"
                + "{\"name\":\"Element\",\"id\":2},"
                + "{\"name\":\"Element\",\"id\":3}]";
        assertEquals(expectedJson, tson.toJson(arr));
    }
}
