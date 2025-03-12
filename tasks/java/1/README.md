# JSON Parser
This is a simple JSON parser library written in Java. It allows you to convert JSON strings into Java objects and vice versa. The library supports basic JSON structures, including objects, arrays, strings, numbers, booleans, and null values. It also handles nested objects and arrays.

## Features
* JSON to Java Object: Convert a JSON string into a Java object.
* Java Object to JSON: Convert a Java object into a JSON string.
* JSON to Map: Convert a JSON string into a Map<String, Object>.
* Nested Objects and Arrays: Supports nested objects and arrays within JSON.
* Primitive Types: Handles primitive types (e.g., int, double, boolean) and their boxed counterparts.
* Custom Objects: Can convert JSON into custom Java objects and vice versa.

## Usage
### 1. Converting JSON to Map
To convert a JSON string into a Map<String, Object>, use the readJsonToMap method. This is useful when you don't have a specific Java class to map the JSON to.
```Java
String json = "{\"name\":\"John\", \"age\":30, \"isStudent\":false}";
Map<String, Object> map = JSONParser.readJsonToMap(json);
System.out.println(map.get("name")); // Output: John
System.out.println(map.get("age"));  // Output: 30
System.out.println(map.get("isStudent")); // Output: false
```

### 2. Converting JSON to Java Object
To convert a JSON string into a Java object, use the readJsonToObject or readJsonToEntity method.
```Java
String json = "{\"name\":\"John\", \"age\":30, \"isStudent\":false}";
Person person = JSONParser.readJsonToEntity(json, Person.class);
```

### 3. Converting Java Object to JSON
To convert a Java object into a JSON string, use the convertEntityToJSON method.
```Java
Person person = new Person("John", 30, false);
String json = JSONParser.convertEntityToJSON(person);
System.out.println(json); // Output: {"name":"John","age":30,"isStudent":false}
```

### 4. Handling Nested Objects and Arrays
The library can handle nested objects and arrays. For example:
```Java
String json = "{\"name\":\"John\", \"address\":{\"city\":\"New York\", \"zip\":10001}, \"hobbies\":[\"reading\", \"swimming\"]}";
Person person = JSONParser.readJsonToEntity(json, Person.class);
```

### 5. Custom Objects
To use custom objects, ensure that the class has a constructor that matches the fields in the JSON. The library will automatically map JSON fields to the corresponding class fields.
```Java
public class Person {
    private String name;
    private int age;
    private boolean isStudent;

    // Constructor
    public Person(String name, int age, boolean isStudent) {
        this.name = name;
        this.age = age;
        this.isStudent = isStudent;
    }

    // Getters and setters
    // ...
}
```

## Limitations
* The library does not support complex JSON structures like mixed types in arrays.
* It assumes that the JSON structure matches the Java object structure.
* Error handling is basic and may not cover all edge cases.

## Dependencies
This library has no external dependencies and uses only standard Java libraries.
