package BusinessLogic;

import Objects.MessageType;
import Objects.PayloadType1;

public class SHPDecifer implements ISHPDecifer{

    public String getPayload(MessageType msgType, byte[] content) throws Exception {

        if (content == null || content.length <= 3) {
            throw new IllegalArgumentException("Content must be at least 4 bytes long");
        }

        byte[] payload = new byte[content.length - 3];
        System.arraycopy(content, 3, payload, 0, payload.length);

        switch (msgType){
            case TYPE1:
                return new String(payload);


            default: throw new Exception("Invalid Message Type");
        }
    }
}
