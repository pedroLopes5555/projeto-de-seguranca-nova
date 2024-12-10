package Objects;

//AI generated class

public enum MessageType {
    TYPE1((byte) 0x01), // 0x01
    TYPE2((byte) 0x02), // 0x02
    TYPE3((byte) 0x03), // 0x03
    TYPE4((byte) 0x04), // 0x04
    TYPE5((byte) 0x05); // 0x05

    private final byte value;

    // Constructor to set the byte value for each type
    MessageType(byte value) {
        this.value = value;
    }

    // Getter to retrieve the byte value
    public byte getValue() {
        return value;
    }

    //ai generated
    public static MessageType fromByte(byte input) {
        for (MessageType type : MessageType.values()) {
            if (type.getValue() == input) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown MessageType for byte value: " + input);
    }

    @Override
    public String toString() {
        return name() + " (" + Integer.toHexString(value) + ")";
    }
}
