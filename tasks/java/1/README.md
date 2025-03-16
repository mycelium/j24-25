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

## Limitations
Parser does not support cyclic dependencies and non-representable in JSON types
