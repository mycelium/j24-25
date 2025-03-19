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
    void doubleToJson() {
        Tson tson = new Tson();

        assertEquals("2.718281828459045", tson.toJson(2.718281828459045));
        assertEquals("-2.718281828459045", tson.toJson(-2.718281828459045));
        assertEquals("1.0E100", tson.toJson(1E100));
        assertEquals("-1.0E100", tson.toJson(-1E100));
        assertEquals("2.718281828459045", tson.toJson(Double.valueOf(2.718281828459045)));
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
}
