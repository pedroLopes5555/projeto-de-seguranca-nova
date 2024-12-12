package Objects.Payload;

import Objects.User;
import Repository.ClientRepository;
import Repository.IClientRepository;
import Repository.IServerRepository;
import Repository.ServerRepository;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.Arrays;

public class PayloadType4 extends Payload{

    IServerRepository _repository;
    private User user;


    public PayloadType4(String userId, String request, byte[] nonce4, String cryptoconfig) throws Exception {
        _repository = new ServerRepository();
        this.user = _repository.getUserById(userId);
        this.payload = createPayload(request, nonce4, cryptoconfig);

    }


    private byte[] createPayload(String request, byte[] nonce4, String cryptoconfig) throws Exception{
        /*Ekpubclient (request-confirmation, Nonce4+1, Nonce5, crypto config),
        DigitalSig (request-confirmation, userID, Nonce4+1, Nonce5 , crypto config),
        HMACkmac (X)*/

        //Payload: ------------------------
        String nonce4plus1 = new String(addOne(nonce4));
        String nonce5 = new String(generateSalt());
        String payload = "request:streaming;" +
                "nonce4plus1:" + nonce4plus1 +
                "nonce5:" + nonce5 +
                "cryptoconfig:" + cryptoconfig;
        //----------------------------------

        return Encrypt(payload.getBytes());
    }



    private byte[] Encrypt(byte[] content) throws Exception{
        if(this.user == null){
            throw new Exception("User dont exist");
        }
        var pubKey = this.user.getECCPublicKey();
        Cipher cipher=Cipher.getInstance("ECIES", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return cipher.doFinal(content);
    }


//
//
//
//    private byte[] getSigBytes(byte[] message) throws  Exception{
//        PublicKey pubKey = _repository.getUserById(this.user.getUserId()).getECCPublicKey();
//
//        Signature signature = Signature.getInstance("SHA256withECDSA", "BC");
//        signature.initSign(privKey, new SecureRandom());
//        signature.update(message);
//        byte[] sigBytes = signature.sign();
//
//        return sigBytes;
//    }








    private byte[] generateSalt(){
        return new byte[] { 0x7d, 0x60, 0x43, 0x5f, 0x02, (byte)0xe9, (byte)0xe0, (byte)0xae };
        /*
        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);

        return salt;*/
    }

    //AI generetaed code
    private static byte[] addOne(byte[] byteArray) {
        BigInteger bigInt = new BigInteger(1, byteArray);
        bigInt = bigInt.add(BigInteger.ONE);
        byte[] added = bigInt.toByteArray();
        // Ensure the resulting byte array does not exceed the original size
        return Arrays.copyOfRange(added, added.length - byteArray.length, added.length);
    }
}
