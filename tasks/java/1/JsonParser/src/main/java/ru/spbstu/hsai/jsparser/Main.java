package ru.spbstu.hsai.jsparser;
import java.util.*;
import ru.spbstu.hsai.jsparser.custom.JsonDeserialize;
public class Main {

//    private static class AnimalPart {
//        String name = "part";
//        public String getName() {
//            return name;
//        }
//    }
//
//
//    private static class Tail extends AnimalPart {
//        double lenght = 10.2;
//        public Tail() {
//            name = "Pretty fluffy tail";
//        }
//    }
//
//
//    private static class Claw extends AnimalPart {
//        public Claw() {
//            name = "claw";
//        }
//        @Override
//        public String getName() {
//            return "Sharp claw";
//        }
//    }
//
//    private static class Paw extends AnimalPart {
//        boolean isFront = true;
//        List<Claw> claws = new ArrayList<>();
//        public Paw(boolean isFront, Claw... claws) {
//            name = "paw";
//            this.isFront = isFront;
//            this.claws.addAll(Arrays.asList(claws));
//        }
//        @Override
//        public String getName() {
//            return isFront ? "Front paw" : "Back paw";
//        }
//    }
//
//    @JsonDeserialize(using = Cat.CatDeserializer.class)
//    private static class Cat {
//        List<AnimalPart> parts = new LinkedList<>();
//        public Cat(Tail tail, Paw... paws) {
//            super();
//            parts.add(tail);
//            parts.addAll(Arrays.asList(paws));
//        }
//
//        public static class CatDeserializer {
//            public static Cat deserialize(Map<String, Object> jsonMap) {
//                try {
//                    List<Map<String, Object>> partsData = (List<Map<String, Object>>) jsonMap.get("\"parts\"");
//
//                    Map<String, Object> tailMap = (Map<String, Object>) partsData.getFirst();
//                    Tail tail = new Tail();
//                    if (tailMap != null) {
//                        if (tailMap.containsKey("\"lenght\"")) {
//                            tail.lenght = (Double) tailMap.get("\"lenght\"");
//                        }
//                        if (tailMap.containsKey("\"name\"")) {
//                            tail.name = (String) tailMap.get("\"name\"");
//                        }
//                    }
//
//                    List<Map<String, Object>> pawsList = partsData.subList(1, partsData.size());
//                    Paw[] paws = new Paw[pawsList != null ? pawsList.size() : 0];
//
//                    for (int i = 0; pawsList != null && i < pawsList.size(); i++) {
//                        Map<String, Object> pawMap = pawsList.get(i);
//                        boolean isFront = pawMap.containsKey("\"isFront\"") &&
//                                Boolean.TRUE.equals(pawMap.get("\"isFront\""));
//
//                        List<Map<String, Object>> clawsList = (List<Map<String, Object>>) pawMap.get("\"claws\"");
//                        Claw[] claws = new Claw[clawsList != null ? clawsList.size() : 0];
//
//                        for (int j = 0; clawsList != null && j < clawsList.size(); j++) {
//                            claws[j] = new Claw();
//                            Map<String, Object> clawMap = clawsList.get(j);
//                            if (clawMap.containsKey("\"name\"")) {
//                                claws[j].name = (String) clawMap.get("\"name\"");
//                            }
//                        }
//
//                        paws[i] = new Paw(isFront, claws);
//                    }
//                    return new Cat(tail, paws);
//
//                } catch (Exception e) {
//                    throw new RuntimeException("Failed to deserialize Cat", e);
//                }
//            }
//        }
//    }

//    public static void main(String[] args) {
//        Cat cat = new Cat(new Tail(),
//                new Paw(true, new Claw(), new Claw(), new Claw(), new Claw()),
//                new Paw(true, new Claw(), new Claw(), new Claw(), new Claw()),
//                new Paw(false, new Claw(), new Claw(), new Claw(), new Claw()),
//                new Paw(false, new Claw(), new Claw(), new Claw(), new Claw())
//        );
//        String json = JsonSerializer.classToJson(cat);
//        System.out.println(json + "\n");
//        Cat catty = JsonDeserializer.jsonToClass(json, Cat.class);
//        System.out.println(JsonSerializer.classToJson(catty));
//    }


}