package BusinessLogic;

import BusinessLogic.DeciferResult.DeciferResult;
import BusinessLogic.DeciferResult.TypeOneResult;
import BusinessLogic.DeciferResult.TypeTwoResult;
import Objects.MessageType;

public class SHPDecifer implements ISHPDecifer{

    public DeciferResult getPayload(MessageType msgType, byte[] content) throws Exception {

        if (content == null || content.length <= 3) {
            throw new IllegalArgumentException("Content must be at least 4 bytes long");
        }

        byte[] payload = new byte[content.length - 3];
        System.arraycopy(content, 3, payload, 0, payload.length);

        return switch (msgType) {
            case TYPE1 -> {
                if (content.length > 323) {
                    throw new Exception("Invalid size of package");
                }
                yield new TypeOneResult(new String(payload));
            }
            case TYPE2 -> {
                if (content.length > 51) {
                    throw new Exception("Invalid size of package");
                }
                yield new TypeTwoResult(getNonces(payload));
            }
            default -> throw new Exception("Invalid Message Type");
        };
    }


    private byte[][] getNonces (byte[] nonces){
        byte[][] result = new byte[3][16];
        int resultPos = 0;

            for (int i = 0; i < 48; i+=16){
                System.arraycopy(nonces, i, result[resultPos], 0 , result[resultPos].length);
                resultPos ++;
            }
        return result;
    }



















}
