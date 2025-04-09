package ru.spbstu.telematics.java;
import java.util.*;

public class JsonParser {
    private String json;
    private int index;

    public Object parse(String json) {
        this.json = json;
        this.index = 0;

        skipWSpaces();
        Object result = parseValue();

        if (index < json.length()) {
            throw new RuntimeException("Неожиданные символы в конце json: " + json.substring(index));
        }

        return result;
    }

    private Object parseValue () {
        skipWSpaces();
        char c = currentChar();

        if (c == '{') {
            return parseObject();
        } else if (c == '[') {
            return parseArray();
        } else if (c == '"') {
            return parseString();
        } else if (c == '-' || Character.isDigit(c)) {
            return parseNumber();
        } else if (matchKeyword("true")) {
            return true;
        } else if (matchKeyword("false")) {
            return false;
        } else if (matchKeyword("null")) {
            return null;
        } else {
            throw new RuntimeException("Неизвестный тип значения в позиции " + index);
        }
    }

    private String parseString() {
        expect('"');
        StringBuilder str = new StringBuilder();

        while(true) {
            char c = json.charAt(index++);
            if (c == '"') break;
            else if (c == '\\') {
                if (index >= json.length()) {
                    throw new RuntimeException("Незавершенная escape последовательность");
                }
                c = json.charAt(index++);
                switch (c) {
                    case '"': str.append('"'); break;
                    case '\\': str.append('\\'); break;
                    case '/': str.append('/'); break;
                    case 'b': str.append('\b'); break;
                    case 'f': str.append('\f'); break;
                    case 'n': str.append('\n'); break;
                    case 'r': str.append('\r'); break;
                    case 't': str.append('\t'); break;
                    case 'u':
                        //unicode символ
                        String hex = json.substring(index, index + 4);
                        str.append((char) Integer.parseInt(hex, 16));
                        index += 4;
                        break;
                    default:
                        throw new RuntimeException("Встречен неизвестный символ: \\" + c);

                }
            } else {
                str.append(c);
            }
        }

        return str.toString();
    }

    private Number parseNumber() {
        int start = index;

        if (index < json.length() && json.charAt(index) == '-') {
            index++;
        }

        while (index < json.length() && Character.isDigit(json.charAt(index))) {
            index++;
        }

        boolean isDouble = false;

        if (index < json.length() && json.charAt(index) == '.') {
            isDouble = true;
            index++;
            while (index < json.length() && Character.isDigit(json.charAt(index))) {
                index++;
            }
        }

        if (index < json.length() && (json.charAt(index) == 'e' || json.charAt(index) == 'E')) {
            isDouble = true;
            index++;
            if (index < json.length() && (json.charAt(index) == '+' || json.charAt(index) == '-')) {
                index++;
            }
            while (index < json.length() && Character.isDigit(json.charAt(index))) {
                index++;
            }
        }

        String numStr = json.substring(start, index);

        if (isDouble) {
            return Double.parseDouble(numStr);
        } else {
            return Long.parseLong(numStr);
        }
    }

    private List<Object> parseArray() {
        expect('[');
        List<Object> list = new ArrayList<>();
        skipWSpaces();

        if(currentChar() == ']') {
            index ++;
            return list;
        }

        while (true) {
            skipWSpaces();
            Object value = parseValue();
            list.add(value);
            skipWSpaces();

            char c = currentChar();
            if (c == ']') {
                index ++;
                break;
            } else if (c == ',') {
                index++;
            } else {
                throw new RuntimeException("Ожидалось ',' или ']' по индексу " + index);
            }
        }
        return list;
    }

    private Object parseObject() {
        expect('{');
        Map<String, Object> map = new LinkedHashMap<>();
        skipWSpaces();

        if (currentChar() == '}') {
            index++;
            return map;
        }

        while (true) {
            skipWSpaces();
            String key = parseString();
            skipWSpaces();
            expect(':');
            skipWSpaces();
            Object value = parseValue();
            map.put(key, value);

            char c = currentChar();
            if (c == '}') {
                index ++;
                break;
            } else if (c == ',') {
                index++;
            } else {
                throw new RuntimeException("Ожидалось '}' или ',' по индексу " + index);
            }

        }

        return map;
    }


    private void expect(char expected) {
        if (index >= json.length() || json.charAt(index) != expected) {
            throw new RuntimeException("Ожидался '" + expected + "' по индексу " + index);
        }
        index++;
    }

    private boolean matchKeyword(String keyword) {
        if (json.startsWith(keyword, index)) {
            index += keyword.length();
            return true;
        }
        return false;
    }

    private char currentChar() {
        if (index >= json.length())
            throw new RuntimeException("Индекс символа больше длины json-строки");
        return json.charAt(index);
    }

    private void skipWSpaces() {
        while (index < json.length() && Character.isWhitespace(json.charAt(index)))
            index++;
    }


}
