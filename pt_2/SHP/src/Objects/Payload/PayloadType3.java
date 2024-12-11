package Objects.Payload;


/*
* PBEH(password), Salt, Counter (request, userID, Nonce3+1, Nonce4 , udp_port),
DigitalSig (request, userID, Nonce3+1, Nonce4 , udp_port ),
HMACkmac (X)
*
* MsgType 3 size: Size depending on used cryptographic constructions in message components
request: the request, according to the application (ex., movie or files to transfer)
PBE() : Must choose a secure PasswordBasedEncryption scheme
DigitlSig() : an ECDSA Signature, made with the client ECC private key (with a selected curve)
HMAC(): Must choose a secure HMAC construction, with the kmac derived from the password
X: the content of all (encrypted and signed) components in the message, to allow a fast message
authenticity and integrity check
* */

import Objects.User;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

public class PayloadType3 extends Payload{

    public PayloadType3(User user, byte[] nonce3) throws Exception {
        this.payload = createPayload(user, nonce3);
    }




    private byte[] createPayload(User user, byte[] nonce3) throws Exception {
        //Payload: ------------------------
        String request = "streaming";
        String userId = user.getUserId();
        String  nonce3plus1 = new String(addOne(nonce3));
        String nonce4 = new String(generateSalt()) ;
        String udpPort = "3000";
        String payload =
                "request:" + request +
                ";userid:" + userId +
                ";nonce3:" + nonce3plus1 +
                ";nonce4:" + nonce4 +
                ";udpport:" + udpPort;

        byte[] dataToEncrypt = payload.getBytes();
        System.out.println("payload: " + payload);

        //----------------------------------

        byte[] salt = generateSalt();
        int iterationCount = 2048;



        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashedBytes = digest.digest(user.getPassword().getBytes(StandardCharsets.UTF_8));


        System.out.printf(hashedBytes.toString());

        PBEKeySpec pbeSpec = new PBEKeySpec((hashedBytes.toString()).toCharArray());
        SecretKeyFactory keyFact = SecretKeyFactory.getInstance("PBEWITHSHA256AND192BITAES-CBC-BC","BC");
        Key secretKey = keyFact.generateSecret(pbeSpec);


        Cipher cEnc = Cipher.getInstance("PBEWITHSHA256AND192BITAES-CBC-BC","BC");
        cEnc.init(Cipher.ENCRYPT_MODE, secretKey, new PBEParameterSpec(salt, iterationCount));
        return  cEnc.doFinal(dataToEncrypt);
    }




    //AI generetaed code
    private static byte[] addOne(byte[] byteArray) {
        BigInteger bigInt = new BigInteger(1, byteArray);
        bigInt = bigInt.add(BigInteger.ONE);
        byte[] added = bigInt.toByteArray();
        // Ensure the resulting byte array does not exceed the original size
        byte[] finalResult = Arrays.copyOfRange(added, added.length - byteArray.length, added.length);

        return finalResult;
    }


    private byte[] generateSalt(){
        return new byte[] { 0x7d, 0x60, 0x43, 0x5f, 0x02, (byte)0xe9, (byte)0xe0, (byte)0xae };
//        byte[] salt = new byte[16];
//        SecureRandom random = new SecureRandom();
//        random.nextBytes(salt);
//
//        return salt;
    }



}



/*
public class PBEOtherExample
{
    public static void main(String[]    args)
        throws Exception
    {

	if (args.length==0) {
	    System.err.println("Use: PBEOtherExample <password>");
	    System.exit(-1);
	}

	Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());


        byte[]          input = new byte[] {
                            0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
                            0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
                            0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07 };
        byte[]          keyBytes = new byte[] {
                            0x73, 0x2f, 0x2d, 0x33, (byte)0xc8, 0x01, 0x73,
                            0x2b, 0x72, 0x06, 0x75, 0x6c, (byte)0xbd, 0x44,
                            (byte)0xf9, (byte)0xc1, (byte)0xc1, 0x03, (byte)0xdd,
                            (byte)0xd9, 0x7c, 0x7c, (byte)0xbe, (byte)0x8e };
        byte[]		    ivBytes = new byte[] {
                            (byte)0xb0, 0x7b, (byte)0xf5, 0x22, (byte)0xc8,
                            (byte)0xd6, 0x08, (byte)0xb8 };

        System.out.println("plaintext  : " + Utils.toHex(input));


        char[] password = args[0].toCharArray();
        byte[] salt = new byte[] { 0x7d, 0x60, 0x43, 0x5f, 0x02, (byte)0xe9, (byte)0xe0, (byte)0xae };
        int                 iterationCount = 2048;
        PBEKeySpec          pbeSpec = new PBEKeySpec(password);
        SecretKeyFactory keyFact = SecretKeyFactory.getInstance("PBEWITHSHA256AND192BITAES-CBC-BC","BC");
        Key sKey= keyFact.generateSecret(pbeSpec);

        // Cifrar com esquema PBE

        Cipher cEnc = Cipher.getInstance("PBEWITHSHA256AND192BITAES-CBC-BC","BC");
        cEnc.init(Cipher.ENCRYPT_MODE, sKey, new PBEParameterSpec(salt, iterationCount));

        byte[] out = cEnc.doFinal(input);

        // Decifrar com esquema PBE
        // Decifrar passando explicitamente os parametros de salt e iterador

        Cipher cDec = Cipher.getInstance("PBEWITHSHA256AND192BITAES-CBC-BC","BC");
        cDec.init(Cipher.DECRYPT_MODE, sKey, new PBEParameterSpec(salt, iterationCount));

        System.out.println("-------------------------------------------------");
        System.out.println("gen key    : " + Utils.toHex(sKey.getEncoded()));
        System.out.println("gen iv     : " + Utils.toHex(cDec.getIV()));
        System.out.println("key format : " + sKey.getFormat());
        System.out.println("key alg    : " + sKey.getAlgorithm());
        System.out.println("ciphertext : " + Utils.toHex(out));
        System.out.println("plaintext  : " + Utils.toHex(cDec.doFinal(out)));
        System.out.println("-------------------------------------------------");
    }
}

 */