package BusinessLogic;

import Objects.MessageType;
import Objects.Payload.Payload;
import Objects.User;

public interface ISHPCifer {

    public Payload createPayloadType1(String content) throws Exception;
    public Payload createPayloadType2() throws Exception;
    public Payload createPayloadType3(User user, byte[] nonce3) throws Exception;
    public Payload createPayloadType4(String userId, String request, byte[] nonce4, String cryptoconfig) throws Exception;
    public Payload createPayloadType5(MessageType msgType) throws Exception;


}
