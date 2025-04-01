package ru.spbstu.hsai.jsparser;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class JsonParserClass {

    record MapPair(Map<String, Object> resultMap, int index) {}
    record ListPair(List<Object> resultList, int index) {};

    public static Map<String, Object> jsonToMap(String jsString) {
        JsonTokenizerClass tokensObj = new JsonTokenizerClass(jsString);
        List<String> tokens = tokensObj.tokenize().stream()
                .filter(n -> !n.equals(",") && !n.equals(":"))
                .collect(Collectors.toList());
        return convertTokenToMap(tokens, 1).resultMap;
    }

    private static ListPair convertTokenToList(List<String> subTokenList, int index) {
        List<Object> listObject = new ArrayList<>();
        while (index < subTokenList.size()) {
            String token = subTokenList.get(index);
            if (token.equals("[")) {
                ListPair result = convertTokenToList(subTokenList, index + 1);
                listObject.add(result.resultList);
                index = result.index;
            } else if (token.equals("{")) {
                MapPair result = convertTokenToMap(subTokenList, index + 1);
                listObject.add(result.resultMap);
                index = result.index;
            } else if (token.equals("]")) {
                return new ListPair(listObject, index);
            } else if (!token.equals(",")) {
                listObject.add(token);
            }
            index++;
        }
        return new ListPair(listObject, subTokenList.size());
    }

    private static MapPair convertTokenToMap(List<String> subTokenList, int index) {
        Map<String, Object> jsonMap = new HashMap<>();
        String key = "";
        while (index < subTokenList.size()) {
            String currentToken = subTokenList.get(index);
            switch (currentToken) {
                case "[" -> {
                    ListPair result = convertTokenToList(subTokenList, index + 1);
                    jsonMap.put(key, result.resultList);
                    key = "";
                    index = result.index;
                }
                case "{" -> {
                    MapPair result = convertTokenToMap(subTokenList, index+1);
                    jsonMap.put(key, result.resultMap);
                    key = "";
                    index = result.index;
                }
                case "}" -> {
                    return new MapPair(jsonMap, index);
                }
                default -> {
                    if (key.isEmpty()) {
                        key = currentToken;
                    } else {
                        jsonMap.put(key, currentToken);
                        key = "";
                    }
                }
            }
            index++;
        }
        return new MapPair(jsonMap, subTokenList.size());
    }
}
