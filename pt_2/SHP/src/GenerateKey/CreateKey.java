package GenerateKey;// hj, SRSC 24/25

import java.math.BigInteger;
import java.security.*;
import java.security.spec.*;
import java.util.Arrays;

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

        /*GENERATE A KEY*/
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("ECDSA", "BC");
        ECGenParameterSpec ecSpec= new ECGenParameterSpec("secp256k1");
        kpg.initialize(ecSpec, new SecureRandom()); // why ?
        KeyPair keyPair= kpg.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        /*SINGH THE MESSAGE*/
        //Signature signature = Signature.getInstance("SHA512withECDSA", "BC");
        Signature signature = Signature.getInstance("SHA256withECDSA", "BC");
        //Signature signature = Signature.getInstance("SHA224/ECDSA", "BC");
        signature.initSign(keyPair.getPrivate(), new SecureRandom());
        signature.update(message);
        byte[]  sigBytes = signature.sign();
        System.out.println("ECDSA signature bytes: " + Utils3.toHex(sigBytes));
        System.out.println("Size of Signature    : " + sigBytes.length);




        /*Create the PrivKey from the encode*/
        System.out.println(Arrays.toString(privateKey.getEncoded()));
        var priveKeyEncodaded = privateKey.getEncoded();
        KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "BC");
        EncodedKeySpec KeySpec = new PKCS8EncodedKeySpec(priveKeyEncodaded);
        var privKey = keyFactory.generatePrivate(KeySpec);
        
        /*Create PubKey from encode*/
        System.out.println(Arrays.toString(publicKey.getEncoded()));
        var pubKeyEncoded = publicKey.getEncoded();
        KeyFactory keyFactory_2 = KeyFactory.getInstance("ECDSA", "BC");
        EncodedKeySpec KeySpec_2 = new X509EncodedKeySpec(pubKeyEncoded);
        var pubKey = keyFactory.generatePublic(KeySpec_2);
        System.out.println(Arrays.toString(pubKey.getEncoded()));




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
