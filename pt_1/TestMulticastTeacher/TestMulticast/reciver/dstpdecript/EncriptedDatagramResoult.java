package dstpdecript;

public class EncriptedDatagramResoult {

    private byte[] ptextBytes;
    private byte[] sequenceNumber;

    // Constructor
    public EncriptedDatagramResoult(byte[] ptextBytes, byte[] sequenceNumber) {
        this.ptextBytes = ptextBytes;
        this.sequenceNumber = sequenceNumber;
    }

    // Getter and Setter for ptextBytes
    public byte[] getPtextBytes() {
        return ptextBytes;
    }

    public void setPtextBytes(byte[] ptextBytes) {
        this.ptextBytes = ptextBytes;
    }

    // Getter and Setter for sequenceNumber
    public byte[] getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(byte[] sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
}
