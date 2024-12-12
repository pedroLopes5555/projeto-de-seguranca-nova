package BusinessLogic;

import BusinessLogic.DeciferResult.DeciferResult;
import Objects.User;

public interface ISHPDecifer {

    public DeciferResult getPayloadType1(byte[] content) throws Exception;
    public DeciferResult getPayloadType2(byte[] content) throws Exception;
    public DeciferResult getPayloadType3(byte[] content, String userId) throws Exception;
    public DeciferResult getPayloadType4(byte[] content) throws Exception;

}
