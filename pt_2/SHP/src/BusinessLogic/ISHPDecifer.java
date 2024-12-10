package BusinessLogic;

import BusinessLogic.DeciferResult.DeciferResult;
import Objects.MessageType;

public interface ISHPDecifer {

    public DeciferResult getPayload(MessageType msgType, byte[] payload) throws Exception;

}
