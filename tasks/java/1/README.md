# JSON Parser

## Overview
The JSON Parser Library is a Java library for parsing JSON data into Java objects and Map. It provides functionality for reading JSON files, converting them into Java objects or maps, and writing objects or maps back into JSON format.

## Features
- Convert JSON strings and files to Java objects and maps.
- Serialize Java objects and maps into JSON strings.
- Supports nested objects and collections.
- Handles various Java data types such as primitives, collections, and custom objects.
- Provides exception handling for incorrect JSON formats.

## Usage

### JsonInteractor Interface
The `JsonInteractor` interface provides utility methods for working with JSON data.
#### Convert Map to JSON string
```java
String jsonString = JsonInteractor.mapToJson(myMap);
```
#### Read JSON from a file
```java
String jsonString = JsonInteractor.jsonFileToJsonString(new File("data.json"));
```

### JsonReader Class
The `JsonReader` class provides methods to parse JSON strings and files into Java objects and maps.
#### Convert JSON string to Map
```java
Map<String, Object> jsonMap = JsonReader.fromJsonToMap(jsonString);
```
#### Convert JSON string to a new object instance
```java
MyClass obj = JsonReader.fromJsonNewObject(jsonString, MyClass.class);
```
#### Populate an existing object with JSON data
```java
JsonReader.fromJsonToObject(jsonString, existingObject);
```
#### Read JSON file into a Map
```java
Map<String, Object> jsonMap = JsonReader.fromJsonToMap(new File("data.json"));
```

### JsonWriter Class
The `JsonWriter` class provides methods to serialize Java objects and maps into JSON format.
#### Convert Map to JSON string
```java
String jsonString = JsonWriter.fromMapToJson(myMap);
```
#### Convert an object to JSON string
```java
String jsonString = JsonWriter.fromObjectToJsonString(myObject);
```

## Exception Handling
The library throws `WrongJsonStringFormatException` for incorrectly formatted JSON strings. Ensure that input JSON follows proper syntax.