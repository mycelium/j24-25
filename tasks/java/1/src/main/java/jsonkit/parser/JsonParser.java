package jsonkit.parser;

import jsonkit.model.JsonException;
import jsonkit.util.JsonUtils;
import java.util.*;

public class JsonParser {
    public static Object parse(String json) throws JsonException {
        json = json.trim();
        if (json.startsWith("{")) return parseObject(json);
        if (json.startsWith("[")) return parseArray(json);
        if (json.startsWith("\"")) return parseString(json);
        if (json.equals("null")) return null;
        if (json.equals("true") || json.equals("false")) return Boolean.parseBoolean(json);
        return parseNumber(json);
    }

    static Map<String, Object> parseObject(String json) throws JsonException {
        json = json.substring(1).trim();
        int endIndex = JsonUtils.findMatchingEnd(json, '{', '}');
        if (endIndex == -1) throw new JsonException("Unmatched curly braces");

        String content = json.substring(0, endIndex).trim();
        if (content.isEmpty()) return new LinkedHashMap<>();

        Map<String, Object> map = new LinkedHashMap<>();
        for (String pair : JsonUtils.splitJson(content, ',')) {
            String[] kv = JsonUtils.splitKeyValue(pair);
            map.put(parseString(kv[0]), parse(kv[1]));
        }
        return map;
    }

    static List<Object> parseArray(String json) throws JsonException {
        json = json.substring(1).trim();
        int endIndex = JsonUtils.findMatchingEnd(json, '[', ']');
        if (endIndex == -1) throw new JsonException("Unmatched square brackets");

        String content = json.substring(0, endIndex).trim();
        if (content.isEmpty()) return new ArrayList<>();

        List<Object> list = new ArrayList<>();
        for (String element : JsonUtils.splitJson(content, ',')) {
            list.add(parse(element.trim()));
        }
        return list;
    }

    static String parseString(String json) throws JsonException {
        if (json.length() < 2 || json.charAt(0) != '"' || json.charAt(json.length() - 1) != '"') {
            throw new JsonException("Unclosed string");
        }

        StringBuilder sb = new StringBuilder(json.length() - 2);
        for (int i = 1; i < json.length() - 1; i++) {
            char c = json.charAt(i);
            if (c == '\\') {
                if (i + 1 >= json.length() - 1) throw new JsonException("Invalid escape sequence");
                c = json.charAt(++i);
                sb.append(parseEscapeSequence(c, json, i));
            } else if (c == '"') {
                throw new JsonException("Unescaped quote in string");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static char parseEscapeSequence(char c, String json, int position) throws JsonException {
        switch (c) {
            case '"': return '"';
            case '\\': return '\\';
            case '/': return '/';
            case 'b': return '\b';
            case 'f': return '\f';
            case 'n': return '\n';
            case 'r': return '\r';
            case 't': return '\t';
            case 'u':
                if (position + 4 >= json.length() - 1) {
                    throw new JsonException("Invalid Unicode escape sequence");
                }
                String hex = json.substring(position + 1, position + 5);
                try {
                    return (char) Integer.parseInt(hex, 16);
                } catch (NumberFormatException e) {
                    throw new JsonException("Invalid Unicode escape: \\u" + hex);
                }
            default: throw new JsonException("Invalid escape character: \\" + c);
        }
    }

    static Number parseNumber(String json) throws JsonException {
        try {
            if (json.indexOf('.') != -1 || json.indexOf('e') != -1 || json.indexOf('E') != -1) {
                return Double.parseDouble(json);
            }
            return Long.parseLong(json);
        } catch (NumberFormatException e) {
            throw new JsonException("Invalid number format: " + json, e);
        }
    }
}