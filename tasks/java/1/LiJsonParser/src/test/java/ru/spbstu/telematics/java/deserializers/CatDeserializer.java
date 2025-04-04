package ru.spbstu.telematics.java.deserializers;

import ru.spbstu.telematics.java.Common.LiJsonException;
import ru.spbstu.telematics.java.JsonReading.LiJsonCustomDeserializer;
import ru.spbstu.telematics.java.testModelClasses.Cat;
import ru.spbstu.telematics.java.testModelClasses.Paw;
import ru.spbstu.telematics.java.testModelClasses.Tail;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

public class CatDeserializer implements LiJsonCustomDeserializer<Cat> {
    private final AnimalPartDeserializer animalPartDeserializer = new AnimalPartDeserializer();

    @Override
    public Cat deserialize(Map<String, Object> jsonMap) throws LiJsonException {
        try {
            List<Map<String, Object>> partsData = (List<Map<String, Object>>) jsonMap.get("parts");

            // Первый элемент - хвост
            Tail tail = (Tail) animalPartDeserializer.deserialize(partsData.get(0));

            // Остальные элементы - лапы
            Paw[] paws = new Paw[partsData.size() - 1];
            for (int i = 1; i < partsData.size(); i++) {
                paws[i-1] = (Paw) animalPartDeserializer.deserialize(partsData.get(i));
            }

            // Создаем Cat через reflection
            return createCatWithReflection(tail, paws);
        } catch (Exception e) {
            throw new LiJsonException("Ошибка десериализации Cat");
        }
    }

    private Cat createCatWithReflection(Tail tail, Paw... paws) throws Exception {
        Constructor<Cat> constructor = Cat.class.getDeclaredConstructor(Tail.class, Paw[].class);
        constructor.setAccessible(true);
        return constructor.newInstance(tail, paws);
    }
}