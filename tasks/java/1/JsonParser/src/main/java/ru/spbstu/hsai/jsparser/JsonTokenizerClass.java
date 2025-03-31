package ru.spbstu.hsai.jsparser;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonTokenizerClass {
    private final String jsString;
    private static final Pattern JSON_TOKEN = Pattern.compile(
            "\"(?:\\\\[\"\\\\/bfnrt]|\\\\u[0-9a-fA-F]{4}|[^\"\\\\])*\"|" +
                    "-?(?:0|[1-9]\\d*)(?:\\.\\d+)?(?:[eE][+-]?\\d+)?|" +
                    "true|false|null|" +
                    "[{}\\[\\],:]|" +
                    "\\s+"
    );

    private static final Pattern NUMBER_PATTERN =
            Pattern.compile("^-?(?:0|[1-9]\\d*)(?:\\.\\d+)?(?:[eE][+-]?\\d+)?$");

    public JsonTokenizerClass(String jsString) {
        this.jsString = jsString;
    }

    public List<String> tokenize(){
        List<String> tokens = new ArrayList<>();
        Matcher matcher = JSON_TOKEN.matcher(jsString);
        int lastPos = 0;
        Deque<Character> braceStack = new ArrayDeque<>();

        while (matcher.find()) {
            if (matcher.start() > lastPos) {
                String unexpected = jsString.substring(lastPos, matcher.start()).trim();
                if (!unexpected.isEmpty()) {
                    throw new IllegalArgumentException("Unexpected character: '" + unexpected +
                            "' at position " + lastPos);
                }
            }
            lastPos = matcher.end();

            String token = matcher.group();
            if (token.trim().isEmpty()) continue;

            checkStructure(token, braceStack);

            validateToken(token);

            tokens.add(token);
        }

        if (lastPos != jsString.length()) {
            throw new IllegalArgumentException("Unexpected end of input");
        }

        if (!braceStack.isEmpty()) {
            throw new IllegalArgumentException("Unclosed " + braceStack.peek());
        }

        return tokens;
    }

    private static void checkStructure(String token, Deque<Character> braceStack){
        switch (token) {
            case "{":
                braceStack.push('}');
                break;
            case "[":
                braceStack.push(']');
                break;
            case "}":
            case "]":
                if (braceStack.isEmpty() || braceStack.pop()!=(token.charAt(0))) {
                    throw new IllegalArgumentException("Mismatched " + token);
                }
                break;
        }
    }

    private static void validateToken(String token){
        if (token.startsWith("\"")) {
            validateString(token);
        } else if (token.matches("-?\\d.*")) {
            if (!NUMBER_PATTERN.matcher(token).matches()) {
                throw new IllegalArgumentException("Invalid number format: " + token);
            }
        }
    }

    private static void validateString(String str) {
        String content = str.substring(1, str.length() - 1);
        Matcher m = Pattern.compile("\\\\.").matcher(content);

        while (m.find()) {
            String escape = m.group();
            if (escape.length() == 2) {
                char escChar = escape.charAt(1);
                if (!isValidEscapeChar(escChar)) {
                    throw new IllegalArgumentException("Invalid escape sequence: " + escape);
                }
            }
        }
    }

    private static boolean isValidEscapeChar(char c) {
        return c == '"' || c == '\\' || c == '/' ||
                c == 'b' || c == 'f' || c == 'n' ||
                c == 'r' || c == 't' || c == 'u';
    }

}
