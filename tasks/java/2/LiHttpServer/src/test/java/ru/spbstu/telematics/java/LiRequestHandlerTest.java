package ru.spbstu.telematics.java;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.*;

class LiRequestHandlerTest {
    @Test
    void testParseSimpleGetRequest() throws Exception {
        String request = "GET / HTTP/1.1\r\nHost: localhost\r\nTestHeader: testh\r\n\r\n";
        BufferedReader reader = createFakeRequest(request);

        LiRequestHandler handler = new LiRequestHandler(null, null);
        LiHttpRequest httpRequest = handler.parseRequest(reader);

        assertEquals("GET", httpRequest.getMethod());
        assertEquals("/", httpRequest.getPath());
        assertEquals("HTTP/1.1", httpRequest.getProtocol());
        assertEquals("localhost", httpRequest.getHeader("host"));
        assertEquals("testh", httpRequest.getHeader("testheader"));
    }

    @Test
    void testParseRequestWithBody() throws Exception {
        String request = "POST /submit HTTP/1.1\r\n" +
                "Content-Type: application/json\r\n" +
                "Content-Length: 15\r\n\r\n" +
                "{\"key\":\"value\"}";
        BufferedReader reader = createFakeRequest(request);
        LiRequestHandler handler = new LiRequestHandler(null, null);
        LiHttpRequest httpRequest = handler.parseRequest(reader);
        assertEquals("POST", httpRequest.getMethod());
        assertEquals("application/json", httpRequest.getHeader("content-type"));
        assertEquals("{\"key\":\"value\"}", httpRequest.getBody());
    }


    @Test
    void testParseInvalidRequest() {
        String invalidRequest = "INVALID REQUEST LINE\r\n";
        BufferedReader reader = createFakeRequest(invalidRequest);
        LiRequestHandler handler = new LiRequestHandler(null, null);
        assertThrows(IllegalArgumentException.class, () -> handler.parseRequest(reader));
    }


    private BufferedReader createFakeRequest(String input) {
        return new BufferedReader(new InputStreamReader(
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))));
    }
}