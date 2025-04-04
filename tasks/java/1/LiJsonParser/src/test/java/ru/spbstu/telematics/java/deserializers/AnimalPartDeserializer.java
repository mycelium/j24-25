package ru.spbstu.telematics.java.deserializers;

import ru.spbstu.telematics.java.Common.LiJsonException;
import ru.spbstu.telematics.java.JsonReading.LiJsonCustomDeserializer;
import ru.spbstu.telematics.java.testModelClasses.AnimalPart;

import java.util.Map;

class AnimalPartDeserializer implements LiJsonCustomDeserializer<AnimalPart> {
    @Override
    public AnimalPart deserialize(Map<String, Object> jsonMap) throws LiJsonException {
        String name = (String) jsonMap.get("name");

        if ("Pretty fluffy tail".equals(name)) {
            return new TailDeserializer().deserialize(jsonMap);
        } else if ("paw".equals(name)) {
            return new PawDeserializer().deserialize(jsonMap);
        } else if ("claw".equals(name)) {
            return new ClawDeserializer().deserialize(jsonMap);
        }

        throw new LiJsonException("Unknown AnimalPart type: " + name);
    }
}
