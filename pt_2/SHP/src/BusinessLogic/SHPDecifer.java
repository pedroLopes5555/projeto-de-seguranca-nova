package BusinessLogic;

import BusinessLogic.DeciferResult.*;
import Objects.MessageType;
import Objects.User;

import javax.swing.*;

public class SHPDecifer implements ISHPDecifer {

    public DeciferResult getPayloadType1(byte[] content) throws Exception {

        if (content == null || content.length <= 3) {
            throw new IllegalArgumentException("Content must be at least 4 bytes long");
        }
        if (content.length > 323) {
            throw new Exception("Invalid size of package");
        }

        return new TypeOneResult(new String(getPayloadFromContent(content)));

    }


    public DeciferResult getPayloadType2(byte[] content) throws Exception {
        if (content == null || content.length <= 3) {
            throw new IllegalArgumentException("Content must be at least 4 bytes long");
        }

        if (content.length > 51) {
            throw new Exception("Invalid size of package");
        }
        return new TypeTwoResult(getNonces(getPayloadFromContent(content)));

    }


    public DeciferResult getPayloadType3(byte[] content, String userId) throws Exception {

        if (content == null || content.length <= 3) {
            throw new IllegalArgumentException("Content must be at least 4 bytes long");
        }

        return new TypeThreeResult(getPayloadFromContent(content), userId);


    }

    @Override
    public DeciferResult getPayloadType4(byte[] content) throws Exception {
        if (content == null || content.length <= 3) {
            throw new IllegalArgumentException("Content must be at least 4 bytes long");
        }

        return new TypeFourResult(getPayloadFromContent(content));
    }


    private byte[][] getNonces(byte[] nonces) {
        byte[][] result = new byte[3][16];
        int resultPos = 0;

        for (int i = 0; i < 48; i += 16) {
            System.arraycopy(nonces, i, result[resultPos], 0, result[resultPos].length);
            resultPos++;
        }
        return result;
    }


    private byte[] getPayloadFromContent(byte[] content) {
        byte[] payload = new byte[content.length - 3];
        System.arraycopy(content, 3, payload, 0, payload.length);

        return payload;
    }


}






