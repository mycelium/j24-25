package dev.tishenko;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

public class DynamicFromJsonTest {
    @Test
    void testDynamicNullFromJson() {
        Tson tson = new Tson();
        String json = "null";
        Object result = tson.fromJson(json, Object.class);
        assertNull(result);
    }

    @Test
    void testDynamicMapFromJson() {
        Tson tson = new Tson();

        String json = """
                    {
                        "number": 123.45,
                        "flag": true,
                        "nestedList": [1, "two", false]
                    }
                """;

        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) tson.fromJson(json, Object.class);

        assertEquals(123.45, map.get("number"));
        assertEquals(true, map.get("flag"));

        List<?> nestedList = (List<?>) map.get("nestedList");
        assertEquals(1L, nestedList.get(0));
        assertEquals("two", nestedList.get(1));
        assertEquals(false, nestedList.get(2));
    }
}
