package dev.tishenko;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PrimitivesToJsonTest {
    @Test
    void byteToJson() {
        Tson tson = new Tson();

        assertEquals("100", tson.toJson((byte) 100));
        assertEquals("-100", tson.toJson((byte) -100));
        assertEquals("0", tson.toJson((byte) 0));
        assertEquals("100", tson.toJson(Byte.valueOf((byte) 100)));
    }

    @Test
    void shortToJson() {
        Tson tson = new Tson();

        assertEquals("1000", tson.toJson((short) 1000));
        assertEquals("-1000", tson.toJson((short) -1000));
        assertEquals("0", tson.toJson((short) 0));
        assertEquals("1000", tson.toJson(Short.valueOf((short) 1000)));
    }

    @Test
    void integerToJson() {
        Tson tson = new Tson();

        assertEquals("100000", tson.toJson(100000));
        assertEquals("-100000", tson.toJson(-100000));
        assertEquals("0", tson.toJson(0));
        assertEquals("100000", tson.toJson(Integer.valueOf(100000)));
    }

    @Test
    void longToJson() {
        Tson tson = new Tson();

        assertEquals("1000000000000", tson.toJson(1000000000000L));
        assertEquals("-1000000000000", tson.toJson(-1000000000000L));
        assertEquals("0", tson.toJson(0L));
        assertEquals("1000000000000", tson.toJson(Long.valueOf(1000000000000L)));
    }

    @Test
    void booleanToJson() {
        Tson tson = new Tson();

        assertEquals("true", tson.toJson(true));
        assertEquals("false", tson.toJson(false));
        assertEquals("true", tson.toJson(Boolean.valueOf(true)));
        assertEquals("false", tson.toJson(Boolean.valueOf(false)));
    }

    @Test
    void floatToJson() {
        Tson tson = new Tson();

        assertEquals("2.71828", tson.toJson(2.71828f));
        assertEquals("-2.71828", tson.toJson(-2.71828f));
        assertEquals("1.0E10", tson.toJson(1E10f));
        assertEquals("-1.0E10", tson.toJson(-1E10f));
        assertEquals("2.71828", tson.toJson(Float.valueOf(2.71828f)));
    }

    @Test
    void floatNanInfinityToJson() {
        Tson tson = new Tson();

        assertThrows(IllegalArgumentException.class, () -> {
            tson.toJson(Float.NaN);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            tson.toJson(Float.POSITIVE_INFINITY);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            tson.toJson(Float.NEGATIVE_INFINITY);
        });
    }

    @Test
    void doubleToJson() {
        Tson tson = new Tson();

        assertEquals("2.718281828459045", tson.toJson(2.718281828459045));
        assertEquals("-2.718281828459045", tson.toJson(-2.718281828459045));
        assertEquals("1.0E100", tson.toJson(1E100));
        assertEquals("-1.0E100", tson.toJson(-1E100));
        assertEquals("2.718281828459045", tson.toJson(Double.valueOf(2.718281828459045)));
    }

    @Test
    void doubleNanInfinityToJson() {
        Tson tson = new Tson();

        assertThrows(IllegalArgumentException.class, () -> {
            tson.toJson(Double.NaN);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            tson.toJson(Double.POSITIVE_INFINITY);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            tson.toJson(Double.NEGATIVE_INFINITY);
        });
    }

    @Test
    void charToJson() {
        Tson tson = new Tson();

        assertEquals("\"a\"", tson.toJson('a'));

        assertEquals("\"\\\"\"", tson.toJson('"')); // Quotation mark \"
        assertEquals("\"\\\\\"", tson.toJson('\\')); // Reverse solidus \\
        assertEquals("\"\\b\"", tson.toJson('\b')); // Backspace \b
        assertEquals("\"\\f\"", tson.toJson('\f')); // Formfeed \f
        assertEquals("\"\\n\"", tson.toJson('\n')); // Linefeed \n
        assertEquals("\"\\r\"", tson.toJson('\r')); // Carriage return \r
        assertEquals("\"\\t\"", tson.toJson('\t')); // Horizontal tab \t
    }

    @Test
    void byteArrayToJson() {
        Tson tson = new Tson();

        assertEquals("[]", tson.toJson(new byte[] {}));
        assertEquals("[100,-100,0]", tson.toJson(new byte[] { 100, -100, 0 }));

        assertEquals("[]", tson.toJson(new Byte[] {}));
        assertEquals("[100,-100,0,null]", tson.toJson(new Byte[] { 100, -100, 0, null }));
    }

    @Test
    void shortArrayToJson() {
        Tson tson = new Tson();

        assertEquals("[]", tson.toJson(new short[] {}));
        assertEquals("[1000,-1000,0]", tson.toJson(new short[] { 1000, -1000, 0 }));

        assertEquals("[]", tson.toJson(new Short[] {}));
        assertEquals("[1000,-1000,0,null]", tson.toJson(new Short[] { 1000, -1000, 0, null }));
    }

    @Test
    void intArrayToJson() {
        Tson tson = new Tson();

        assertEquals("[]", tson.toJson(new int[] {}));
        assertEquals("[100000,-100000,0]", tson.toJson(new int[] { 100000, -100000, 0 }));

        assertEquals("[]", tson.toJson(new Integer[] {}));
        assertEquals("[100000,-100000,0,null]", tson.toJson(new Integer[] { 100000, -100000, 0, null }));
    }

    @Test
    void longArrayToJson() {
        Tson tson = new Tson();

        assertEquals("[]", tson.toJson(new long[] {}));
        assertEquals("[1000000000000,-1000000000000,0]",
                tson.toJson(new long[] { 1000000000000L, -1000000000000L, 0L }));

        assertEquals("[]", tson.toJson(new Long[] {}));
        assertEquals("[1000000000000,-1000000000000,0,null]",
                tson.toJson(new Long[] { 1000000000000L, -1000000000000L, 0L, null }));
    }

    @Test
    void booleanArrayToJson() {
        Tson tson = new Tson();

        assertEquals("[]", tson.toJson(new boolean[] {}));
        assertEquals("[true,false,true]", tson.toJson(new boolean[] { true, false, true }));

        assertEquals("[]", tson.toJson(new Boolean[] {}));
        assertEquals("[true,false,true,null]", tson.toJson(new Boolean[] { true, false, true, null }));
    }

    @Test
    void floatArrayToJson() {
        Tson tson = new Tson();

        assertEquals("[]", tson.toJson(new float[] {}));
        assertEquals("[2.71828,-2.71828,1.0E10,-1.0E10]",
                tson.toJson(new float[] { 2.71828f, -2.71828f, 1E10f, -1E10f }));

        assertEquals("[]", tson.toJson(new Float[] {}));
        assertEquals("[2.71828,-2.71828,1.0E10,-1.0E10,null]",
                tson.toJson(new Float[] { 2.71828f, -2.71828f, 1E10f, -1E10f, null }));
    }

    @Test
    void doubleArrayToJson() {
        Tson tson = new Tson();

        assertEquals("[]", tson.toJson(new double[] {}));
        assertEquals("[2.718281828459045,-2.718281828459045,1.0E100,-1.0E100]",
                tson.toJson(new double[] { 2.718281828459045, -2.718281828459045, 1E100, -1E100 }));

        assertEquals("[]", tson.toJson(new Double[] {}));
        assertEquals("[2.718281828459045,-2.718281828459045,1.0E100,-1.0E100,null]",
                tson.toJson(new Double[] { 2.718281828459045, -2.718281828459045, 1E100, -1E100, null }));
    }

    @Test
    void charArrayToJson() {
        Tson tson = new Tson();

        assertEquals("[]", tson.toJson(new char[] {}));
        assertEquals("[\"a\",\"\\\"\",\"\\\\\",\"\\b\",\"\\f\",\"\\n\",\"\\r\",\"\\t\"]",
                tson.toJson(new char[] { 'a', '"', '\\', '\b', '\f', '\n', '\r', '\t' }));

        assertEquals("[]", tson.toJson(new Character[] {}));
        assertEquals("[\"a\",\"\\\"\",\"\\\\\",\"\\b\",\"\\f\",\"\\n\",\"\\r\",\"\\t\",null]",
                tson.toJson(new Character[] { 'a', '"', '\\', '\b', '\f', '\n', '\r', '\t', null }));
    }

    @Test
    void int2DArrayToJson() {
        Tson tson = new Tson();

        assertEquals("[]", tson.toJson(new int[][] {}));
        assertEquals("[[],[]]", tson.toJson(new int[][] { {}, {} }));
        assertEquals("[[100000,-100000,0],[1,-1,0]]",
                tson.toJson(new int[][] { { 100000, -100000, 0 }, { 1, -1, 0 } }));
    }

    @Test
    void integerWrapper2DArrayToJson() {
        Tson tson = new Tson();

        assertEquals("[]", tson.toJson(new Integer[][] {}));
        assertEquals("[[],[]]", tson.toJson(new Integer[][] { {}, {} }));
        assertEquals("[[100000,-100000,0],[1,-1,0]]",
                tson.toJson(new Integer[][] { { 100000, -100000, 0 }, { 1, -1, 0 } }));

        assertEquals("[[100000,null,0],[null,-1,0]]",
                tson.toJson(new Integer[][] { { 100000, null, 0 }, { null, -1, 0 } }));
    }
}
