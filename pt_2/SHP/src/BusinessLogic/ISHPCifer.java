package BusinessLogic;

import Objects.MessageType;
import Objects.Payload;

public interface ISHPCifer {

    public Payload createPayload(MessageType msgType, String content) throws Exception;

}
