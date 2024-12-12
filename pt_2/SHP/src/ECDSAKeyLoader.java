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
        String privateKeyEncodedString = "MIGNAgEAMBAGByqGSM49AgEGBSuBBAAKBHYwdAIBAQQgWukXndmurjN4ZpVIvab+HlmX7ItXjalxZfWWpbw3ZQSgBwYFK4EEAAqhRANCAASaAP7a7u4n4yOTeH161Qb1jQW0vZ7LDGSjirqLjMCOa7509k+TMSpRUDnHcUATmQpXXbRjq1qQQn/TbS+Mu6sh";
        String publicKeyEncodedString = "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEmgD+2u7uJ+Mjk3h9etUG9Y0FtL2eywxko4q6i4zAjmu+dPZPkzEqUVA5x3FAE5kKV120Y6takEJ/020vjLurIQ==";




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