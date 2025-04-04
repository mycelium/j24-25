package ru.spbstu.telematics.java.testModelClasses;

import ru.spbstu.telematics.java.LiJsonParserTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Paw extends AnimalPart {
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