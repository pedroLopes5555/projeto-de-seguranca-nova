package BusinessLogic;

import BusinessLogic.DeciferResult.DeciferResult;

public interface ISHPDecifer {

    public DeciferResult getPayloadType1(byte[] payload) throws Exception;
    public DeciferResult getPayloadType2(byte[] payload) throws Exception;
    public DeciferResult getPayloadType3(byte[] payload, String userId) throws Exception;

}
