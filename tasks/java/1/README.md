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

### 6. Difficult cases
There are problems that JsonParser cannot handle on its own. For example:
```java
    private static class AnimalPart {
        String name = "part";
        public String getName() {
            return name;
        }
    }

    private static class Tail extends AnimalPart {
        double lenght = 10.2;
        public Tail() {
            name = "Pretty fluffy tail";
        }
    }

    private static class Claw extends AnimalPart {
        public Claw() {
            name = "claw";
        }
        @Override
        public String getName() {
            return "Sharp claw";
        }
    }

    private static class Paw extends AnimalPart {
        boolean isFront = true;
        List<Claw> claws = new ArrayList<>();
        public Paw(boolean isFront, Claw... claws) {
            name = "paw";
            this.isFront = isFront;
            this.claws.addAll(Arrays.asList(claws));
        }
        @Override
        public String getName() {
            return isFront ? "Front paw" : "Back paw";
        }
    }

    private static class Cat {
        List<AnimalPart> parts = new LinkedList<>();
        public Cat(Tail tail, Paw... paws) {
            super();
            parts.add(tail);
            parts.addAll(Arrays.asList(paws));
        }
    }
```
In this case we can see:
* **Ambiguity of types**: JSON does not store information about types (Tail, Paw, Claw — they all look like objects in JSON). Without explicit hints, the parser will not understand which class to deserialize AnimalPart into.
* **Incorrect order of elements**: The variable argument must be the last in the constructor. If the order of the fields in JSON does not match (for example, paws is specified before tail), deserialization will fail.

To solve such problems, you need to use the static method **JSONParser.addDeserializer( JSONParser. JSONDeserializer<T> deserializer, Class<T> clazz)**:
```java
JSONParser.addDeserializer((map) -> {
            List<HashMap<String, Object>> parts = (List) map.get("parts");
            Tail tail = null;
            List<Paw> paws = new ArrayList<>();
            for(var part: parts){
                try{
                    tail = JSONParser.fillClazzWithMap(part, Tail.class);
                } catch (Exception e){}
                try{
                    paws.addLast(JSONParser.fillClazzWithMap(part, Paw.class));
                } catch (Exception e){}
            }
            return new Cat(tail, paws.toArray(Paw[]::new));
        }, Cat.class);

        JSONParser.addDeserializer((map) -> {
            String name = (String) map.get("name");
            Boolean isFront = (Boolean) map.get("isFront");
            List<Claw> claws = ((List<Map<String, Object>>) map.get("claws")).stream().map((claw) ->
                    JSONParser.fillClazzWithMap(claw, Claw.class)
            ).toList();
            return new Paw(isFront, claws.toArray(Claw[]::new));
        }, Paw.class);
```
**addDeserializer** method registers a custom deserializer for the specified class.

If you only have a problem with the order of the arguments, then I advise you to use the **@ParamName** annotation:
```java
private static class Cat {
        List<AnimalPart> parts = new LinkedList<>();
        public Cat(@ParamName("tail") Tail tail, Paw... paws) {
            super();
            parts.add(tail);
            parts.addAll(Arrays.asList(paws));
        }
    }
```
If you mark all the constructor parameters with this annotation, then you don't have to worry about the order.

## Limitations
* The library does not support complex JSON structures like mixed types in arrays.
* It assumes that the JSON structure matches the Java object structure.
* Error handling is basic and may not cover all edge cases.
* The constructor that will be used to create the object must be defined. The number of parameters and types of parameters of the JSON object must be reduced to the parameters of this constructor.

## Dependencies
This library has no external dependencies and uses only standard Java libraries.
