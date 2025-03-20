package dev.tishenko;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PrimitivesFromJsonTest {
    @Test
    void byteFromJson() {
        Tson tson = new Tson();

        assertEquals((byte) 100, tson.fromJson("100", byte.class));
        assertEquals((byte) -100, tson.fromJson("-100", byte.class));
        assertEquals((byte) 0, tson.fromJson("0", byte.class));
        assertEquals(Byte.valueOf((byte) 100), tson.fromJson("100", Byte.class));
    }

    @Test
    void shortFromJson() {
        Tson tson = new Tson();

        assertEquals((short) 1000, tson.fromJson("1000", short.class));
        assertEquals((short) -1000, tson.fromJson("-1000", short.class));
        assertEquals((short) 0, tson.fromJson("0", short.class));
        assertEquals(Short.valueOf((short) 1000), tson.fromJson("1000", Short.class));
    }

    @Test
    void integerFromJson() {
        Tson tson = new Tson();

        assertEquals(100000, tson.fromJson("100000", int.class));
        assertEquals(-100000, tson.fromJson("-100000", int.class));
        assertEquals(0, tson.fromJson("0", int.class));
        assertEquals(Integer.valueOf(100000), tson.fromJson("100000", Integer.class));
    }

    @Test
    void longFromJson() {
        Tson tson = new Tson();

        assertEquals(1000000000000L, tson.fromJson("1000000000000", long.class));
        assertEquals(-1000000000000L, tson.fromJson("-1000000000000", long.class));
        assertEquals(0L, tson.fromJson("0", long.class));
        assertEquals(Long.valueOf(1000000000000L), tson.fromJson("1000000000000", Long.class));
    }

    @Test
    void booleanFromJson() {
        Tson tson = new Tson();

        assertTrue(tson.fromJson("true", boolean.class));
        assertFalse(tson.fromJson("false", boolean.class));
        assertEquals(Boolean.TRUE, tson.fromJson("true", Boolean.class));
        assertEquals(Boolean.FALSE, tson.fromJson("false", Boolean.class));
    }

    @Test
    void floatFromJson() {
        Tson tson = new Tson();

        assertEquals(2.71828f, tson.fromJson("2.71828", float.class), 0.00001f);
        assertEquals(-2.71828f, tson.fromJson("-2.71828", float.class), 0.00001f);
        assertEquals(1E10f, tson.fromJson("1.0E10", float.class), 0.0f);
        assertEquals(Float.valueOf(2.71828f), tson.fromJson("2.71828", Float.class), 0.00001f);
    }

    @Test
    void doubleFromJson() {
        Tson tson = new Tson();

        assertEquals(2.718281828459045, tson.fromJson("2.718281828459045", double.class), 0.000000000000001);
        assertEquals(-2.718281828459045, tson.fromJson("-2.718281828459045", double.class), 0.000000000000001);
        assertEquals(1E100, tson.fromJson("1.0E100", double.class), 0.0);
        assertEquals(Double.valueOf(2.718281828459045), tson.fromJson("2.718281828459045", Double.class),
                0.000000000000001);
    }

    @Test
    void charFromJson() {
        Tson tson = new Tson();

        assertEquals('a', tson.fromJson("\"a\"", char.class));
        assertEquals('"', tson.fromJson("\"\\\"\"", char.class));
        assertEquals('\\', tson.fromJson("\"\\\\\"", char.class));
        assertEquals('\b', tson.fromJson("\"\\b\"", char.class));
        assertEquals('\f', tson.fromJson("\"\\f\"", char.class));
        assertEquals('\n', tson.fromJson("\"\\n\"", char.class));
        assertEquals('\r', tson.fromJson("\"\\r\"", char.class));
        assertEquals('\t', tson.fromJson("\"\\t\"", char.class));

        assertEquals('\u20AC', tson.fromJson("\"\\u20AC\"", char.class));
        assertEquals('\u5B57', tson.fromJson("\"\\u5B57\"", char.class));
        assertEquals('\u00A9', tson.fromJson("\"\\u00A9\"", char.class));
        assertEquals('\u03A9', tson.fromJson("\"\\u03A9\"", char.class));
    }
}