package dev.tishenko;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PrimitivesFromJsonTest {
    @Test
    void primitivesWithWhitespacesFromJson() {
        Tson tson = new Tson();

        assertEquals((byte) 100, tson.fromJson("\t\r  \n \t100\n \n ", byte.class));
        assertEquals((short) 100, tson.fromJson("\t\r  \n \t100\n \n ", short.class));
        assertEquals(100, tson.fromJson("\t\r  \n \t100\n \n ", int.class));
        assertEquals(100L, tson.fromJson("\t\r  \n \t100\n \n ", long.class));
        assertEquals(2.71f, tson.fromJson("\t\r  \n \t2.71\n \n ", float.class));
        assertEquals(2.71, tson.fromJson("\t\r  \n \t2.71\n \n ", double.class));
        assertEquals(true, tson.fromJson("\t\r  \n \ttrue\n \n ", boolean.class));
        assertEquals(false, tson.fromJson("\t\r  \n \tfalse\n \n ", boolean.class));
        assertEquals('a', tson.fromJson("\t\r  \n \t\"a\"\n \n ", char.class));
        assertEquals('\n', tson.fromJson("\t\r  \n \t\"\n\"\n \n ", char.class));
    }

    @Test
    void wrappedPrimitivesWithWhitespacesFromJson() {
        Tson tson = new Tson();

        assertEquals(Byte.valueOf((byte) 100), tson.fromJson("\t\r  \n \t100\n \n ", Byte.class));
        assertEquals(Short.valueOf((short) 100), tson.fromJson("\t\r  \n \t100\n \n ", Short.class));
        assertEquals(Integer.valueOf(100), tson.fromJson("\t\r  \n \t100\n \n ", Integer.class));
        assertEquals(Long.valueOf(100L), tson.fromJson("\t\r  \n \t100\n \n ", Long.class));
        assertEquals(Float.valueOf(2.71f), tson.fromJson("\t\r  \n \t2.71\n \n ", Float.class));
        assertEquals(Double.valueOf(2.71), tson.fromJson("\t\r  \n \t2.71\n \n ", Double.class));
        assertEquals(Boolean.TRUE, tson.fromJson("\t\r  \n \ttrue\n \n ", Boolean.class));
        assertEquals(Boolean.FALSE, tson.fromJson("\t\r  \n \tfalse\n \n ", Boolean.class));
        assertEquals(Character.valueOf('a'), tson.fromJson("\t\r  \n \t\"a\"\n \n ", Character.class));
        assertEquals(Character.valueOf('\n'), tson.fromJson("\t\r  \n \t\"\n\"\n \n ", Character.class));
    }

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

    @Test
    void arraysWithPrimitivesAndWhitespacesFromJson() {
        Tson tson = new Tson();

        assertArrayEquals(new byte[0], tson.fromJson("\t\r  \n \t[]\t\r  \n \t", byte[].class));

        assertArrayEquals(new byte[] { 1, 2, 3 },
                tson.fromJson("\t\r  \n \t[  1 ,  \t 2 ,  3 ]\t\r  \n \t", byte[].class));
        assertArrayEquals(new short[] { 10, 20, 30 },
                tson.fromJson("\t\r  \n \t[ 10 ,  \t20 , 30 ]\t\r  \n \t", short[].class));
        assertArrayEquals(new int[] { 100, 200, 300 },
                tson.fromJson("\t\r  \n \t[100 ,  \t200 , 300 ]\t\r  \n \t", int[].class));
        assertArrayEquals(new long[] { 1000L, 2000L, 3000L },
                tson.fromJson("\t\r  \n \t[1000 ,  \t2000 , 3000 ]\t\r  \n \t", long[].class));
        assertArrayEquals(new float[] { 1.23f, 4.56f, 7.89f },
                tson.fromJson("\t\r  \n \t[  1.23 ,  \t4.56 , 7.89 ]\t\r  \n \t", float[].class));
        assertArrayEquals(new double[] { 2.71, 3.14, 1.61 },
                tson.fromJson("\t\r  \n \t[2.71 , \t3.14 ,  1.61 ]\t\r  \n \t", double[].class));
        assertArrayEquals(new boolean[] { true, false, true },
                tson.fromJson("\t\r  \n \t[ true ,  \tfalse ,  true ]\t\r  \n \t", boolean[].class));
        assertArrayEquals(new char[] { 'a', 'b', 'c' },
                tson.fromJson("\t\r  \n \t[ \"a\" ,  \t\"b\" , \"c\" ]\t\r  \n \t", char[].class));
    }

    @Test
    void arraysWithWrappersAndWhitespacesFromJson() {
        Tson tson = new Tson();

        assertArrayEquals(new Byte[0], tson.fromJson("\t\r  \n \t[]\t\r  \n \t", Byte[].class));

        assertArrayEquals(new Byte[] { 1, 2, 3 },
                tson.fromJson("\t\r  \n \t[  1 ,  \t2 ,  3 ]\t\r  \n \t", Byte[].class));
        assertArrayEquals(new Short[] { 10, 20, 30 },
                tson.fromJson("\t\r  \n \t[ 10 ,  \t20 , 30 ]\t\r  \n \t", Short[].class));
        assertArrayEquals(new Integer[] { 100, 200, 300 },
                tson.fromJson("\t\r  \n \t[100 ,  \t200 , 300 ]\t\r  \n \t", Integer[].class));
        assertArrayEquals(new Long[] { 1000L, 2000L, 3000L },
                tson.fromJson("\t\r  \n \t[1000 ,  \t2000 , 3000 ]\t\r  \n \t", Long[].class));
        assertArrayEquals(new Float[] { 1.23f, 4.56f, 7.89f },
                tson.fromJson("\t\r  \n \t[  1.23 ,  \t4.56 , 7.89 ]\t\r  \n \t", Float[].class));
        assertArrayEquals(new Double[] { 2.71, 3.14, 1.61 },
                tson.fromJson("\t\r  \n \t[2.71 , \t3.14 ,  1.61 ]\t\r  \n \t", Double[].class));
        assertArrayEquals(new Boolean[] { true, false, true },
                tson.fromJson("\t\r  \n \t[ true ,  \tfalse ,  true ]\t\r  \n \t", Boolean[].class));
        assertArrayEquals(new Character[] { 'a', 'b', 'c' },
                tson.fromJson("\t\r  \n \t[ \"a\" ,  \t\"b\" , \"c\" ]\t\r  \n \t", Character[].class));
    }

    @Test
    void byteArrayFromJson() {
        Tson tson = new Tson();

        assertArrayEquals(new byte[0], tson.fromJson("[]", byte[].class));
        assertArrayEquals(new byte[] { 100, -100, 0 }, tson.fromJson("[100, -100, 0]", byte[].class));

        assertArrayEquals(new Byte[0], tson.fromJson("[]", Byte[].class));
        assertArrayEquals(new Byte[] { 100, -100, 0, null }, tson.fromJson("[100, -100, 0, null]", Byte[].class));
    }

    @Test
    void shortArrayFromJson() {
        Tson tson = new Tson();

        assertArrayEquals(new short[0], tson.fromJson("[]", short[].class));
        assertArrayEquals(new short[] { 1000, -1000, 0 }, tson.fromJson("[1000, -1000, 0]", short[].class));

        assertArrayEquals(new Short[0], tson.fromJson("[]", Short[].class));
        assertArrayEquals(new Short[] { 1000, -1000, 0, null }, tson.fromJson("[1000, -1000, 0, null]", Short[].class));
    }

    @Test
    void intArrayFromJson() {
        Tson tson = new Tson();

        assertArrayEquals(new int[0], tson.fromJson("[]", int[].class));
        assertArrayEquals(new int[] { 100000, -100000, 0 }, tson.fromJson("[100000, -100000, 0]", int[].class));

        assertArrayEquals(new Integer[0], tson.fromJson("[]", Integer[].class));
        assertArrayEquals(new Integer[] { 100000, -100000, 0, null },
                tson.fromJson("[100000, -100000, 0, null]", Integer[].class));
    }

    @Test
    void longArrayFromJson() {
        Tson tson = new Tson();

        assertArrayEquals(new long[0], tson.fromJson("[]", long[].class));
        assertArrayEquals(new long[] { 1000000000000L, -1000000000000L, 0L },
                tson.fromJson("[1000000000000, -1000000000000, 0]", long[].class));

        assertArrayEquals(new Long[0], tson.fromJson("[]", Long[].class));
        assertArrayEquals(new Long[] { 1000000000000L, -1000000000000L, 0L, null },
                tson.fromJson("[1000000000000, -1000000000000, 0, null]", Long[].class));
    }

    @Test
    void booleanArrayFromJson() {
        Tson tson = new Tson();

        assertArrayEquals(new boolean[0], tson.fromJson("[]", boolean[].class));
        assertArrayEquals(new boolean[] { true, false, true }, tson.fromJson("[true, false, true]", boolean[].class));

        assertArrayEquals(new Boolean[0], tson.fromJson("[]", Boolean[].class));
        assertArrayEquals(new Boolean[] { true, false, true, null },
                tson.fromJson("[true, false, true, null]", Boolean[].class));
    }

    @Test
    void floatArrayFromJson() {
        Tson tson = new Tson();

        assertArrayEquals(new float[0], tson.fromJson("[]", float[].class), 0.0001f);
        assertArrayEquals(new float[] { 2.71828f, -2.71828f, 1E10f },
                tson.fromJson("[2.71828, -2.71828, 1.0E10]", float[].class), 0.0001f);

        assertArrayEquals(new Float[0], tson.fromJson("[]", Float[].class));
        assertArrayEquals(new Float[] { 2.71828f, -2.71828f, 1E10f, null },
                tson.fromJson("[2.71828, -2.71828, 1.0E10, null]", Float[].class));
    }

    @Test
    void doubleArrayFromJson() {
        Tson tson = new Tson();

        assertArrayEquals(new double[0], tson.fromJson("[]", double[].class), 0.0001);
        assertArrayEquals(new double[] { 2.718281828459045, -2.718281828459045, 1E100 },
                tson.fromJson("[2.718281828459045, -2.718281828459045, 1.0E100]", double[].class), 0.0001);

        assertArrayEquals(new Double[0], tson.fromJson("[]", Double[].class));
        assertArrayEquals(new Double[] { 2.718281828459045, -2.718281828459045, 1E100, null },
                tson.fromJson("[2.718281828459045, -2.718281828459045, 1.0E100, null]", Double[].class));
    }

    @Test
    void charArrayFromJson() {
        Tson tson = new Tson();

        assertArrayEquals(new char[0], tson.fromJson("[]", char[].class));
        assertArrayEquals(new char[] { 'a', '"', '\\', '\b', '\f', '\n', '\r', '\t' }, tson
                .fromJson("[\"a\", \"\\\"\", \"\\\\\", \"\\b\", \"\\f\", \"\\n\", \"\\r\", \"\\t\"]", char[].class));

        assertArrayEquals(new Character[0], tson.fromJson("[]", Character[].class));
        assertArrayEquals(new Character[] { 'a', '"', '\\', '\b', '\f', '\n', '\r', '\t', null }, tson.fromJson(
                "[\"a\", \"\\\"\", \"\\\\\", \"\\b\", \"\\f\", \"\\n\", \"\\r\", \"\\t\", null]", Character[].class));
    }

    @Test
    void int2DArrayFromJson() {
        Tson tson = new Tson();

        assertArrayEquals(new int[][] {}, tson.fromJson("[]", int[][].class));
        assertArrayEquals(new int[][] { {}, {} }, tson.fromJson("[[], []]", int[][].class));
        assertArrayEquals(new int[][] { { 100000, -100000, 0 }, { 1, -1, 0 } },
                tson.fromJson("[[100000, -100000, 0], [1, -1, 0]]", int[][].class));
    }

    @Test
    void integerWrapper2DArrayFromJson() {
        Tson tson = new Tson();

        assertArrayEquals(new Integer[][] {}, tson.fromJson("[]", Integer[][].class));
        assertArrayEquals(new Integer[][] { {}, {} }, tson.fromJson("[[], []]", Integer[][].class));
        assertArrayEquals(new Integer[][] { { 100000, -100000, 0 }, { 1, -1, 0 } },
                tson.fromJson("[[100000, -100000, 0], [1, -1, 0]]", Integer[][].class));
        assertArrayEquals(new Integer[][] { { 100000, null, 0 }, { null, -1, 0 } },
                tson.fromJson("[[100000, null, 0], [null, -1, 0]]", Integer[][].class));
    }
}