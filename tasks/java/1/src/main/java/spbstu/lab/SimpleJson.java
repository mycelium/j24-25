
package spbstu.lab;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;


/**
 * 1) parse(String json) -> Object
 * 2) parse(String json, Class<T> clazz) -> T
 * 3) parseToMap(String json) -> Map<String, Object>
 * 4) toJson(Object obj) -> String
 * <p>
 * Поддерживается:
 * - Примитивы и их объектные оболочки
 * - Строки
 * - Массивы
 * - Списки (List)
 * - Map
 * - Вложенные объекты
 */
public class SimpleJson {

    /**
     * Разбирает JSON-строку в "сырую" структуру Java:
     * - Вложенный Map для JSON-объектов
     * - List для JSON-массивов
     * - String, Number, Boolean, null и т.п.
     */
    public static Object parse(String json) {
        Parser parser = new Parser(json);
        return parser.parseValue();
    }

    /**
     * Разбирает JSON-строку в Map<String, Object>.
     * Если корневой элемент не является JSON-объектом,
     * метод вернёт null.
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> parseToMap(String json) {
        Object root = parse(json);
        if (root instanceof Map) {
            return (Map<String, Object>) root;
        }
        return null;
    }


    public static <T> T parse(String json, Class<T> clazz) {
        Object root = parse(json);
        return convertValue(root, clazz);
    }


    public static String toJson(Object obj) {
        StringBuilder sb = new StringBuilder();
        serializeValue(obj, sb);
        return sb.toString();
    }


    /**
     * Универсальный метод преобразования "сырых" JSON-данных (Map, List, String, Number, Boolean)
     * в объект нужного типа через reflection.
     */
    @SuppressWarnings("unchecked")
    static <T> T convertValue(Object rawValue, Class<T> targetType) {
        if (rawValue == null) {
            return null;
        }

        if (targetType == String.class) {
            return (T) String.valueOf(rawValue);
        }

        if (isNumericType(targetType) && rawValue instanceof Number) {
            return convertNumber((Number) rawValue, targetType);
        }

        if (targetType == Boolean.class || targetType == boolean.class) {
            if (rawValue instanceof Boolean) {
                return (T) rawValue;
            }
            return (T) Boolean.valueOf(String.valueOf(rawValue));
        }

        if (List.class.isAssignableFrom(targetType)) {
            if (rawValue instanceof List) {
                return (T) rawValue;
            }
            return null;
        }

        if (targetType.isArray()) {
            if (rawValue instanceof List) {
                List<?> rawList = (List<?>) rawValue;
                Class<?> componentType = targetType.getComponentType();
                Object array = Array.newInstance(componentType, rawList.size());
                for (int i = 0; i < rawList.size(); i++) {
                    Object element = convertValue(rawList.get(i), componentType);
                    Array.set(array, i, element);
                }
                return (T) array;
            }
            return null;
        }


        if (rawValue instanceof Map && !targetType.isPrimitive()) {
            try {
                T instance = targetType.getDeclaredConstructor().newInstance();
                Map<String, Object> map = (Map<String, Object>) rawValue;


                for (Field field : getAllFields(targetType)) {
                    field.setAccessible(true);
                    Object valueInMap = map.get(field.getName());
                    if (valueInMap != null) {
                        Class<?> fieldType = field.getType();
                        Object fieldValue = convertValue(valueInMap, fieldType);
                        field.set(instance, fieldValue);
                    }
                }
                return instance;
            } catch (Exception e) {
                throw new RuntimeException("Ошибка при преобразовании в " + targetType.getName(), e);
            }
        }

        return (T) rawValue;
    }


    private static boolean isNumericType(Class<?> type) {
        if (type.isPrimitive()) {
            return type == int.class
                    || type == long.class
                    || type == float.class
                    || type == double.class
                    || type == short.class
                    || type == byte.class;
        }
        return Number.class.isAssignableFrom(type);
    }


    @SuppressWarnings("unchecked")
    private static <T> T convertNumber(Number number, Class<T> targetType) {
        if (targetType == Integer.class || targetType == int.class) {
            return (T) Integer.valueOf(number.intValue());
        }
        if (targetType == Long.class || targetType == long.class) {
            return (T) Long.valueOf(number.longValue());
        }
        if (targetType == Float.class || targetType == float.class) {
            return (T) Float.valueOf(number.floatValue());
        }
        if (targetType == Double.class || targetType == double.class) {
            return (T) Double.valueOf(number.doubleValue());
        }
        if (targetType == Short.class || targetType == short.class) {
            return (T) Short.valueOf(number.shortValue());
        }
        if (targetType == Byte.class || targetType == byte.class) {
            return (T) Byte.valueOf(number.byteValue());
        }
        return (T) number;
    }

    private static void serializeValue(Object value, StringBuilder sb) {
        if (value == null) {
            sb.append("null");
            return;
        }

        if (value instanceof String) {
            sb.append("\"").append(escapeString((String) value)).append("\"");
            return;
        }

        if (value instanceof Number) {
            sb.append(value);
            return;
        }

        if (value instanceof Boolean) {
            sb.append(value);
            return;
        }

        if (value instanceof Collection) {
            serializeCollection((Collection<?>) value, sb);
            return;
        }

        if (value.getClass().isArray()) {
            serializeArray(value, sb);
            return;
        }

        if (value instanceof Map) {
            serializeMap((Map<?, ?>) value, sb);
            return;
        }

        serializeObject(value, sb);
    }


    private static void serializeCollection(Collection<?> collection, StringBuilder sb) {
        sb.append("[");
        boolean first = true;
        for (Object element : collection) {
            if (!first) {
                sb.append(",");
            }
            serializeValue(element, sb);
            first = false;
        }
        sb.append("]");
    }

    private static void serializeArray(Object array, StringBuilder sb) {
        sb.append("[");
        int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            serializeValue(Array.get(array, i), sb);
        }
        sb.append("]");
    }

    private static void serializeMap(Map<?, ?> map, StringBuilder sb) {
        sb.append("{");
        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) {
                sb.append(",");
            }
            sb.append("\"").append(escapeString(String.valueOf(entry.getKey()))).append("\":");
            serializeValue(entry.getValue(), sb);
            first = false;
        }
        sb.append("}");
    }


    static void serializeObject(Object obj, StringBuilder sb) {
        sb.append("{");
        Field[] fields = getAllFields(obj.getClass());
        boolean first = true;
        for (Field field : fields) {
            if (!first) {
                sb.append(",");
            }
            field.setAccessible(true);
            sb.append("\"").append(escapeString(field.getName())).append("\":");
            try {
                Object fieldValue = field.get(obj);
                serializeValue(fieldValue, sb);
            } catch (IllegalAccessException e) {
                sb.append("null");
            }
            first = false;
        }
        sb.append("}");
    }


    private static Field[] getAllFields(Class<?> clazz) {
        List<Field> result = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            Field[] declaredFields = clazz.getDeclaredFields();
            Collections.addAll(result, declaredFields);
            clazz = clazz.getSuperclass();
        }
        return result.toArray(new Field[0]);
    }


    private static String escapeString(String str) {
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }


    static class Parser {
        private final String json;
        private int pos;
        private final int length;

        Parser(String json) {
            this.json = json;
            this.pos = 0;
            this.length = json.length();
        }


        Object parseValue() {
            skipWhitespace();
            if (pos >= length) {
                return null;
            }
            char c = json.charAt(pos);
            return switch (c) {
                case '{' -> parseObject();
                case '[' -> parseArray();
                case '"' -> parseString();
                case 't', 'f' -> parseBoolean();
                case 'n' -> parseNull();
                default -> parseNumber();
            };
        }

        /**
         * Парсинг JSON-объекта: { "key": value, ... }
         */
        private Map<String, Object> parseObject() {
            Map<String, Object> map = new LinkedHashMap<>();
            expectChar('{');
            skipWhitespace();
            if (json.charAt(pos) == '}') {
                pos++;
                return map;
            }
            while (true) {
                skipWhitespace();
                String key = parseString();
                skipWhitespace();
                expectChar(':');
                skipWhitespace();
                Object value = parseValue();
                map.put(key, value);
                skipWhitespace();
                char c = json.charAt(pos);
                if (c == '}') {
                    pos++;
                    break;
                }
                expectChar(',');
            }
            return map;
        }

        /**
         * Парсинг JSON-массива: [ value, value, ... ]
         */
        private List<Object> parseArray() {
            List<Object> list = new ArrayList<>();
            expectChar('[');
            skipWhitespace();
            if (json.charAt(pos) == ']') {
                pos++;
                return list;
            }
            while (true) {
                skipWhitespace();
                Object value = parseValue();
                list.add(value);
                skipWhitespace();
                char c = json.charAt(pos);
                if (c == ']') {
                    pos++;
                    break;
                }
                expectChar(',');
            }
            return list;
        }


        private String parseString() {
            expectChar('"');
            StringBuilder sb = new StringBuilder();
            while (pos < length) {
                char c = json.charAt(pos++);
                if (c == '"') {
                    break;
                }
                if (c == '\\') {
                    if (pos < length) {
                        char next = json.charAt(pos++);
                        switch (next) {
                            case '"', '\\', '/' -> sb.append(next);
                            case 'b' -> sb.append('\b');
                            case 'f' -> sb.append('\f');
                            case 'n' -> sb.append('\n');
                            case 'r' -> sb.append('\r');
                            case 't' -> sb.append('\t');
                            default -> {
                                sb.append('\\').append(next);
                            }
                        }
                    }
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }


        private Number parseNumber() {
            int start = pos;
            while (pos < length) {
                char c = json.charAt(pos);
                if ((c >= '0' && c <= '9') || c == '.' || c == '-' || c == '+'
                        || c == 'e' || c == 'E') {
                    pos++;
                } else {
                    break;
                }
            }
            String numberStr = json.substring(start, pos);
            if (numberStr.contains(".") || numberStr.contains("e") || numberStr.contains("E")) {
                try {
                    return Double.valueOf(Double.parseDouble(numberStr));
                } catch (NumberFormatException e) {
                    return Double.valueOf(0.0);
                }
            } else {
                try {
                    long longValue = Long.parseLong(numberStr);
                    if (longValue <= Integer.MAX_VALUE && longValue >= Integer.MIN_VALUE) {
                        return Integer.valueOf((int) longValue);
                    }
                    return Long.valueOf(longValue);
                } catch (NumberFormatException e) {
                    return Long.valueOf(0L);
                }
            }
        }


        private Boolean parseBoolean() {
            if (json.startsWith("true", pos)) {
                pos += 4;
                return Boolean.TRUE;
            } else if (json.startsWith("false", pos)) {
                pos += 5;
                return Boolean.FALSE;
            }
            return null;
        }


        private Object parseNull() {
            if (json.startsWith("null", pos)) {
                pos += 4;
                return null;
            }
            return null;
        }

        private void skipWhitespace() {
            while (pos < length) {
                char c = json.charAt(pos);
                if (Character.isWhitespace(c)) {
                    pos++;
                } else {
                    break;
                }
            }
        }

        private void expectChar(char expected) {
            char c = json.charAt(pos);
            if (c != expected) {
                throw new RuntimeException("Ожидался символ '" + expected + "', получено: '" + c + "' (pos=" + pos + ")");
            }
            pos++;
        }
    }
}
