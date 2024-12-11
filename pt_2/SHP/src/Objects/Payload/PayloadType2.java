package Objects.Payload;

import java.security.SecureRandom;

public class PayloadType2 extends Payload{


    public PayloadType2() {
        this.payload = createSecureRandoms();
    }

    private byte[] createSecureRandoms(){
        byte[] nonces = new byte[48];

        for (int i = 0; i < 48; i+=16){
            byte[] helper = new byte[16];
            SecureRandom random = new SecureRandom();
            random.nextBytes(helper);

            System.arraycopy(helper, 0, nonces, i, 16);
        }

        return nonces;
    }



}
