package BusinessLogic;

import Objects.MessageType;
import Objects.Payload.*;
import Objects.User;

import java.util.Map;
import java.util.Objects;

public class SHPCifer implements ISHPCifer {



    public Payload createPayloadType1(String content) throws Exception {

        if (content.getBytes().length > 320 || content.getBytes().length == 0){
            throw new Exception("Invalid Payload Content");
        }

        return new PayloadType1(content);

    }


    public Payload createPayloadType2() throws Exception {

        return new PayloadType2();
    }


    public Payload createPayloadType3(User user, byte[] nonce3, int udpPort, String request) throws Exception{


        if(user == null || user.isEmpty()){
            throw new Exception("Invalid User");
        }

        return new PayloadType3(user, nonce3, udpPort, request);
    }


    public Payload createPayloadType4(String userId, String request, byte[] nonce4) throws Exception {
        
        return new PayloadType4(userId, request, nonce4);
    }


    public Payload createPayloadType5(byte[] nonce5, Map<String, String> cryptoCfg) throws Exception {
        return new PayloadType5(nonce5, cryptoCfg);
    }


}
