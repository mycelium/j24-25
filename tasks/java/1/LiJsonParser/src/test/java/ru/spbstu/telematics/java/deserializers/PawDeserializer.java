package ru.spbstu.telematics.java.deserializers;

import ru.spbstu.telematics.java.Common.LiJsonException;
import ru.spbstu.telematics.java.JsonReading.LiJsonCustomDeserializer;
import ru.spbstu.telematics.java.testModelClasses.Claw;
import ru.spbstu.telematics.java.testModelClasses.Paw;

import java.lang.reflect.Constructor;
import java.util.*;

public class PawDeserializer implements LiJsonCustomDeserializer<Paw> {
    @Override
    public Paw deserialize(Map<String, Object> jsonMap) throws LiJsonException {
        try {
            boolean isFront = (Boolean) jsonMap.get("isFront");
            List<Map<String, Object>> clawsData = (List<Map<String, Object>>) jsonMap.get("claws");

            Claw[] claws = new Claw[clawsData.size()];
            for (int i = 0; i < clawsData.size(); i++) {
                claws[i] = new ClawDeserializer().deserialize(clawsData.get(i));
            }

            return createPawWithReflection(isFront, claws);
        } catch (Exception e) {
            throw new LiJsonException("Ошибка десериализации Paw");
        }
    }

    private Paw createPawWithReflection(boolean isFront, Claw... claws) throws Exception {
        Constructor<Paw> constructor = Paw.class.getDeclaredConstructor(
                boolean.class, Claw[].class
        );
        constructor.setAccessible(true);
        return constructor.newInstance(isFront, claws);
    }
}