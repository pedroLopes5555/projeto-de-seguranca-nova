package BusinessLogic;

import Objects.MessageType;
import Objects.Payload.Payload;

public interface ISHPCifer {

    public Payload createPayload(MessageType msgType, String content) throws Exception;
    public Payload createPayload(MessageType msgType) throws Exception;


}
