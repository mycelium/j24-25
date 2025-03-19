package dev.tishenko;

public class Tson {
    public String toJson(Object obj) {
        if (obj == null) {
            return "null";
        }

        return obj.toString();
    }
}
