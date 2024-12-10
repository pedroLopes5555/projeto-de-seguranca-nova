package Objects.SHPSocket;

import Objects.MessageType;

public class SHPSocketUtils {


    public MessageType getMessageType(byte[] content) throws Exception {
        if(content.length < 3){
            throw new Exception("Invalid header size");
        }
        return MessageType.fromByte(content[2]);
    }

}
