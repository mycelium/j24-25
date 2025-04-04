package ru.spbstu.telematics.java.testModelClasses;

import ru.spbstu.telematics.java.LiJsonParserTest;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Cat {
    List<AnimalPart> parts = new LinkedList<>();
    public Cat(Tail tail, Paw... paws) {
        super();
        parts.add(tail);
        parts.addAll(Arrays.asList(paws));
    }
}
