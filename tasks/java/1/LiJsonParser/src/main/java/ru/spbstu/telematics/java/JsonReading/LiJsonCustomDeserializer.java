package ru.spbstu.telematics.java.JsonReading;

import ru.spbstu.telematics.java.Common.LiJsonException;
import java.util.Map;

public interface LiJsonCustomDeserializer<T> {
    T deserialize(Map<String, Object> jsonMap) throws LiJsonException;
}
