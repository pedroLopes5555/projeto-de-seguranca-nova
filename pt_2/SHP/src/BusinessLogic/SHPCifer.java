package BusinessLogic;

import Objects.MessageType;
import Objects.Payload;
import Objects.PayloadType1;

public class SHPCifer implements ISHPCifer {



    public Payload createPayload(MessageType msgType, String content) throws Exception {

        switch (msgType){
            case TYPE1:
                return new PayloadType1(content);


            default: throw new Exception("Invalid Message Type");
        }
    }
}
