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
import Repository.ClientRepository;
import Repository.IClientRepository;
import org.bouncycastle.jcajce.provider.asymmetric.ec.KeyFactorySpi;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;

public class PayloadType3 extends Payload{


    IClientRepository _repository;

    public PayloadType3(User user, byte[] nonce3, int udpPort, String request) throws Exception {
        _repository = new ClientRepository();
        this.payload = createPayload(user, nonce3, udpPort, request);
    }


    private byte[] createPayload(User user, byte[] nonce3, int udpPort, String request) throws Exception {
        //Payload: ------------------------
        String userId = user.getUserId();
        String  nonce3plus1 = new String(addOne(nonce3));
        String nonce4 = new String(generateSalt()) ;
        String udpPortstring = Integer.toString(udpPort);
        String payload =
                "request:" + request +
                ";userid:" + userId +
                ";nonce3:" + nonce3plus1 +
                ";nonce4:" + nonce4 +
                ";udpport:" + udpPort;

        byte[] dataToEncrypt = payload.getBytes();
        System.out.println("payload: " + payload);

        //----------------------------------
        byte[] PBEEncryptedData = getPasswordBasedEncryptionPart(dataToEncrypt, user);
        byte[] digitalSign = getSigBytes(dataToEncrypt);

        byte[] pbeSize = intToBytes(PBEEncryptedData.length);
        byte[] signSize = intToBytes(digitalSign.length);

        byte[] hash = new byte[PBEEncryptedData.length + digitalSign.length];
        System.arraycopy(PBEEncryptedData, 0, hash,0, PBEEncryptedData.length);
        System.arraycopy(digitalSign, 0, hash,PBEEncryptedData.length, digitalSign.length);

        String password = user.getPassword();
        byte[] hmac = generateHMAC(hash, password);

        byte[] result = new byte[PBEEncryptedData.length + digitalSign.length + hmac.length + 4];
        System.arraycopy(pbeSize, 0, result,0, 2);
        System.arraycopy(signSize, 0, result,2, 2);
        System.arraycopy(PBEEncryptedData, 0, result,4, PBEEncryptedData.length);
        System.arraycopy(digitalSign, 0, result,PBEEncryptedData.length+4, digitalSign.length);
        System.arraycopy(hmac, 0, result,digitalSign.length+PBEEncryptedData.length+4, hmac.length);

        return result;
    }



    private byte[] getSigBytes(byte[] message) throws  Exception{
        PrivateKey privKey = _repository.getPrivateKey();
        Signature signature = Signature.getInstance("SHA256withECDSA", "BC");
        signature.initSign(privKey, new SecureRandom());
        signature.update(message);
        byte[] sigBytes = signature.sign();

        return sigBytes;
    }


    private byte[] getPasswordBasedEncryptionPart(byte[] dataToEncrypt, User user) throws Exception {
        byte[] salt = generateSalt();
        int iterationCount = 2048;


        byte[] hashedBytes = user.getHasehdPassword();
        System.out.println(hashedBytes.toString());


        //nao sei pq, e acho assustador, se eu colocar a hash numa variavel antes funciona,
        // se nao colocar nao funciona kkkkkk
        var hash = hashedBytes.toString();
        PBEKeySpec pbeSpec = new PBEKeySpec(hash.toCharArray());
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
        return Arrays.copyOfRange(added, added.length - byteArray.length, added.length);
    }


    private byte[] generateSalt(){
        return new byte[] { 0x7d, 0x60, 0x43, 0x5f, 0x02, (byte)0xe9, (byte)0xe0, (byte)0xae };
        /*
        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);

        return salt;*/
    }

    private static byte[] intToBytes(int value) {
        byte[] result = new byte[2];
        result[0] = (byte) (value >> 8);
        result[1] = (byte) (value);
        return result;
    }

    private byte[] generateHMAC(byte[] data, String password) throws Exception {
        // Derive a key from the password
        byte[] key = password.getBytes(); // Use a proper key derivation function in production!

        // Initialize HMAC with SHA-256
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "HmacSHA256");
        mac.init(secretKeySpec);

        // Compute the HMAC
        return mac.doFinal(data);
    }
}
