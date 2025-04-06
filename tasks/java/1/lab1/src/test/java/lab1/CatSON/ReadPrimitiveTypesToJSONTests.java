package lab1.CatSON;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ReadPrimitiveTypesToJSONTests{
    @Test
    void byteToJson() {
        CatSON CatSON = new CatSON();

        assertEquals("100", CatSON.toJson((byte) 100));
        assertEquals("-100", CatSON.toJson((byte) -100));
        assertEquals("0", CatSON.toJson((byte) 0));
        assertEquals("100", CatSON.toJson(Byte.valueOf((byte) 100)));
    }

    @Test
    void shortToJson() {
        CatSON CatSON = new CatSON();

        assertEquals("1000", CatSON.toJson((short) 1000));
        assertEquals("-1000", CatSON.toJson((short) -1000));
        assertEquals("0", CatSON.toJson((short) 0));
        assertEquals("1000", CatSON.toJson(Short.valueOf((short) 1000)));
    }

    @Test
    void integerToJson() {
        CatSON CatSON = new CatSON();

        assertEquals("100000", CatSON.toJson(100000));
        assertEquals("-100000", CatSON.toJson(-100000));
        assertEquals("0", CatSON.toJson(0));
        assertEquals("100000", CatSON.toJson(Integer.valueOf(100000)));
    }

    @Test
    void longToJson() {
        CatSON CatSON = new CatSON();

        assertEquals("1000000000000", CatSON.toJson(1000000000000L));
        assertEquals("-1000000000000", CatSON.toJson(-1000000000000L));
        assertEquals("0", CatSON.toJson(0L));
        assertEquals("1000000000000", CatSON.toJson(Long.valueOf(1000000000000L)));
    }

    @Test
    void booleanToJson() {
        CatSON CatSON = new CatSON();

        assertEquals("true", CatSON.toJson(true));
        assertEquals("false", CatSON.toJson(false));
        assertEquals("true", CatSON.toJson(Boolean.valueOf(true)));
        assertEquals("false", CatSON.toJson(Boolean.valueOf(false)));
    }

    @Test
    void floatToJson() {
        CatSON CatSON = new CatSON();

        assertEquals("2.71828", CatSON.toJson(2.71828f));
        assertEquals("-2.71828", CatSON.toJson(-2.71828f));
        assertEquals("1.0E10", CatSON.toJson(1E10f));
        assertEquals("-1.0E10", CatSON.toJson(-1E10f));
        assertEquals("2.71828", CatSON.toJson(Float.valueOf(2.71828f)));
    }

    @Test
    void floatNanInfinityToJson() {
        CatSON CatSON = new CatSON();

        assertThrows(IllegalArgumentException.class, () -> {
            CatSON.toJson(Float.NaN);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            CatSON.toJson(Float.POSITIVE_INFINITY);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            CatSON.toJson(Float.NEGATIVE_INFINITY);
        });
    }

    @Test
    void doubleToJson() {
        CatSON CatSON = new CatSON();

        assertEquals("2.718281828459045", CatSON.toJson(2.718281828459045));
        assertEquals("-2.718281828459045", CatSON.toJson(-2.718281828459045));
        assertEquals("1.0E100", CatSON.toJson(1E100));
        assertEquals("-1.0E100", CatSON.toJson(-1E100));
        assertEquals("2.718281828459045", CatSON.toJson(Double.valueOf(2.718281828459045)));
    }

    @Test
    void doubleNanInfinityToJson() {
        CatSON CatSON = new CatSON();

        assertThrows(IllegalArgumentException.class, () -> {
            CatSON.toJson(Double.NaN);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            CatSON.toJson(Double.POSITIVE_INFINITY);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            CatSON.toJson(Double.NEGATIVE_INFINITY);
        });
    }

    @Test
    void charToJson() {
        CatSON CatSON = new CatSON();

        assertEquals("\"a\"", CatSON.toJson('a'));

        assertEquals("\"\\\"\"", CatSON.toJson('"')); // Quotation mark \"
        assertEquals("\"\\\\\"", CatSON.toJson('\\')); // Reverse solidus \\
        assertEquals("\"\\b\"", CatSON.toJson('\b')); // Backspace \b
        assertEquals("\"\\f\"", CatSON.toJson('\f')); // Formfeed \f
        assertEquals("\"\\n\"", CatSON.toJson('\n')); // Linefeed \n
        assertEquals("\"\\r\"", CatSON.toJson('\r')); // Carriage return \r
        assertEquals("\"\\t\"", CatSON.toJson('\t')); // Horizontal tab \t
    }

    @Test
    void byteArrayToJson() {
        CatSON CatSON = new CatSON();

        assertEquals("[]", CatSON.toJson(new byte[] {}));
        assertEquals("[100,-100,0]", CatSON.toJson(new byte[] { 100, -100, 0 }));

        assertEquals("[]", CatSON.toJson(new Byte[] {}));
        assertEquals("[100,-100,0,null]", CatSON.toJson(new Byte[] { 100, -100, 0, null }));
    }

    @Test
    void shortArrayToJson() {
        CatSON CatSON = new CatSON();

        assertEquals("[]", CatSON.toJson(new short[] {}));
        assertEquals("[1000,-1000,0]", CatSON.toJson(new short[] { 1000, -1000, 0 }));

        assertEquals("[]", CatSON.toJson(new Short[] {}));
        assertEquals("[1000,-1000,0,null]", CatSON.toJson(new Short[] { 1000, -1000, 0, null }));
    }

    @Test
    void intArrayToJson() {
        CatSON CatSON = new CatSON();

        assertEquals("[]", CatSON.toJson(new int[] {}));
        assertEquals("[100000,-100000,0]", CatSON.toJson(new int[] { 100000, -100000, 0 }));

        assertEquals("[]", CatSON.toJson(new Integer[] {}));
        assertEquals("[100000,-100000,0,null]", CatSON.toJson(new Integer[] { 100000, -100000, 0, null }));
    }

    @Test
    void longArrayToJson() {
        CatSON CatSON = new CatSON();

        assertEquals("[]", CatSON.toJson(new long[] {}));
        assertEquals("[1000000000000,-1000000000000,0]",
                CatSON.toJson(new long[] { 1000000000000L, -1000000000000L, 0L }));

        assertEquals("[]", CatSON.toJson(new Long[] {}));
        assertEquals("[1000000000000,-1000000000000,0,null]",
                CatSON.toJson(new Long[] { 1000000000000L, -1000000000000L, 0L, null }));
    }

    @Test
    void booleanArrayToJson() {
        CatSON CatSON = new CatSON();

        assertEquals("[]", CatSON.toJson(new boolean[] {}));
        assertEquals("[true,false,true]", CatSON.toJson(new boolean[] { true, false, true }));

        assertEquals("[]", CatSON.toJson(new Boolean[] {}));
        assertEquals("[true,false,true,null]", CatSON.toJson(new Boolean[] { true, false, true, null }));
    }

    @Test
    void floatArrayToJson() {
        CatSON CatSON = new CatSON();

        assertEquals("[]", CatSON.toJson(new float[] {}));
        assertEquals("[2.71828,-2.71828,1.0E10,-1.0E10]",
                CatSON.toJson(new float[] { 2.71828f, -2.71828f, 1E10f, -1E10f }));

        assertEquals("[]", CatSON.toJson(new Float[] {}));
        assertEquals("[2.71828,-2.71828,1.0E10,-1.0E10,null]",
                CatSON.toJson(new Float[] { 2.71828f, -2.71828f, 1E10f, -1E10f, null }));
    }

    @Test
    void doubleArrayToJson() {
        CatSON CatSON = new CatSON();

        assertEquals("[]", CatSON.toJson(new double[] {}));
        assertEquals("[2.718281828459045,-2.718281828459045,1.0E100,-1.0E100]",
                CatSON.toJson(new double[] { 2.718281828459045, -2.718281828459045, 1E100, -1E100 }));

        assertEquals("[]", CatSON.toJson(new Double[] {}));
        assertEquals("[2.718281828459045,-2.718281828459045,1.0E100,-1.0E100,null]",
                CatSON.toJson(new Double[] { 2.718281828459045, -2.718281828459045, 1E100, -1E100, null }));
    }

    @Test
    void charArrayToJson() {
        CatSON CatSON = new CatSON();

        assertEquals("[]", CatSON.toJson(new char[] {}));
        assertEquals("[\"a\",\"\\\"\",\"\\\\\",\"\\b\",\"\\f\",\"\\n\",\"\\r\",\"\\t\"]",
                CatSON.toJson(new char[] { 'a', '"', '\\', '\b', '\f', '\n', '\r', '\t' }));

        assertEquals("[]", CatSON.toJson(new Character[] {}));
        assertEquals("[\"a\",\"\\\"\",\"\\\\\",\"\\b\",\"\\f\",\"\\n\",\"\\r\",\"\\t\",null]",
                CatSON.toJson(new Character[] { 'a', '"', '\\', '\b', '\f', '\n', '\r', '\t', null }));
    }

    @Test
    void int2DArrayToJson() {
        CatSON CatSON = new CatSON();

        assertEquals("[]", CatSON.toJson(new int[][] {}));
        assertEquals("[[],[]]", CatSON.toJson(new int[][] { {}, {} }));
        assertEquals("[[100000,-100000,0],[1,-1,0]]",
                CatSON.toJson(new int[][] { { 100000, -100000, 0 }, { 1, -1, 0 } }));
    }

    @Test
    void integerWrapper2DArrayToJson() {
        CatSON CatSON = new CatSON();

        assertEquals("[]", CatSON.toJson(new Integer[][] {}));
        assertEquals("[[],[]]", CatSON.toJson(new Integer[][] { {}, {} }));
        assertEquals("[[100000,-100000,0],[1,-1,0]]",
                CatSON.toJson(new Integer[][] { { 100000, -100000, 0 }, { 1, -1, 0 } }));

        assertEquals("[[100000,null,0],[null,-1,0]]",
                CatSON.toJson(new Integer[][] { { 100000, null, 0 }, { null, -1, 0 } }));
    }
}