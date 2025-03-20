package dev.tishenko;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class CollectionsToJsonTest {
    @Test
    void emptyCollectionOfIntegersToJson() {
        Tson tson = new Tson();

        Collection<Integer> ints = Arrays.asList();

        assertEquals("[]", tson.toJson(ints));
    }

    @Test
    void collectionOfIntegersToJson() {
        Tson tson = new Tson();

        Collection<Integer> ints = Arrays.asList(-1, 0, 1, 2, 3);

        assertEquals("[-1,0,1,2,3]", tson.toJson(ints));
    }

    @Test
    void listOfStringsToJson() {
        Tson tson = new Tson();
        List<String> strings = Arrays.asList("apple", "banana", "cherry");
        assertEquals("[\"apple\",\"banana\",\"cherry\"]", tson.toJson(strings));
    }

    @Test
    void setOfBooleansToJson() {
        Tson tson = new Tson();
        Set<Boolean> bools = new HashSet<>(Arrays.asList(true, false));

        String result = tson.toJson(bools);
        assertTrue(result.contains("[true,false]") || result.contains("[false,true]"));
    }

    @Test
    void queueOfDoublesToJson() {
        Tson tson = new Tson();
        Queue<Double> doubles = new LinkedList<>(Arrays.asList(1.1, 2.2, 3.3));
        assertEquals("[1.1,2.2,3.3]", tson.toJson(doubles));
    }

    @Test
    void nestedCollectionsToJson() {
        Tson tson = new Tson();
        List<List<Integer>> nestedList = Arrays.asList(
                Arrays.asList(1, 2, 3),
                Arrays.asList(4, 5, 6));
        assertEquals("[[1,2,3],[4,5,6]]", tson.toJson(nestedList));
    }

    @Test
    void complexNestedCollectionToJson() {
        Tson tson = new Tson();
        List<Object> complexList = Arrays.asList(
                Arrays.asList(1, 2),
                new HashSet<>(Arrays.asList("a", "b")),
                Arrays.asList(true, false));

        String result = tson.toJson(complexList);
        assertTrue(result.contains("[1,2]"));
        assertTrue(result.contains("[true,false]") || result.contains("[false,true]"));
        assertTrue(result.contains("[\"a\",\"b\"]") || result.contains("[\"b\",\"a\"]"));
    }
}
