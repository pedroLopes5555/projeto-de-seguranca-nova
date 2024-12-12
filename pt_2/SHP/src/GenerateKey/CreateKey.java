package GenerateKey;// hj, SRSC 24/25

import java.math.BigInteger;
import java.security.*;
import java.security.spec.*;
import java.util.Arrays;
import java.util.Base64;

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



        System.out.println("\n");
        System.out.println("\n");
        System.out.println("\n");

        /*Create the PrivKey from the encode*/
        System.out.println("privada:");
        System.out.println(Arrays.toString(privateKey.getEncoded()));

        String privKeyBase64 = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        System.out.println("para base 64: " + privKeyBase64);

        byte[] decodeprivFromBase64 = Base64.getDecoder().decode(privKeyBase64);
        System.out.println("de 64 para encoded:");
        System.out.println(decodeprivFromBase64);

        var priveKeyEncodaded = privateKey.getEncoded();
        KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "BC");
        EncodedKeySpec KeySpec = new PKCS8EncodedKeySpec(priveKeyEncodaded);
        var privKey = keyFactory.generatePrivate(KeySpec);
        System.out.println("\n");
        System.out.println("\n");


        /*Create PubKey from encode*/
        System.out.println("publica:");
        System.out.println(Arrays.toString(publicKey.getEncoded()));

        var pubKeyEncoded = publicKey.getEncoded();
        var pubKeyBase64 = Base64.getEncoder().encodeToString(pubKeyEncoded);
        System.out.println("para base 64 :" + pubKeyBase64);
        byte[] decodePubFromBase64 = Base64.getDecoder().decode(pubKeyBase64);
        System.out.println("de 64 para encoded de novo");
        System.out.println(Arrays.toString(decodePubFromBase64));

        KeyFactory keyFactory_2 = KeyFactory.getInstance("ECDSA", "BC");
        EncodedKeySpec KeySpec_2 = new X509EncodedKeySpec(decodePubFromBase64);
        var pubKey = keyFactory.generatePublic(KeySpec_2);
        System.out.println(Arrays.toString(pubKey.getEncoded()));
        System.out.println("\n");
        System.out.println("\n");
        System.out.println("\n");
        System.out.println("\n");


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
