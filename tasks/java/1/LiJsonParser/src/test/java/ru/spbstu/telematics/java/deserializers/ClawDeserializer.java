package ru.spbstu.telematics.java.deserializers;

import ru.spbstu.telematics.java.JsonReading.LiJsonCustomDeserializer;
import ru.spbstu.telematics.java.testModelClasses.Claw;

import java.util.Map;

public class ClawDeserializer implements LiJsonCustomDeserializer<Claw> {
    @Override
    public Claw deserialize(Map<String, Object> jsonMap) {
        return new Claw();
    }
}