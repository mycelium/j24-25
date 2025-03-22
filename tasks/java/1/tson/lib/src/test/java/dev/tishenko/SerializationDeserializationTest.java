package dev.tishenko;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SerializationDeserializationTest {
    static class SimpleObject {
        private String name;
        private int value;

        public SimpleObject() {
        }

        public SimpleObject(String name, int value) {
            this.name = name;
            this.value = value;
        }
    }

    @Test
    void simpleObjectSerializationDeserialization() {
        Tson tson = new Tson();

        SimpleObject obj = new SimpleObject("Test Name", 42);

        String json = tson.toJson(obj);
        SimpleObject deserializedObj = tson.fromJson(json, SimpleObject.class);

        assertNotNull(deserializedObj);
        assertEquals(obj.name, deserializedObj.name);
        assertEquals(obj.value, deserializedObj.value);
    }

    static class OuterObject {
        private String name;
        private InnerObject inner;

        public OuterObject() {
        }

        public OuterObject(String name, InnerObject inner) {
            this.name = name;
            this.inner = inner;
        }
    }

    static class InnerObject {
        private String name;

        public InnerObject() {
        }

        public InnerObject(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Test
    void OuterObjectSerializationDeserialization() {
        Tson tson = new Tson();

        OuterObject obj = new OuterObject(
                "Outer Object", new InnerObject("Inner Object"));

        String json = tson.toJson(obj);
        OuterObject deserializedObj = tson.fromJson(json, OuterObject.class);

        assertNotNull(deserializedObj);
        assertEquals(obj.name, deserializedObj.name);
        assertNotNull(deserializedObj.inner);
        assertEquals(obj.inner.name, deserializedObj.inner.name);
    }

    static class ArrayElement {
        private String name = "Element";

        ArrayElement() {
        }

        ArrayElement(String name) {
            this.name = name;
        }
    }

    @Test
    void arrayOfObjectsSerializationDeserialization() {
        Tson tson = new Tson();

        ArrayElement[] items = {
                new ArrayElement("Item 1"),
                new ArrayElement("Item 2"),
                new ArrayElement("Item 3")
        };

        String json = tson.toJson(items);

        ArrayElement[] deserializedItems = tson.fromJson(json, ArrayElement[].class);
        assertNotNull(deserializedItems);
        assertEquals(3, deserializedItems.length);
        assertEquals(items[0].name, deserializedItems[0].name);
        assertEquals(items[1].name, deserializedItems[1].name);
        assertEquals(items[2].name, deserializedItems[2].name);
    }

    static class MultiTypeObject {
        private boolean flag;
        private double number;
        private String text;
        private Object nullField;

        public MultiTypeObject() {
        }

        public MultiTypeObject(boolean flag, double number, String text, Object nullField) {
            this.flag = flag;
            this.number = number;
            this.text = text;
            this.nullField = nullField;
        }
    }

    @Test
    void multiTypeSerializationDeserialization() {
        Tson tson = new Tson();

        MultiTypeObject obj = new MultiTypeObject(
                true,
                3.1415,
                "Test Text",
                null);

        String json = tson.toJson(obj);
        MultiTypeObject deserialized = tson.fromJson(json, MultiTypeObject.class);

        assertNotNull(deserialized);
        assertEquals(obj.flag, deserialized.flag);
        assertEquals(obj.number, deserialized.number);
        assertEquals(obj.text, deserialized.text);
        assertNull(deserialized.nullField);
    }

    static class NullableObject {
        private String normalField;
        private String nullField;
        private Object nullObject;

        public NullableObject() {
        }

        public NullableObject(String normalField, String nullField) {
            this.normalField = normalField;
            this.nullField = nullField;
            this.nullObject = null;
        }
    }

    @Test
    void nullHandlingSerializationDeserialization() {
        Tson tson = new Tson();

        NullableObject obj = new NullableObject("Exists", null);

        String json = tson.toJson(obj);
        NullableObject deserialized = tson.fromJson(json, NullableObject.class);

        assertNotNull(deserialized);
        assertEquals("Exists", deserialized.normalField);
        assertNull(deserialized.nullField);
        assertNull(deserialized.nullObject);
    }
}
