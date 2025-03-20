package com.parser;

import java.util.ArrayList;
import java.util.List;

class JsonTokenizer {
    private final String json;
    private int position;
    private final List<JsonToken> tokens;
    private int tokenIndex;

    public JsonTokenizer(String json) throws JsonParseException {
        this.json = json;
        this.position = 0;
        this.tokens = new ArrayList<>();
        this.tokenIndex = 0;
        tokenize();
    }

    private void tokenize() throws JsonParseException {
        while (position < json.length()) {
            char c = json.charAt(position);
            if (Character.isWhitespace(c)) {
                position++;
                continue;
            }
            switch (c) {
                case '{':
                    tokens.add(new JsonToken(JsonTokenType.OBJECT_START, "{"));
                    position++;
                    break;
                case '}':
                    tokens.add(new JsonToken(JsonTokenType.OBJECT_END, "}"));
                    position++;
                    break;
                case '[':
                    tokens.add(new JsonToken(JsonTokenType.ARRAY_START, "["));
                    position++;
                    break;
                case ']':
                    tokens.add(new JsonToken(JsonTokenType.ARRAY_END, "]"));
                    position++;
                    break;
                case ',':
                    tokens.add(new JsonToken(JsonTokenType.COMMA, ","));
                    position++;
                    break;
                case ':':
                    tokens.add(new JsonToken(JsonTokenType.COLON, ":"));
                    position++;
                    break;
                case '"':
                    tokens.add(readString());
                    break;
                case 't':
                case 'f':
                    tokens.add(readBoolean());
                    break;
                case 'n':
                    tokens.add(readNull());
                    break;
                default:
                    if (c == '-' || Character.isDigit(c)) {
                        tokens.add(readNumber());
                    } else {
                        throw new JsonParseException("Unexpected character: " + c);
                    }
            }
        }
    }

    private JsonToken readString() throws JsonParseException {
        StringBuilder sb = new StringBuilder();
        position++; // Skip opening quote
        while (position < json.length()) {
            char c = json.charAt(position);
            if (c == '"') {
                position++;
                return new JsonToken(JsonTokenType.STRING, sb.toString());
            }
            if (c == '\\') {
                position++;
                if (position >= json.length()) {
                    throw new JsonParseException("Unterminated string");
                }
                c = json.charAt(position);
                switch (c) {
                    case '"':
                    case '\\':
                    case '/':
                        sb.append(c);
                        break;
                    case 'b':
                        sb.append('\b');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'u':
                        sb.append(readUnicode());
                        break;
                    default:
                        throw new JsonParseException("Invalid escape sequence: \\" + c);
                }
            } else {
                sb.append(c);
            }
            position++;
        }
        throw new JsonParseException("Unterminated string");
    }

    private char readUnicode() throws JsonParseException {
        if (position + 4 >= json.length()) {
            throw new JsonParseException("Invalid Unicode escape sequence");
        }
        String hex = json.substring(position + 1, position + 5);
        position += 4;
        try {
            return (char) Integer.parseInt(hex, 16);
        } catch (NumberFormatException e) {
            throw new JsonParseException("Invalid Unicode escape sequence: \\u" + hex);
        }
    }

    private JsonToken readBoolean() throws JsonParseException {
        if (json.startsWith("true", position)) {
            position += 4;
            return new JsonToken(JsonTokenType.BOOLEAN, "true");
        } else if (json.startsWith("false", position)) {
            position += 5;
            return new JsonToken(JsonTokenType.BOOLEAN, "false");
        } else {
            throw new JsonParseException("Invalid boolean value");
        }
    }

    private JsonToken readNull() throws JsonParseException {
        if (json.startsWith("null", position)) {
            position += 4;
            return new JsonToken(JsonTokenType.NULL, "null");
        } else {
            throw new JsonParseException("Invalid null value");
        }
    }

    private JsonToken readNumber() throws JsonParseException {
        int start = position;
        boolean hasDot = false;
        boolean hasExp = false;

        if (json.charAt(position) == '-') {
            position++;
        }

        while (position < json.length()) {
            char c = json.charAt(position);
            if (Character.isDigit(c)) {
                position++;
            } else if (c == '.' && !hasDot && !hasExp) {
                hasDot = true;
                position++;
            } else if ((c == 'e' || c == 'E') && !hasExp) {
                hasExp = true;
                position++;
                if (position < json.length() && (json.charAt(position) == '+' || json.charAt(position) == '-')) {
                    position++;
                }
            } else {
                break;
            }
        }

        String number = json.substring(start, position);
        return new JsonToken(JsonTokenType.NUMBER, number);
    }

    public JsonToken nextToken() throws JsonParseException {
        if (tokenIndex >= tokens.size()) {
            throw new JsonParseException("Unexpected end of input");
        }
        return tokens.get(tokenIndex++);
    }

    public JsonToken peekToken() throws JsonParseException {
        if (tokenIndex >= tokens.size()) {
            throw new JsonParseException("Unexpected end of input");
        }
        return tokens.get(tokenIndex);
    }
}