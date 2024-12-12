import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;

import java.math.BigInteger;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class ECDSAKeyLoader {
    public static void main(String[] args) throws Exception {
        // Add the BouncyCastle provider
        Security.addProvider(new BouncyCastleProvider());
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        // Values provided
        String curve = "secp256k1";
        String privateKeyEncodedString = "[B@145f66e3";
        String publicKeyEncodedString = "[B@4c309d4d";




    }
}