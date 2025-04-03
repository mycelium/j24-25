package ru.spbstu.telematics.java;

import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


import static org.junit.jupiter.api.Assertions.*;


class LiHttpResponseTest {

    @Test
    void testTextResponse() {
        LiHttpResponse response = new LiHttpResponse(200, "Hello");
        assertArrayEquals("Hello".getBytes(), response.getBody());
        assertEquals("text/plain; charset=UTF-8", response.getResponseHeader("Content-Type"));
    }


    @Test
    void testJsonResponse() {
        LiHttpResponse response = LiHttpResponse.json("{\"key\":\"value\"}");
        assertArrayEquals("{\"key\":\"value\"}".getBytes(), response.getBody());
        assertEquals("application/json; charset=UTF-8", response.getResponseHeader("Content-Type"));
    }

    @Test
    void testFileResponse() throws Exception {
        Path testFile = Paths.get("C:/Users/lizab/JavaProjects/6term/j24-25/tasks/java/2/LiHttpServer/src/test/java/ru/spbstu/telematics/java/text.txt");
        Files.write(testFile, "test content".getBytes());
        LiHttpResponse response = LiHttpResponse.file(testFile);
        assertArrayEquals("test content".getBytes(), response.getBody());
        assertTrue(response.getResponseHeader("Content-Type").startsWith("text/plain"));
        Files.deleteIfExists(testFile);
    }

    @Test
    void testStatusCode() {
        LiHttpResponse response = new LiHttpResponse(404, "Not found")
                .setStatusCode(200);
        assertEquals(200, response.getStatusCode());
    }

    @Test
    void testToByteArray() {
        LiHttpResponse response = new LiHttpResponse(200, "Hello");
        byte[] bytes = response.LiHttpResponseToByteArray();
        assertTrue(bytes.length > 0);
        assertTrue(new String(bytes).contains("HTTP/1.1 200 OK"));
    }
}
