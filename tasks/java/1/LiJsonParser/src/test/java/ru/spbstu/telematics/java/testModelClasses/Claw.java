package ru.spbstu.telematics.java.testModelClasses;



public class Claw extends AnimalPart {
    public Claw() {
        name = "claw";
    }
    @Override
    public String getName() {
        return "Sharp claw";
    }
}
