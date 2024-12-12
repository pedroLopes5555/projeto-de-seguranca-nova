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
import java.util.HashMap;
import java.util.Map;

public class PayloadType4 extends Payload{

    IServerRepository _repository;
    private User user;


    public PayloadType4(String userId, String request, byte[] nonce4) throws Exception {
        _repository = new ServerRepository();
        this.user = _repository.getUserById(userId);
        this.payload = createPayload(request, nonce4);

    }


    private byte[] createPayload(String request, byte[] nonce4) throws Exception{
        /*Ekpubclient (request-confirmation, Nonce4+1, Nonce5, crypto config),
        DigitalSig (request-confirmation, userID, Nonce4+1, Nonce5 , crypto config),
        HMACkmac (X)*/

        //Payload: ------------------------
        String nonce4plus1 = new String(addOne(nonce4));
        String nonce5 = new String(generateSalt());
        String payload = "request:streaming" +
                ";nonce4plus1:" + nonce4plus1 +
                ";nonce5:" + nonce5 +
                ";" + _repository.getCriptoConfig();


        System.out.println(payload);


        String[] pairs = payload.split(";");

        // Create a map to store the parsed key-value pairs
        Map<String, String> payloadMap = new HashMap<>();

        for (String pair : pairs) {
            // Split each pair into key and value
            String[] keyValue = pair.split(":", 2); // Limit to 2 splits to handle cases with ':' in the value
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                payloadMap.put(key, value);
            }
        }

        // Print the parsed key-value pairs
        for (Map.Entry<String, String> entry : payloadMap.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }

        // Example: Access specific values
        String confidentiality = payloadMap.get("CONFIDENTIALITY");
        System.out.println("CONFIDENTIALITY: " + confidentiality);






        byte[] dataToEncrypt = payload.getBytes();
        System.out.println("payload: " + payload);
        //----------------------------------

        byte[] encryptedData = Encrypt(payload.getBytes());
        byte[] digitalSign = getSigBytes(dataToEncrypt);

        byte[] encSize = intToBytes(encryptedData.length);
        byte[] signSize = intToBytes(digitalSign.length);

        byte[] hash = new byte[encryptedData.length + digitalSign.length];
        System.arraycopy(encryptedData, 0, hash,0, encryptedData.length);
        System.arraycopy(digitalSign, 0, hash,encryptedData.length, digitalSign.length);

        byte[] result = new byte[encryptedData.length + digitalSign.length + hash.length + 4];
        System.arraycopy(encSize, 0, result,0, 2);
        System.arraycopy(signSize, 0, result,2, 2);
        System.arraycopy(encryptedData, 0, result,4, encryptedData.length);
        System.arraycopy(digitalSign, 0, result,encryptedData.length+4, digitalSign.length);
        System.arraycopy(hash, 0, result,digitalSign.length+encryptedData.length+4, hash.length);

        //MISSING HASH

        return result;
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

    private byte[] getSigBytes(byte[] message) throws  Exception{
        PrivateKey privKey = _repository.getPrivateKey();
        Signature signature = Signature.getInstance("SHA256withECDSA", "BC");
        signature.initSign(privKey, new SecureRandom());
        signature.update(message);
        byte[] sigBytes = signature.sign();

        return sigBytes;
    }

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

    private static byte[] intToBytes(int value) {
        byte[] result = new byte[2];
        result[0] = (byte) (value >> 8);
        result[1] = (byte) (value);
        return result;
    }
}
