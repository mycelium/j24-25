package ru.spbstu.telematics.java.deserializers;

import ru.spbstu.telematics.java.Common.LiJsonException;
import ru.spbstu.telematics.java.JsonReading.LiJsonCustomDeserializer;
import ru.spbstu.telematics.java.testModelClasses.AnimalPart;
import ru.spbstu.telematics.java.testModelClasses.Tail;

import java.util.Map;

class AnimalPartDeserializer implements LiJsonCustomDeserializer<AnimalPart> {
    @Override
    public AnimalPart deserialize(Map<String, Object> jsonMap) throws LiJsonException {
        String name = (String) jsonMap.get("name");

        if ("Pretty fluffy tail".equals(name)) {
            return new Tail();
        } else if ("paw".equals(name)) {
            return new PawDeserializer().deserialize(jsonMap);
        }

        throw new LiJsonException("Неизвестный AnimalPart тип: " + name);
    }
}
