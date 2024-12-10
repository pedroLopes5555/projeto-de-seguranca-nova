package Objects;

public class SHPSocketType1 extends SHPSocket{

    public SHPSocketType1(byte protocolVersion, byte protocolRelease, MessageType msgTypeCode, byte[] payload) throws Exception {
        super(protocolVersion, protocolRelease, msgTypeCode, payload);
    }

    public SHPSocketType1(MessageType msgTypeCode, byte[] payload) throws Exception {
        super(msgTypeCode, payload);
    }


    public String getDecriptedPayload(){

        return new String(getPayload());
    }


}
