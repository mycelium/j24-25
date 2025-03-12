package ru.lab.json_parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.lab.json_parser.test_entities.Category;
import ru.lab.json_parser.test_entities.Image;
import ru.lab.json_parser.test_entities.ProductCard;
import java.util.*;

public class JSONParserTest {

    private final String JSON = "{\"image\":{\"width\":200,\"url\":\"img/01.png\",\"height\":200},\"thumbnail\":{\"width\":200,\"url\":\"images/thumbnails/01.png\",\"height\":200},\"name\":\"Vanilla Cone\",\"id\":1,\"categories\":[{\"categoryName\":\"Example\",\"categoryID\":2}],\"type\":\"icecream\"}";

    private final ProductCard testObject = new ProductCard(
                1,
                "icecream",
                "Vanilla Cone",
                new Image("img/01.png", 200, 200),
                new Image("images/thumbnails/01.png", 200, 200),
                List.of(new Category(2, "Example"))
            );

    private final Map<String, Object> testMap = Map.of(
        "id", 1,
            "type", "icecream",
            "name", "Vanilla Cone",
            "image", Map.of(
                    "url", "img/01.png",
                    "width", 200,
                    "height", 200
            ),
            "thumbnail", Map.of(
                    "url", "images/thumbnails/01.png",
                    "width", 200,
                    "height", 200
            ),
            "categories", List.of(Map.of(
                    "categoryID", 2,
                    "categoryName", "Example"
            ))
    );

    @Test
    public void convertEntityToJSON(){
        Assertions.assertEquals(JSON, JSONParser.convertEntityToJSON(testObject));
    }

    @Test
    public void parsingJSONToEntity(){
        Assertions.assertEquals(JSONParser.readJsonToEntity(JSON, ProductCard.class), testObject);
    }

    @Test
    public void parsingJSONToMap(){
        Assertions.assertEquals(JSONParser.readJsonToMap(JSON), testMap);
    }
}
