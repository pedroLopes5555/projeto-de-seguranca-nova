package GenerateKey;// hj, SRSC 24/25

import java.security.*;
import java.security.spec.ECGenParameterSpec;

/**
 * Geracao e verificacao de assinaturas digitais
 * com ECC DSA
 */
public class CreateKey
{
    public static void main(String[] args) throws Exception
    {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());


        byte[] message = "important msg to sign with ECC/DSA".getBytes();
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("ECDSA", "BC");
        ECGenParameterSpec ecSpec= new ECGenParameterSpec("secp256k1");
        kpg.initialize(ecSpec, new SecureRandom()); // why ?
        KeyPair keyPair= kpg.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();

        System.out.println(privateKey);

        //Signature signature = Signature.getInstance("SHA512withECDSA", "BC");
        Signature signature = Signature.getInstance("SHA256withECDSA", "BC");
        //Signature signature = Signature.getInstance("SHA224/ECDSA", "BC");
        signature.initSign(keyPair.getPrivate(), new SecureRandom());
        signature.update(message);

        byte[]  sigBytes = signature.sign();
        System.out.println("ECDSA signature bytes: " + Utils3.toHex(sigBytes));
        System.out.println("Size of Signature    : " + sigBytes.length);


        signature.initVerify(keyPair.getPublic());
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
