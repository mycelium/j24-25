# JSON Parser

В директории Salimli_Lab1, gradle проект.
=======
## Overview 
This JSON parser:
- Parsing made without Gson or ather lib for json parsing
- Also conversion: JSON to Java Objects
- File I/O also to dealWithIt.json file

| Component          | Purpose                          | Key Methods                     |
|____________________|__________________________________|_________________________________|
| **Parser**         | JSON → Java Map/Object           | `JSONIntoMAP()`, `JSONToObj()`  |
| **Serializer**     | Java Object → JSON String        | `toJSON()`, `Values()`          |
| **File Handler**   | Read/Write JSON files            | `FileWriter`                    |

### Key Features
```java
// 1. Parse JSON to Map
Map<String, Object> data = JSONIntoMAP("{\"futureCar\":\"Volvo\"}");

// 2. Convert JSON to Java Object
MyClass obj = JSONToObj(jsonString, MyClass.class);

// 3. Serialize object to JSON
String json = toJSON(obj);

// 4. Save to file
try (FileWriter writer = new FileWriter("dealWithIt.json")) {
    writer.write(toJSON(obj));
}
