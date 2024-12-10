package dstpdecript;
public enum ConfigKey {
    CONFIDENTIALITY("CONFIDENTIALITY"),
    SYMMETRIC_KEY("SYMMETRIC_KEY"),
    SYMMETRIC_KEY_SIZE("SYMMETRIC_KEY_SIZE"),
    IV_SIZE("IV Size"),
    IV("IV"),
    INTEGRITY("INTEGRITY"),
    H("H"),
    MAC("MAC"),
    MACKEY("MACKEY"),
    MACKEY_SIZE("MACKEY_SIZE");

    private final String value;

    
    ConfigKey(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
