package BusinessLogic;

import Objects.MessageType;
import Objects.Payload.Payload;
import Objects.Payload.PayloadType1;
import Objects.Payload.PayloadType2;

public class SHPCifer implements ISHPCifer {



    public Payload createPayload(MessageType msgType, String content) throws Exception {

        switch (msgType){
            case TYPE1:
                return new PayloadType1(content);
            default: throw new Exception("Invalid Message Type");
        }
    }


    public Payload createPayload(MessageType msgType) throws Exception {

        switch (msgType){
            case TYPE2:
                return new PayloadType2();
            default: throw new Exception("Invalid Message Type");
        }
    }
}
