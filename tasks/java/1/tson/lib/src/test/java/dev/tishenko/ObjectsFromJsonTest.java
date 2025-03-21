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
}
