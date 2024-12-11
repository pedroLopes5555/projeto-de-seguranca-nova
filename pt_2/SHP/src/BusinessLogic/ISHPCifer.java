package BusinessLogic;

import Objects.MessageType;
import Objects.Payload.Payload;
import Objects.User;

public interface ISHPCifer {

    public Payload createPayloadType1(String content) throws Exception;
    public Payload createPayloadType2() throws Exception;
    public Payload createPayloadType3(User user, byte[] nonce3) throws Exception;
    public Payload createPayloadType4(MessageType msgType) throws Exception;
    public Payload createPayloadType5(MessageType msgType) throws Exception;


}
