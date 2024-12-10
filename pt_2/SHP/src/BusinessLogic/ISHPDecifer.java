package BusinessLogic;

import Objects.MessageType;

public interface ISHPDecifer {

    public String getPayload(MessageType msgType, byte[] payload) throws Exception;

}
