package ru.spbstu.hsai.jsparser;

import java.util.*;

public class JsonParserClass {
    List<String> tokens;

    public JsonParserClass(String jsString) {
        JsonTokenizerClass tokensObj = new JsonTokenizerClass(jsString);
        tokens = tokensObj.tokenize();
    }

    public Map<String, Object> jsonToMap() {
        Map<String, Object> jsonMap = new HashMap<>();
        List<String> valueArray = new ArrayList<>();
        for (int i = 1; i < tokens.size(); i++) {
            String currentToken = tokens.get(i);
            if (currentToken.equals("}")) {
                break;
            }
            if (currentToken.equals(",")) {
                continue;
            }
            String key = currentToken;
            i += 2;
            currentToken = tokens.get(i);
            if (currentToken.equals("[")) {
                valueArray = new ArrayList<>();
                while (true) {
                    i++;
                    currentToken = tokens.get(i);
                    if (currentToken.equals("]")) break;
                    if (currentToken.equals(",")) continue;
                    valueArray.add(currentToken);
                }
                jsonMap.put(key, valueArray);
            } else {
                jsonMap.put(key, currentToken);
            }
        }
        return jsonMap;
    }


}
