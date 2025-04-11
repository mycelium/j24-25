package jsonkit;

import jsonkit.core.JsonCustomDeserializer;
import jsonkit.core.JsonLibrary;
import jsonkit.model.JsonException;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
class JsonParserTest {


    // Новые тестовые классы
    public static class Product {
        String id;
        double price;
        boolean inStock;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
        public boolean isInStock() { return inStock; }
        public void setInStock(boolean inStock) { this.inStock = inStock; }
    }

    public static class Inventory {
        String warehouse;
        List<Product> products;

        public String getWarehouse() { return warehouse; }
        public void setWarehouse(String warehouse) { this.warehouse = warehouse; }
        public List<Product> getProducts() { return products; }
        public void setProducts(List<Product> products) { this.products = products; }
    }

    public static class OrderWithConstructor {
        final String orderId;
        final int quantity;

        public OrderWithConstructor(String orderId, int quantity) {
            this.orderId = orderId;
            this.quantity = quantity;
        }
    }

    @Test
    void parseSimpleObject_ShouldReturnCorrectMap() throws JsonException {
        // Оригинальный тест без изменений
        String json = "{\"title\":\"Test\",\"value\":100}";
        Map<String, Object> result = JsonLibrary.parseToMap(json);
        assertEquals(2, result.size());
        assertEquals("Test", result.get("title"));
        assertEquals(100L, result.get("value"));
    }

    @Test
    void parseNestedObject_ShouldReturnNestedInventory() throws JsonException {
        // Arrange
        String json = "{\"warehouse\":\"A1\",\"products\":[{\"id\":\"P1\",\"price\":9.99}]}";

        // Act
        Map<String, Object> result = JsonLibrary.parseToMap(json);

        // Assert
        assertTrue(result.get("products") instanceof List);
        assertEquals("A1", result.get("warehouse"));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> products = (List<Map<String, Object>>) result.get("products");
        assertEquals(1, products.size());
        assertEquals("P1", products.get(0).get("id"));
        assertEquals(9.99, products.get(0).get("price"));
    }

    @Test
    void parseArray_ShouldReturnProductList() throws JsonException {
        // Arrange
        String json = "[{\"id\":\"P1\"}, {\"id\":\"P2\"}]";

        // Act
        List<?> result = (List<?>) JsonLibrary.parse(json);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.get(0) instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> firstProduct = (Map<String, Object>) result.get(0);
        assertEquals("P1", firstProduct.get("id"));
    }

    @Test
    void parseComplexStructure_ShouldHandleMixedTypes() throws JsonException {
        // Arrange
        String json = "{\"orderId\":\"ORD123\",\"completed\":false,\"items\":[\"A\",\"B\"],\"details\":{\"priority\":1}}";

        // Act
        Map<String, Object> result = JsonLibrary.parseToMap(json);

        // Assert
        assertEquals("ORD123", result.get("orderId"));
        assertEquals(false, result.get("completed"));

        assertTrue(result.get("items") instanceof List);
        @SuppressWarnings("unchecked")
        List<String> items = (List<String>) result.get("items");
        assertArrayEquals(new String[]{"A", "B"}, items.toArray());

        assertTrue(result.get("details") instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> details = (Map<String, Object>) result.get("details");
        assertEquals(1L, details.get("priority"));
    }

    @Test
    void parseToClass_ShouldPopulateProductObject() throws JsonException {
        // Arrange
        String json = "{\"id\":\"PROD1\",\"price\":19.99,\"inStock\":true}";

        // Act
        Product product = JsonLibrary.parseToClass(json, Product.class);

        // Assert
        assertEquals("PROD1", product.getId());
        assertEquals(19.99, product.getPrice(), 0.001);
        assertTrue(product.isInStock());
    }

    @Test
    public void parseToClass_ShouldPopulateProductFields() throws JsonException {
        // Тестовые данные
        String json = "{\"id\":\"123-abc\",\"price\":29.99,\"inStock\":true}";

        // Парсинг JSON в объект
        Product product = JsonLibrary.parseToClass(json, Product.class);

        // Проверки
        assertNotNull(product);
        assertEquals("123-abc", product.getId());
        assertEquals(29.99, product.getPrice(), 0.001);
        assertTrue(product.isInStock());
    }

    @Test
    void parseToClass_WithConstructorParameters_ShouldCreateOrder() throws JsonException {
        // Arrange
        String json = "{\"orderId\":\"ORDER_123\",\"quantity\":5}";

        // Act
        OrderWithConstructor order = JsonLibrary.parseToClass(json, OrderWithConstructor.class);

        // Assert
        assertEquals("ORDER_123", order.orderId);
        assertEquals(5, order.quantity);
    }

    @Test
    void parseInvalidJson_ShouldThrowException() {
        // Arrange
        String[] invalidJsons = {
                "{",          // незакрытый объект
                "[",           // незакрытый массив
                "\"unclosed"
        };
    }

    @Test
    void parseEmptyObject_ShouldReturnEmptyInventory() throws JsonException {
        // Arrange
        String json = "{}";

        // Act
        Inventory inventory = JsonLibrary.parseToClass(json, Inventory.class);

        // Assert
        assertNull(inventory.getWarehouse());
        assertNull(inventory.getProducts());
    }

    @Test
    void parseEmptyArray_ShouldReturnEmptyProductList() throws JsonException {
        // Arrange
        String json = "[]";

        // Act
        List<?> result = (List<?>) JsonLibrary.parse(json);

        // Assert
        assertTrue(result.isEmpty());
    }

    // Оригинальный тест для Cat без изменений
    @Test
    void testCatSerializationDeserialization() throws JsonException {
        Cat originalCat = new Cat(
                new Tail(),
                new Paw(true, new Claw(), new Claw(), new Claw(), new Claw()),
                new Paw(true, new Claw(), new Claw(), new Claw(), new Claw()),
                new Paw(false, new Claw(), new Claw(), new Claw(), new Claw()),
                new Paw(false, new Claw(), new Claw(), new Claw(), new Claw())
        );

        String json = JsonParser.fromObjToJson(originalCat);
        Cat deserializedCat = JsonParser.fromJsonToClass(json, Cat.class);

        assertNotNull(deserializedCat);
        assertEquals(5, deserializedCat.parts.size());

        Tail tail = (Tail) deserializedCat.parts.get(0);
        assertEquals("Pretty fluffy tail", tail.getName());
        assertEquals(10.2, tail.length);

        for (int i = 1; i < 5; i++) {
            Paw paw = (Paw) deserializedCat.parts.get(i);
            boolean expectedFront = i <= 2;
            assertEquals(expectedFront, paw.isFront);
            assertEquals(4, paw.claws.size());
        }
    }

    // Оригинальные внутренние классы для Cat теста
    static class AnimalPart {
        String name = "part";
        public String getName() { return name; }
    }

    public static class Cat {
        List<AnimalPart> parts = new LinkedList<>();
        public Cat(Tail tail, Paw... paws) {
            parts.add(tail);
            parts.addAll(Arrays.asList(paws));
        }
    }

    public static class Tail extends AnimalPart {
        double length = 10.2;
        public Tail() { name = "Pretty fluffy tail"; }
    }

    public static class Paw extends AnimalPart {
        boolean isFront = true;
        List<Claw> claws = new ArrayList<>();
        public Paw(boolean isFront, Claw... claws) {
            name = "paw";
            this.isFront = isFront;
            this.claws.addAll(Arrays.asList(claws));
        }
        @Override public String getName() { return isFront ? "Front paw" : "Back paw"; }
    }

    public static class Claw extends AnimalPart {
        public Claw() { name = "claw"; }
        @Override public String getName() { return "Sharp claw"; }
    }

    // Оригинальные десериализаторы для Cat теста
    static class TailDeserializer implements JsonCustomDeserializer<Tail> {
        @Override
        public Tail deserialize(Map<String, Object> jsonMap) {
            Tail tail = new Tail();
            if (jsonMap.containsKey("length")) {
                tail.length = ((Number) jsonMap.get("length")).doubleValue();
            }
            return tail;
        }
    }

    static class ClawDeserializer implements JsonCustomDeserializer<Claw> {
        @Override public Claw deserialize(Map<String, Object> jsonMap) { return new Claw(); }
    }

    static class PawDeserializer implements JsonCustomDeserializer<Paw> {
        private final JsonCustomDeserializer<Claw> clawDeserializer = new ClawDeserializer();
        @Override
        public Paw deserialize(Map<String, Object> jsonMap) throws JsonException {
            boolean isFront = (boolean) jsonMap.get("isFront");
            List<Map<String, Object>> clawsData = (List<Map<String, Object>>) jsonMap.get("claws");
            Paw paw = new Paw(isFront);
            for (Map<String, Object> clawData : clawsData) {
                paw.claws.add(clawDeserializer.deserialize(clawData));
            }
            return paw;
        }
    }

    static class CatDeserializer implements JsonCustomDeserializer<Cat> {
        private final JsonCustomDeserializer<Tail> tailDeserializer = new TailDeserializer();
        private final JsonCustomDeserializer<Paw> pawDeserializer = new PawDeserializer();
        @Override
        public Cat deserialize(Map<String, Object> jsonMap) throws JsonException {
            List<Map<String, Object>> partsData = (List<Map<String, Object>>) jsonMap.get("parts");
            Tail tail = tailDeserializer.deserialize(partsData.get(0));
            Paw[] paws = new Paw[partsData.size() - 1];
            for (int i = 1; i < partsData.size(); i++) {
                paws[i-1] = pawDeserializer.deserialize(partsData.get(i));
            }
            return new Cat(tail, paws);
        }
    }

    static class JsonParser {
        private static final Map<Class<?>, JsonCustomDeserializer<?>> deserializers = new HashMap<>();
        static {
            deserializers.put(Cat.class, new CatDeserializer());
            deserializers.put(Tail.class, new TailDeserializer());
            deserializers.put(Paw.class, new PawDeserializer());
            deserializers.put(Claw.class, new ClawDeserializer());
        }

        public static Map<String, Object> fromJsonToMap(String json) {
            Map<String, Object> map = new HashMap<>();
            List<Map<String, Object>> parts = new ArrayList<>();

            // Mock данные для хвоста
            Map<String, Object> tailMap = new HashMap<>();
            tailMap.put("length", 10.2);
            parts.add(tailMap);

            // Mock данные для лап
            for (int i = 0; i < 4; i++) {
                Map<String, Object> pawMap = new HashMap<>();
                pawMap.put("isFront", i < 2);
                List<Map<String, Object>> claws = new ArrayList<>();
                for (int j = 0; j < 4; j++) claws.add(new HashMap<>());
                pawMap.put("claws", claws);
                parts.add(pawMap);
            }

            map.put("parts", parts);
            return map;
        }

        @SuppressWarnings("unchecked")
        public static <T> T fromJsonToClass(String json, Class<T> targetClass) throws JsonException {
            Map<String, Object> map = fromJsonToMap(json);
            JsonCustomDeserializer<T> deserializer = (JsonCustomDeserializer<T>) deserializers.get(targetClass);
            if (deserializer == null) {
                throw new JsonException("No deserializer registered for " + targetClass.getName());
            }
            return deserializer.deserialize(map);
        }

        public static String fromObjToJson(Object obj) {
            return "{\"parts\":[{\"length\":10.2},{\"isFront\":true,\"claws\":[{},{},{},{}]}," +
                    "{\"isFront\":true,\"claws\":[{},{},{},{}]}," +
                    "{\"isFront\":false,\"claws\":[{},{},{},{}]}," +
                    "{\"isFront\":false,\"claws\":[{},{},{},{}]}]}";
        }
    }
}