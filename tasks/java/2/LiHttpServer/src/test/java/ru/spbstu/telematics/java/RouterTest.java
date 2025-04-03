package ru.spbstu.telematics.java;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;


class RouterTest {
    private Router router;

    @BeforeEach
    void setUp() {
        router = new Router();
        router.addDefaultRoutes();
    }

    @Test
    void testPingRoute() {
        LiHttpRequest request = new LiHttpRequest("GET", "/ping", "HTTP/1.1", Map.of(), null);
        LiHttpResponse response = router.handle(request);
        assertEquals(200, response.getStatusCode());
        assertEquals("Сервер запущен", new String(response.getBody()));
    }

    @Test
    void testNotFoundRoute() {
        LiHttpRequest request = new LiHttpRequest("GET", "/nonexistent", "HTTP/1.1", Map.of(), null);
        LiHttpResponse response = router.handle(request);
        assertEquals(404, response.getStatusCode());
    }

    @Test
    void testJsonRoute() {
        LiHttpRequest request = new LiHttpRequest("GET", "/users", "HTTP/1.1", Map.of(), null);
        LiHttpResponse response = router.handle(request);
        assertEquals(200, response.getStatusCode());
        assertEquals("application/json", response.getHeader("Content-Type").split(";")[0]);
        assertTrue(new String(response.getBody()).contains("Frank"));
    }

    @Test
    void testErrorHandling() {
        router.addRoute("GET", "/error", req -> {
            throw new RuntimeException("Test error");
        });

        LiHttpRequest request = new LiHttpRequest("GET", "/error", "HTTP/1.1", Map.of(), null);
        LiHttpResponse response = router.handle(request);
        assertEquals(500, response.getStatusCode());
        assertTrue(new String(response.getBody()).contains("Internal Server Error"));
    }
}