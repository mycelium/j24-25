package ru.spbstu.telematics.java;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static ru.spbstu.telematics.java.LiServerConfig.defaultConfig;


class LiServerConfigTest {
    @Test
    void testValidConfig() {
        LiServerConfig config = new LiServerConfig("localhost", 8080, 10, false);
        assertEquals("localhost", config.getHost());
        assertEquals(8080, config.getPort());
        assertEquals(10, config.getThreadNum());
        assertFalse(config.isVirtual());
    }

    @Test
    void testInvalidPort() {
        assertThrows(IllegalArgumentException.class, () ->
                new LiServerConfig("localhost", 0, 10, false));
    }

    @Test
    void testDefaultConfig() {
        LiServerConfig config = defaultConfig();
        assertEquals("localhost", config.getHost());
        assertEquals(8080, config.getPort());
    }
}