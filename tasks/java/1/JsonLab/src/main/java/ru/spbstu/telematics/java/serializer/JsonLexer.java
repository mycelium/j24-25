package ru.spbstu.telematics.java.serializer;

import ru.spbstu.telematics.java.exceptions.InvalidJsonStringException;
import ru.spbstu.telematics.java.exceptions.InvalidPairParsingException;
import ru.spbstu.telematics.java.exceptions.JsonException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class JsonLexer {
    // Кэшированные шаблоны для чисел
    private static final Pattern INT_PATTERN = Pattern.compile("-?\\d+");
    private static final Pattern DOUBLE_PATTERN = Pattern.compile("-?\\d+\\.\\d+");

    // Разбивает содержимое JSON объекта на пары ключ-значение
    public static String[] splitJsonPairs(String content) {
        int level = 0, splitIndex = 0;
        List<String> pairs = new ArrayList<>();

        for (int i = 0; i < content.length(); i++) {
            switch(content.charAt(i))
            {
                case ('{'),('[') -> level++;
                case ('}'),(']') -> level--;
                case (',') -> {
                    if(level == 0)
                    {
                        pairs.add(content.substring(splitIndex, i).strip());
                        splitIndex = i + 1;
                    }
                }
            }
        }
        pairs.add(content.substring(splitIndex).strip());
        return pairs.toArray(new String[0]);
    }
    // Разбивает пару на ключ и значение
    public static String[] splitKeyValue(String pair) throws InvalidPairParsingException, InvalidJsonStringException {
        int colonIndex = -1, level = 0, levelOfQuotes=0;
        for (int i = 0; i < pair.length(); i++) {
            switch (pair.charAt(i)) {
                case ('{'),('[') -> level++;
                case ('}'),(']') -> level--;
                case ('"') -> levelOfQuotes ++;
                case (':') -> {
                    if(level == 0) {
                        colonIndex = i;
                    }
                }
            }
        }
        if (levelOfQuotes %2 != 0) throw new InvalidJsonStringException("Key provided without quotes");
        if (colonIndex == -1) throw new InvalidPairParsingException("No colon found in pair: " + pair);
        return new String[]{
                pair.substring(0, colonIndex).strip(),
                pair.substring(colonIndex + 1).strip()
        };
    }



    public static boolean isBoolean(String value) {
        return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
    }

    public static Number parseNumber(String valueStr) throws JsonException {
        try {
            if (INT_PATTERN.matcher(valueStr).matches()) {
                return Integer.parseInt(valueStr);
            } else if (DOUBLE_PATTERN.matcher(valueStr).matches()) {
                return Double.parseDouble(valueStr);
            }
            throw new JsonException("Invalid number format: " + valueStr);
        } catch (NumberFormatException e) {
            throw new JsonException("Number parsing error");
        }
    }
}
