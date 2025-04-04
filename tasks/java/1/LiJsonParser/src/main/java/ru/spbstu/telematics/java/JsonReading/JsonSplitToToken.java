package ru.spbstu.telematics.java.JsonReading;

// Класс JsonSplitToToken для разбиения json-строки на токены
// Токен - строка/число/литерал
public class JsonSplitToToken {
    private final String jsonString; //исходная строка для разбора
    private int position; //текущая позиция в строке, с кот начинается разбор

    public JsonSplitToToken(String jsonString) {
        this.jsonString = jsonString;
        this.position = 0;
    }

    //метод для извлечения следующего токена из json-строки
    public String getNextToken() {
        //пропускаем пробелы
        while (position < jsonString.length() && Character.isWhitespace(jsonString.charAt(position))) {
            position++;
        }

        //если достигли конца строки
        if (position >= jsonString.length()) {
            return null;
        }

        char currentChar = jsonString.charAt(position); //получаем текущий символ
        position++;
        //вызываем для каждого символа соответствующую функцию для парсинга: цифра/строка/литерал
        switch (currentChar) {
            case '{':
            case '}':
            case '[':
            case ']':
            case ':':
            case ',':
                return String.valueOf(currentChar);
            // return getNextToken();
            case '"':
                return parseString();
            default:
                if (currentChar == '-') {
                    //проверяем, что следующий символ число (т.е. это знак минус)
                    if (position < jsonString.length() && (Character.isDigit(jsonString.charAt(position)) || jsonString.charAt(position) == '.')) {
                        return parseNumber(currentChar);
                    } else {
                        throw new IllegalArgumentException("Некорректный символ: " + currentChar);
                    }
                } else if (Character.isDigit(currentChar) || currentChar == '.') {
                    return parseNumber(currentChar);
                } else if (currentChar == 't' || currentChar == 'f' || currentChar == 'n') {
                    return parseLiteral(currentChar);
                }
        }
        throw new IllegalArgumentException("Некорректный символ: " + currentChar);
    }

    //метод для разбора строки, заключенной в двойные кавычки
    private String parseString() {
        StringBuilder sb = new StringBuilder(); //для накопления символов строки
        char currentChar;
        //продолжаем разбор строки, пока не встретим закрывающую кавычку
        while (position < jsonString.length() && (currentChar = jsonString.charAt(position)) != '"') {
            //обрабатываем экранированные символы
            if (currentChar == '\\') {
                position++;
                if (position < jsonString.length()) {
                    sb.append(jsonString.charAt(position));
                }
            } else {
                sb.append(currentChar);
            }
            position++;
        }
        position++; //пропускаем закрывающую кавычку
        return sb.toString();
    }

    //метод для разбора числа (включая целые, вещественные, отриц, с экспонентой e)
    private String parseNumber(char firstChar) {
        StringBuilder sb = new StringBuilder();
        sb.append(firstChar);
        char currentChar;
        boolean hasDecimalPoint = firstChar == '.';

        while (position < jsonString.length()) {
            currentChar = jsonString.charAt(position);
            if (Character.isDigit(currentChar)){
                sb.append(currentChar);
                position++;
            }else if(currentChar == '.' && !hasDecimalPoint){
                sb.append(currentChar);
                position++;
                hasDecimalPoint = true;
            }else{
                break;
            }

        }
        return sb.toString();
    }

    //метод для разбора литералов (true, false, null)
    private String parseLiteral(char firstChar) {
        if (firstChar == 't') {
            skipChars("rue");
            return "true";
        } else if (firstChar == 'f') {
            skipChars("alse");
            return "false";
        } else if (firstChar == 'n') {
            skipChars("ull");
            return "null";
        }
        throw new IllegalArgumentException("Некорректный символ литерала: " + firstChar);
    }

    //вспомогательный метод, чтобы пропустить определенные символы строки
    private void skipChars(String chars) {
        for (int i = 0; i < chars.length(); i++) {
            if (position < jsonString.length() && jsonString.charAt(position) == chars.charAt(i)) {
                position++;
            } else {
                throw new IllegalArgumentException("Некорректная последовательность символов" + chars);
            }
        }
    }
}
