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
}
