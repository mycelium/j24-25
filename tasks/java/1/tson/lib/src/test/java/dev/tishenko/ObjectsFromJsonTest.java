package dev.tishenko;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ObjectsFromJsonTest {
    @Test
    void stringFromJson() {
        Tson tson = new Tson();

        assertEquals("Hello, World!", tson.fromJson("\"Hello, World!\"", String.class));
        assertEquals("Hello, World!", tson.fromJson("  \"Hello, World!\"  ", String.class));
        assertEquals("Line1\nLine2", tson.fromJson("\"Line1\\nLine2\"", String.class));
        assertEquals("Tab\tTab", tson.fromJson("\"Tab\\tTab\"", String.class));
        assertEquals("Quote\"Quote", tson.fromJson("\"Quote\\\"Quote\"", String.class));
        assertEquals("\u0024", tson.fromJson("\"\\u0024\"", String.class));
    }

    @Test
    void stringArrayFromJson() {
        Tson tson = new Tson();

        assertArrayEquals(new String[] { "apple", "banana", "cherry" },
                tson.fromJson("[\"apple\", \"banana\", \"cherry\"]", String[].class));
        assertArrayEquals(new String[] { "  apple ", "banana", " cherry " },
                tson.fromJson("[\"  apple \", \"banana\", \" cherry \"]", String[].class));
        assertArrayEquals(new String[] { "Line1\nLine2", "Tab\tTab", "Quote\"Quote" },
                tson.fromJson("[\"Line1\\nLine2\", \"Tab\\tTab\", \"Quote\\\"Quote\"]", String[].class));
        assertArrayEquals(new String[] { "", "", "" },
                tson.fromJson("[\"\", \"\", \"\"]", String[].class));
    }

    static class WithBagOfPrimitives {
        private byte byteValue;
        private short shortValue;
        private int intValue;
        private long longValue;
        private float floatValue;
        private double doubleValue;
        private char charValue;
        private boolean booleanValue;
    }

    @Test
    void objectWithBagOfPrimitivesFromJson() {
        Tson tson = new Tson();

        String json = """
                {
                    "byteValue": 100,
                    "shortValue": 1000,
                    "intValue": 100000,
                    "longValue": 1000000000000,
                    "floatValue": 2.71828,
                    "doubleValue": 2.718281828459045,
                    "charValue": "a",
                    "booleanValue": true
                }
                """;

        WithBagOfPrimitives obj = tson.fromJson(json, WithBagOfPrimitives.class);

        assertEquals(100, obj.byteValue);
        assertEquals(1000, obj.shortValue);
        assertEquals(100000, obj.intValue);
        assertEquals(1000000000000L, obj.longValue);
        assertEquals(2.71828f, obj.floatValue);
        assertEquals(2.718281828459045, obj.doubleValue);
        assertEquals('a', obj.charValue);
        assertTrue(obj.booleanValue);
    }

    static class WithBagOfTransientPrimitives {
        private byte byteValue;
        private transient short shortValue = 999;
        private int intValue;
        private transient long longValue = 999999999L;
        private float floatValue;
        private transient double doubleValue = 9.99;
        private char charValue;
        private transient boolean booleanValue = true;
    }

    @Test
    void objectWithBagOfTransientPrimitivesFromJson() {
        Tson tson = new Tson();

        String json = """
                {
                    "byteValue": 100,
                    "shortValue": 1000,
                    "intValue": 100000,
                    "longValue": 1000000000000,
                    "floatValue": 2.71828,
                    "doubleValue": 2.718281828459045,
                    "charValue": "a",
                    "booleanValue": false
                }
                """;

        WithBagOfTransientPrimitives obj = tson.fromJson(json, WithBagOfTransientPrimitives.class);

        assertEquals(100, obj.byteValue);
        assertEquals(100000, obj.intValue);
        assertEquals(2.71828f, obj.floatValue);
        assertEquals('a', obj.charValue);

        assertEquals(999, obj.shortValue);
        assertEquals(999999999L, obj.longValue);
        assertEquals(9.99, obj.doubleValue);
        assertTrue(obj.booleanValue);
    }

    static class WithBagOfWrappers {
        private Byte byteValue;
        private Short shortValue;
        private Integer intValue;
        private Long longValue;
        private Float floatValue;
        private Double doubleValue;
        private Character charValue;
        private Boolean booleanValue;
    }

    @Test
    void objectWithBagOfWrappersFromJson() {
        Tson tson = new Tson();

        String json = """
                {
                    "byteValue": 100,
                    "shortValue": 1000,
                    "intValue": 100000,
                    "longValue": 1000000000000,
                    "floatValue": 2.71828,
                    "doubleValue": 2.718281828459045,
                    "charValue": "a",
                    "booleanValue": true
                }
                """;

        WithBagOfWrappers obj = tson.fromJson(json, WithBagOfWrappers.class);

        assertEquals(Byte.valueOf((byte) 100), obj.byteValue);
        assertEquals(Short.valueOf((short) 1000), obj.shortValue);
        assertEquals(Integer.valueOf(100000), obj.intValue);
        assertEquals(Long.valueOf(1000000000000L), obj.longValue);
        assertEquals(Float.valueOf(2.71828f), obj.floatValue);
        assertEquals(Double.valueOf(2.718281828459045), obj.doubleValue);
        assertEquals(Character.valueOf('a'), obj.charValue);
        assertEquals(Boolean.TRUE, obj.booleanValue);
    }

    static class WithNestedObject {
        private String name;
        private Nested nested;

        static class Nested {
            private int nestedValue;
        }
    }

    @Test
    void objectWithNestedObjectFromJson() {
        Tson tson = new Tson();

        String json = """
                {
                    "name": "OuterObject",
                    "nested": { "nestedValue": 42 }
                }
                """;
        WithNestedObject obj = tson.fromJson(json, WithNestedObject.class);

        assertEquals("OuterObject", obj.name);
        assertNotNull(obj.nested);
        assertEquals(42, obj.nested.nestedValue);
    }

    static class WithNestedArrayOfObjects {
        private Nested[] nestedArray;

        static class Nested {
            private int nestedValue;
        }
    }

    @Test
    void objectWithNestedArrayOfObjectsFromJson() {
        Tson tson = new Tson();

        String json = """
                {
                    "nestedArray": [
                        { "nestedValue": 1 },
                        { "nestedValue": 2 },
                        { "nestedValue": 3 }
                    ]
                }
                """;
        WithNestedArrayOfObjects obj = tson.fromJson(json, WithNestedArrayOfObjects.class);

        assertNotNull(obj.nestedArray);
        assertEquals(3, obj.nestedArray.length);
        assertEquals(1, obj.nestedArray[0].nestedValue);
        assertEquals(2, obj.nestedArray[1].nestedValue);
        assertEquals(3, obj.nestedArray[2].nestedValue);
    }

    static class WithNullField {
        private String name;
        private Object nullField;
    }

    @Test
    void objectWithNullFieldFromJson() {
        Tson tson = new Tson();

        String json = "{\"name\":\"OuterObject\",\"nullField\":null}";

        WithNullField obj = tson.fromJson(json, WithNullField.class);

        assertEquals("OuterObject", obj.name);
        assertNull(obj.nullField);
    }

    @Test
    void objectWithNullFieldSkippedFromJson() {
        Tson tson = new Tson();

        String json = "{\"name\":\"OuterObject\"}";

        WithNullField obj = tson.fromJson(json, WithNullField.class);

        assertEquals("OuterObject", obj.name);
        assertNull(obj.nullField);
    }

    static class WithThreeLevelsOfNested {
        private boolean flag;
        private Level2 level2;

        static class Level2 {
            private String name;
            private Level3 level3;
        }

        static class Level3 {
            private int value;
        }
    }

    @Test
    void objectWithThreeLevelsOfNestedFromJson() {
        Tson tson = new Tson();

        String json = """
                {
                    "flag": true,
                    "level2": {
                        "name": "Level 2",
                        "level3": { "value": 300 }
                    }
                }
                """;

        WithThreeLevelsOfNested obj = tson.fromJson(json, WithThreeLevelsOfNested.class);

        assertTrue(obj.flag);
        assertNotNull(obj.level2);
        assertEquals("Level 2", obj.level2.name);
        assertNotNull(obj.level2.level3);
        assertEquals(300, obj.level2.level3.value);
    }

    static class WithDifferentAccessModifiers {
        public int publicValue;
        private String privateValue;
        protected boolean protectedValue;
        int defaultValue;
    }

    @Test
    void objectWithDifferentAccessModifiersFromJson() {
        Tson tson = new Tson();

        String json = """
                {
                    "publicValue": 10,
                    "privateValue": "private",
                    "protectedValue": true,
                    "defaultValue": 42
                }
                """;

        WithDifferentAccessModifiers obj = tson.fromJson(json, WithDifferentAccessModifiers.class);

        assertEquals(10, obj.publicValue);
        assertEquals("private", obj.privateValue);
        assertTrue(obj.protectedValue);
        assertEquals(42, obj.defaultValue);
    }

    @SuppressWarnings("unused")
    static class WithStaticFields {
        private static int privateStaticValue = 42;
        public static int publicStaticValue = 42;
        static int staticValue = 42;
        int value;
    }

    @Test
    void objectWithStaticFieldsFromJson() {
        Tson tson = new Tson();

        String json = """
                {
                    "privateStaticValue": 52,
                    "publicStaticValue": 52,
                    "staticValue": 52,
                    "value": 42
                }
                """;

        WithStaticFields obj = tson.fromJson(json, WithStaticFields.class);

        assertEquals(42, obj.value);
        assertEquals(42, WithStaticFields.privateStaticValue);
        assertEquals(42, WithStaticFields.publicStaticValue);
        assertEquals(42, WithStaticFields.staticValue);
    }

    static class ArrayElement {
        private String name;
        private int id;
    }

    @Test
    void arrayOfObjectsFromJson() {
        Tson tson = new Tson();

        String json = """
                [
                    { "name": "Element1", "id": 1 },
                    { "name": "Element2", "id": 2 },
                    { "name": "Element3", "id": 3 }
                ]
                """;

        ArrayElement[] arr = tson.fromJson(json, ArrayElement[].class);

        assertNotNull(arr);
        assertEquals(3, arr.length);
        assertEquals("Element1", arr[0].name);
        assertEquals(1, arr[0].id);
        assertEquals("Element2", arr[1].name);
        assertEquals(2, arr[1].id);
        assertEquals("Element3", arr[2].name);
        assertEquals(3, arr[2].id);
    }

    @SuppressWarnings("unused")
    class NonStaticMemberClass {
        private int value = 42;
    }

    @Test
    void objectNonStaticMemberClassFromJson() {
        Tson tson = new Tson();

        String json = """
                {
                    "value": 42
                }
                """;

        assertNull(tson.fromJson(json, NonStaticMemberClass.class));
    }

    @Test
    void localClassObjectFromJson() {
        Tson tson = new Tson();

        String json = """
                {
                    "value": 42
                }
                """;

        @SuppressWarnings("unused")
        class Local {
            private int value = 42;
        }

        assertNull(tson.fromJson(json, Local.class));
    }

    @Test
    void anonymousClassObjectFromJson() {
        Tson tson = new Tson();

        String json = """
                {
                    "id": 1,
                    "name": "Anonymous"
                }
                """;

        @SuppressWarnings("unused")
        Object anonymousObject = new Object() {
            private int id = 1;
            private String name = "Anonymous";
        };

        assertNull(tson.fromJson(json, anonymousObject.getClass()));
    }

    static class WithPrivateConstructor {
        private int value;

        private WithPrivateConstructor() {
        }
    }

    @Test
    void objectWithPrivateConstructorFromJson() {
        Tson tson = new Tson();

        String json = "{\"value\":42}";

        WithPrivateConstructor obj = tson.fromJson(json, WithPrivateConstructor.class);

        assertEquals(42, obj.value);
    }

    static class WithAdditionalConstructor {
        private int value;

        WithAdditionalConstructor() {
        }

        WithAdditionalConstructor(int value) {
            this.value = value;
        }
    }

    @Test
    void WithAdditionalConstructorFromJson() {
        Tson tson = new Tson();

        String json = "{\"value\":42}";

        WithAdditionalConstructor obj = tson.fromJson(json, WithAdditionalConstructor.class);

        assertEquals(42, obj.value);
    }
}
