public class JSONValue {
    public enum JSONType {
        STRING,
        NUMBER,
        BOOLEAN,
        NULL,
        ARRAY,
        OBJECT
    }

    private JSONType type;
    private String value;

    public JSONValue(JSONType type, String value) {
        this.type = type;
        this.value = value;
    }

    public JSONType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
