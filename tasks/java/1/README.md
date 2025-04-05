# JSON Parser

This library provides a JSON parser for Java. It supports converting Java objects to JSON strings and vice versa, as well as deserializing JSON in a Map<String, object>, and specific Java classes.
## Features

- **Convert Java objects to JSON strings**
    - Supports primitives, boxing types, `null`, arrays, and collections.
    - Handles nested objects and collections.

- **Convert JSON strings to Java objects**
    - Convert JSON to `Map<String, Object>`.
    - Convert JSON directly to Java objects using specified classes.

- **Deserialization with custom deserializer support**
    - Annotate classes with `@JsonDeserialize` to specify a custom deserializer for complex types.

## Basic Usage Examples

### 1. Deserialization: Convert JSON String to Map
Use `JsonDeserializer.jsonToMap(String jsString)` to convert a JSON string into a Map<String, Object>.
```java
String flowerJson = "{\"name\":\"Rose\",\"petalCount\":24,\"colors\":[\"red\",\"white\"]}";

Map<String, Object> flowerMap = JsonDeserializer.jsonToMap(flowerJson);
System.out.println(flowerMap.get("name"));      // Rose
        System.out.println(flowerMap.get("petalCount")); // 24
        System.out.println(flowerMap.get("colors"));    // [red, white]
```

### 2. Deserialization: Convert JSON String to Java object(custom)
Use `JsonDeserializer.jsonToClass(String jsString, Class<T> convertClass)` to deserialize JSON into a Java object.
```java
class Flower {
    String name;
    int petalCount;
    List<String> colors;
}

String json = "{\"name\":\"Tulip\",\"petalCount\":6,\"colors\":[\"yellow\",\"pink\"]}";
Flower myFlower = JsonDeserializer.jsonToClass(json, Flower.class);

System.out.println(myFlower.name);       // Tulip
System.out.println(myFlower.petalCount); // 6
System.out.println(myFlower.colors);    // [yellow, pink]
```
### 3. Serialization: Convert Java Object to JSON String
Use `JsonSerializer.classToJson(Object object)` to serialize Java objects into JSON strings.
```java
class Flower {
    String name = "Orchid";
    int petalCount = 5;
    List<String> colors = Arrays.asList("purple", "white");
}

Flower exoticFlower = new Flower();
String json = JsonSerializer.classToJson(exoticFlower);

System.out.println(json);
// {"name":"Orchid","petalCount":5,"colors":["purple","white"]}
```
## Polymorphic Class Deserialization
For complex polymorphic classes, use a custom deserializer:

1. Annotate your class with `@JsonDeserialize`

2. Create a static deserializer class with `deserialize` method

Example with plants (polymorphic version):

```java
@JsonDeserialize(using = Cat.CatDeserializer.class)
private static class Cat {
    List<AnimalPart> parts = new LinkedList<>();
    public Cat(Tail tail, Paw... paws) {
        super();
        parts.add(tail);
        parts.addAll(Arrays.asList(paws));
    }

    public static class CatDeserializer {
        public static Cat deserialize(Map<String, Object> jsonMap) {
            try {
                List<Map<String, Object>> partsData = (List<Map<String, Object>>) jsonMap.get("\"parts\"");

                Map<String, Object> tailMap = (Map<String, Object>) partsData.getFirst();
                Tail tail = new Tail();
                if (tailMap != null) {
                    if (tailMap.containsKey("\"lenght\"")) {
                        tail.lenght = (Double) tailMap.get("\"lenght\"");
                    }
                    if (tailMap.containsKey("\"name\"")) {
                        tail.name = (String) tailMap.get("\"name\"");
                    }
                }

                List<Map<String, Object>> pawsList = partsData.subList(1, partsData.size());
                Paw[] paws = new Paw[pawsList != null ? pawsList.size() : 0];

                for (int i = 0; pawsList != null && i < pawsList.size(); i++) {
                    Map<String, Object> pawMap = pawsList.get(i);
                    boolean isFront = pawMap.containsKey("\"isFront\"") &&
                            Boolean.TRUE.equals(pawMap.get("\"isFront\""));

                    List<Map<String, Object>> clawsList = (List<Map<String, Object>>) pawMap.get("\"claws\"");
                    Claw[] claws = new Claw[clawsList != null ? clawsList.size() : 0];

                    for (int j = 0; clawsList != null && j < clawsList.size(); j++) {
                        claws[j] = new Claw();
                        Map<String, Object> clawMap = clawsList.get(j);
                        if (clawMap.containsKey("\"name\"")) {
                            claws[j].name = (String) clawMap.get("\"name\"");
                        }
                    }

                    paws[i] = new Paw(isFront, claws);
                }
                return new Cat(tail, paws);

            } catch (Exception e) {
                throw new RuntimeException("Failed to deserialize Cat");
            }
        }
    }
```
## Limitations
Parser does not support:
- cyclic dependencies
- non-representable in JSON types

