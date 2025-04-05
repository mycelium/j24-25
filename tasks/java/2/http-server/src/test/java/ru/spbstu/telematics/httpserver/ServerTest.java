package ru.spbstu.telematics.httpserver;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServerTest {
    @Test
    void testRequest() {
        String rawRequest =
                "POST /submit HTTP/1.1\r\n" +
                "Host: localhost\r\n" +
                "Content-Type: text\r\n" +
                "Content-Length: 2\r\n" +
                "\r\n" +
                "Hello World";

        var request = HttpRequest.parse(rawRequest);
        assertEquals("POST", request.getMethod());
        assertEquals("Hello World", request.getBody());
    }
}
