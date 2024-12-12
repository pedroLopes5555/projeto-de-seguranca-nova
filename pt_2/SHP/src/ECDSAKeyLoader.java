import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;

import java.math.BigInteger;
import java.security.*;
import java.security.spec.*;
import java.util.Arrays;
import java.util.Base64;

public class ECDSAKeyLoader {
    public static void main(String[] args) throws Exception {

        // Add the BouncyCastle provider
        Security.addProvider(new BouncyCastleProvider());
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        // Values provided
        String curve = "secp256k1";
        String privateKeyEncodedString = "MIGNAgEAMBAGByqGSM49AgEGBSuBBAAKBHYwdAIBAQQgU+8FhtL5XAW1LEY0Od+qap57vdVE++HGkWWPBYKzSuygBwYFK4EEAAqhRANCAASUi9hz8lCSJDrvelwzkVFXsL+1ghdhYSp1PC9SHXCt+aDtr3I/i9GmpSwxwwxgodRvCbqpG8PZHog9bbmsT/6K";
        String publicKeyEncodedString = "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAElIvYc/JQkiQ673pcM5FRV7C/tYIXYWEqdTwvUh1wrfmg7a9yP4vRpqUsMcMMYKHUbwm6qRvD2R6IPW25rE/+ig==";




        /*Create privateKey to encode a message*/
        byte[] message = "teste".getBytes();

        /*Generate Priv Key*/
        //decode the base 64 privKey
        byte[] privKeyEncoded = Base64.getDecoder().decode(privateKeyEncodedString);
        KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "BC");
        EncodedKeySpec KeySpec = new PKCS8EncodedKeySpec(privKeyEncoded);
        var privKey = keyFactory.generatePrivate(KeySpec);


        Signature signature = Signature.getInstance("SHA256withECDSA", "BC");
        signature.initSign(privKey, new SecureRandom());
        signature.update(message);
        byte[] sigBytes = signature.sign();




        /*now we will generate the pubKey*/

        byte[] decodePubFromBase64 = Base64.getDecoder().decode(publicKeyEncodedString);
        KeyFactory keyFactory_2 = KeyFactory.getInstance("ECDSA", "BC");
        EncodedKeySpec KeySpec_2 = new X509EncodedKeySpec(decodePubFromBase64);
        var pubKey = keyFactory.generatePublic(KeySpec_2);


        signature.initVerify(pubKey);
        signature.update(message);

        if (signature.verify(sigBytes))
        {
            System.out.println("Assinatura ECDSA validada - reconhecida");
        }
        else
        {
            System.out.println("Assinatura nao reconhecida");
        }
    }
}