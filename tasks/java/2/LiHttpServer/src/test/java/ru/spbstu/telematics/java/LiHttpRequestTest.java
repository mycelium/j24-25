package ru.spbstu.telematics.java;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.channels.SocketChannel;
import java.util.Map;

class LiHttpRequestTest {
    @Test
    void testValidRequest() {
        Map<String, String> headers = Map.of("Content-Type", "application/json");
        LiHttpRequest request = new LiHttpRequest("GET", "/path", "HTTP/1.1", headers, "body");

        assertEquals("GET", request.getMethod());
        assertEquals("/path", request.getPath());
        assertEquals("HTTP/1.1", request.getProtocol());
        assertEquals("application/json", request.getHeader("content-type"));
        assertEquals("body", request.getBody());
    }

    @Test
    void testInvalidMethod() {
        assertThrows(IllegalArgumentException.class, () ->
                new LiHttpRequest("INVALID", "/", "HTTP/1.1", Map.of(), null));
    }

    @Test
    void testInvalidPath() {
        assertThrows(IllegalArgumentException.class, () ->
                new LiHttpRequest("GET", null, "HTTP/1.1", Map.of(), null));

        assertThrows(IllegalArgumentException.class, () ->
                new LiHttpRequest("GET", "no-slash", "HTTP/1.1", Map.of(), null));
    }

    @Test
    void testInvalidProtocol() {
        assertThrows(IllegalArgumentException.class, () ->
                new LiHttpRequest("GET", "/", "HTTP/2.0", Map.of(), null));
    }

    @Test
    void testHeaderNormalization() {
        LiHttpRequest request = new LiHttpRequest("GET", "/", "HTTP/1.1",
                Map.of("Content-Type", "APPLICATION/JSON"), null);

        assertEquals("application/json", request.getHeader("CONTENT-TYPE"));
    }

    @Test
    void testInvalidHeader() {
        assertThrows(IllegalArgumentException.class, () ->
                new LiHttpRequest("GET", "/", "HTTP/1.1",
                        Map.of("", ""), null));
    }
}