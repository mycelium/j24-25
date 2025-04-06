package lab1.CatSON;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ReadCollectionsToJSONTests {
    @Test
    void emptyCollectionOfIntegersToJson() {
        CatSON CatSON = new CatSON();

        Collection<Integer> integers = List.of();

        assertEquals("[]", CatSON.toJson(integers));
    }

    @Test
    void collectionOfIntegersToJson() {
        CatSON CatSON = new CatSON();

        Collection<Integer> ints = Arrays.asList(-1, 0, 1, 2, 3);

        assertEquals("[-1,0,1,2,3]", CatSON.toJson(ints));
    }

    @Test
    void listOfStringsToJson() {
        CatSON CatSON = new CatSON();
        List<String> strings = Arrays.asList("apple", "banana", "cherry");
        assertEquals("[\"apple\",\"banana\",\"cherry\"]", CatSON.toJson(strings));
    }

    @Test
    void setOfBooleansToJson() {
        CatSON CatSON = new CatSON();
        Set<Boolean> bools = new HashSet<>(Arrays.asList(true, false));

        String result = CatSON.toJson(bools);
        assertTrue(result.contains("[true,false]") || result.contains("[false,true]"));
    }

    @Test
    void queueOfDoublesToJson() {
        CatSON CatSON = new CatSON();
        Queue<Double> doubles = new LinkedList<>(Arrays.asList(1.1, 2.2, 3.3));
        assertEquals("[1.1,2.2,3.3]", CatSON.toJson(doubles));
    }

    @Test
    void nestedCollectionsToJson() {
        CatSON CatSON = new CatSON();
        List<List<Integer>> nestedList = Arrays.asList(
                Arrays.asList(1, 2, 3),
                Arrays.asList(4, 5, 6));
        assertEquals("[[1,2,3],[4,5,6]]", CatSON.toJson(nestedList));
    }

    @Test
    void complexNestedCollectionToJson() {
        CatSON CatSON = new CatSON();
        List<Object> complexList = Arrays.asList(
                Arrays.asList(1, 2),
                new HashSet<>(Arrays.asList("a", "b")),
                Arrays.asList(true, false));

        String result = CatSON.toJson(complexList);
        assertTrue(result.contains("[1,2]"));
        assertTrue(result.contains("[true,false]") || result.contains("[false,true]"));
        assertTrue(result.contains("[\"a\",\"b\"]") || result.contains("[\"b\",\"a\"]"));
    }
}