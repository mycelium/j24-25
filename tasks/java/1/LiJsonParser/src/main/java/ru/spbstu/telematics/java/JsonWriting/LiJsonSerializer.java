package ru.spbstu.telematics.java.JsonWriting;
import ru.spbstu.telematics.java.Common.LiJsonException;
import java.lang.reflect.Field;
import java.util.*;


public class LiJsonSerializer {
    //метод для преобразования java-объекта в json-строку
    public String serializeToJson(Object object) throws LiJsonException, IllegalAccessException {
        return serialize(object);
    }

    //рекурсивный метод для преобразования объекта в json-строку
    private String serialize(Object object) throws LiJsonException, IllegalAccessException {
        if (object == null) {
            return "null";
        } else if (object instanceof String) {
            return "\"" + escapeString((String) object) + "\"";
        } else if (object instanceof Number) {
            return object.toString();
        } else if (object instanceof Boolean) {
            return object.toString();
        } else if (object instanceof Map) {
            return serializeMap((Map<?, ?>) object);
        } else if (object instanceof Collection) {
            return serializeCollection((Collection<?>) object);
        } else {
            return serializeObject(object);
        }
    }

    //метод для преобразования map в json-объект
    private String serializeMap(Map<?, ?> map) throws LiJsonException, IllegalAccessException {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) {
                json.append(",");
            }
            json.append("\"").append(escapeString(entry.getKey().toString())).append("\":");
            json.append(serialize(entry.getValue()));
            first = false;
        }
        json.append("}");
        return json.toString();
    }

    //метод для преобразования Collection в json-массив
    private String serializeCollection(Collection<?> collection) throws LiJsonException, IllegalAccessException {
        StringBuilder json = new StringBuilder("[");
        boolean first = true;
        for (Object item : collection) {
            if (!first) {
                json.append(",");
            }
            json.append(serialize(item));
            first = false;
        }
        json.append("]");
        return json.toString();
    }

    //метод для преобразования произвольного объекта в json-строку
    private String serializeObject(Object object) throws LiJsonException, IllegalAccessException {
        StringBuilder json = new StringBuilder("{");
        List<Field> fields = getAllFields(object.getClass());
        boolean first = true;
        for (Field field : fields) {
            field.setAccessible(true);
            if (!first) {
                json.append(",");
            }
            json.append("\"").append(escapeString(field.getName())).append("\":");
            json.append(serialize(field.get(object)));
            first = false;
        }
        json.append("}");
        return json.toString();
    }


    //вспомогательный метод для экранирования спец символов в строке
    private String escapeString(String str) {
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    //метод для получения всех полей класса, в том числе наследованных
    private List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }
}

