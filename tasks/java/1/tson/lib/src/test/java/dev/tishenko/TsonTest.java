package dev.tishenko;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TsonTest {
    @Test
    void nullToJson() {
        Tson tson = new Tson();

        assertEquals("null", tson.toJson(null));
    }
}
