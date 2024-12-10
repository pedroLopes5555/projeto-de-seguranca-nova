package Objects;

import java.util.ArrayList;
import java.util.List;
/*
    This class is representative of the TCP socket that the Secure Handshake Protocol will use
*/

public class SHPSocket {

    private byte protocolVersion;    // 8 bits
    private byte protocolRelease;    // 8 bits
    private byte msgTypeCode;        // 8 bits
    private byte[] payload;          // Variable length
    private int HEADER_SIZE = 3;

    // Constructor
    public SHPSocket(byte protocolVersion, byte protocolRelease, byte msgTypeCode, byte[] payload) throws Exception {

        if (protocolVersion > 0x15){
            throw new Exception("protocool version max size is 4 bits");
        }
        if (protocolRelease > 0x15){
            throw new Exception("protocol realease max size is 4 bits");
        }

        this.protocolVersion = protocolVersion;
        this.protocolRelease = protocolRelease;
        this.msgTypeCode = msgTypeCode;
        this.payload = payload;
    }


    public SHPSocket(byte msgTypeCode, byte[] payload) throws Exception {
        this.protocolVersion = 0x01;
        this.protocolRelease = 0x01;
        this.msgTypeCode = msgTypeCode;
        this.payload = payload;
    }




    public byte[] getSocketContent(){

        byte[] result = new byte[HEADER_SIZE + getPayload().length];

        // Copy the elements of the first array to the new array
        System.arraycopy(getHeader(), 0, result, 0, HEADER_SIZE);

        // Copy the elements of the second array to the new array
        System.arraycopy(getPayload(), 0, result, HEADER_SIZE, getPayload().length);

        return result;

    }

    private byte[] getHeader(){

        byte[] header = new byte[3];
        header[0] = getProtocolVersion();
        header[1] = getProtocolRelease();
        header[2] = getMsgTypeCode();

        return header;
    }


    // Getters
    public byte getProtocolVersion() {
        return protocolVersion;
    }

    public byte getProtocolRelease() {
        return protocolRelease;
    }

    public byte getMsgTypeCode() {
        return msgTypeCode;
    }

    public byte[] getPayload() {
        return payload;
    }

    // Setters
    public void setProtocolVersion(byte protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public void setProtocolRelease(byte protocolRelease) {
        this.protocolRelease = protocolRelease;
    }

    public void setMsgTypeCode(byte msgTypeCode) {
        this.msgTypeCode = msgTypeCode;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }
}