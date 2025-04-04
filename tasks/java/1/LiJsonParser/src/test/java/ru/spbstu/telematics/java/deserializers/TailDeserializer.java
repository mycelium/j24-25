package ru.spbstu.telematics.java.deserializers;

import ru.spbstu.telematics.java.Common.LiJsonException;
import java.lang.reflect.Field;
import java.util.Map;

import ru.spbstu.telematics.java.JsonReading.LiJsonCustomDeserializer;
import ru.spbstu.telematics.java.testModelClasses.Tail;

public class TailDeserializer implements LiJsonCustomDeserializer<Tail> {
    @Override
    public Tail deserialize(Map<String, Object> jsonMap) throws LiJsonException {
        try {
            Tail tail = new Tail();
            Field lengthField = Tail.class.getDeclaredField("length");
            lengthField.setAccessible(true);
            lengthField.set(tail, ((Number) jsonMap.get("length")).doubleValue());
            return tail;
        } catch (Exception e) {
            throw new LiJsonException("Ошибка десериализации Tail");
        }
    }
}
