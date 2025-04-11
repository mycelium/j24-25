package jsonkit.util;

import jsonkit.model.JsonException;
import java.util.*;

public class JsonUtils {
    public static int findMatchingEnd(String json, char start, char end) throws JsonException {
        int level = 0;
        boolean inQuotes = false;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '"' && (i == 0 || json.charAt(i - 1) != '\\')) inQuotes = !inQuotes;
            if (!inQuotes) {
                if (c == start) level++;
                else if (c == end && --level < 0) return i;
            }
        }
        return level == 0 ? json.length() - 1 : -1;
    }

    public static List<String> splitJson(String json, char delimiter) {
        List<String> parts = new ArrayList<>();
        int braceCount = 0, bracketCount = 0;
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '"' && (i == 0 || json.charAt(i - 1) != '\\')) inQuotes = !inQuotes;

            if (!inQuotes) {
                if (c == '{') braceCount++;
                else if (c == '}') braceCount--;
                else if (c == '[') bracketCount++;
                else if (c == ']') bracketCount--;

                if (c == delimiter && braceCount == 0 && bracketCount == 0) {
                    parts.add(current.toString().trim());
                    current.setLength(0);
                    continue;
                }
            }
            current.append(c);
        }

        if (current.length() > 0) parts.add(current.toString().trim());
        return parts;
    }

    public static String[] splitKeyValue(String pair) throws JsonException {
        boolean inQuotes = false;
        for (int i = 0; i < pair.length(); i++) {
            char c = pair.charAt(i);
            if (c == '"') inQuotes = !inQuotes;
            else if (c == ':' && !inQuotes) {
                return new String[]{pair.substring(0, i).trim(), pair.substring(i + 1).trim()};
            }
        }
        throw new JsonException("Invalid key-value pair: " + pair);
    }
}