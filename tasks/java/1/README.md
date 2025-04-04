# JSON Parser

## Overview
A simple Java-based JSON parser that converts JSON strings to Java objects using reflection. Supports basic data types, nested objects, arrays, collections, and custom class mappings.

## Features
- Converts JSON strings to `Map<String, Object>`
- Maps JSON to custom Java classes (with private fields support)
- Serializes Java objects back to JSON strings
- Handles:
    - Primitive types
    - Boxing types
    - Arrays and nested arrays
    - Collections
    - Null values
    - Custom objects

## Usage Examples

### 1. JSON to Map
```java
String json = "{\"name\":\"Mark\", \"age\":22}";
Map<String, Object> map = JsonParser.fromJsonToMap(json);
System.out.println(map.get("name")); // Output: Mark
```

### 2. JSON to Custom Object
```java
String json = "{\"name\":\"Mark\", \"age\":22}";
User user = JsonParser.fromJsonToClass(json, User.class);
System.out.println(user.getName()); // Output: Mark
```

### 3. Object to JSON
```java
String json = "{\"name\":\"Mark\", \"age\":22}";
User user = JsonParser.fromJsonToClass(json, User.class);
String json = JsonParser.fromObjToJson(user);
// Output: {"name":"Mark","age":22}
```

## Custom Deserialization with Annotations

For complex objects, you can define custom deserializers using annotations:

1. Create a deserializer class implementing `JsonDeserializer<T>` in your class:
```java
public class CatDeserializer implements JsonDeserializer<Cat> {
    @Override
    public Cat deserialize(Map<String, Object> map) {
        // Custom deserialization logic
    }
}
```

2. Annotate your class with @JsonDeserialize and implement method:
```java
@JsonDeserialize(using = CatDeserializer.class)
public class Cat {
  public static class CatDeserializer implements JsonDeserializer<Cat> {
    @Override
    public Cat deserialize(Map<String, Object> map) {
     // Custom deserialization logic
    }
  }
  
    // class implementation
}
```

### Example: Complex Object Deserialization
```java
@JsonDeserialize(using = Cat.CatDeserializer.class)
private static class Cat {

  public static class CatDeserializer implements JsonParser.JsonDeserializer<Cat> {
    @Override
    public Cat deserialize(Map<String, Object> map) {
      List<Map<String, Object>> parts = (List<Map<String, Object>>) map.get("parts");
      Tail tail = null;
      List<Paw> paws = new ArrayList<>();

      for (Map<String, Object> part : parts) {
        if (part.containsKey("lenght")) {
          tail = JsonParser.convertMapToObject(part, Tail.class);
        } else if (part.containsKey("isFront")) {
          paws.add(JsonParser.convertMapToObject(part, Paw.class));
        }
      }
      return new Cat(tail, paws.toArray(new Paw[0]));
    }
  }


  List<AnimalPart> parts = new LinkedList<>();
  public Cat(Tail tail, Paw... paws) {
    super();
    parts.add(tail);
    parts.addAll(Arrays.asList(paws));
  }
}
```


## Limitations
Parser does not support cyclic dependencies and non-representable in JSON types
