package BusinessLogic.DeciferResult;

import BusinessLogic.Utils;
import Objects.User;
import Repository.ClientRepository;
import Repository.IClientRepository;
import Repository.ServerRepository;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.HashMap;
import java.util.Map;

public class TypeFourResult extends DeciferResult{



    /*
    *   Ekpubclient (request-confirmation, Nonce4+1, Nonce5, crypto config),
        DigitalSig (request-confirmation, userID, Nonce4+1, Nonce5 , crypto config),
        HMACkmac (X)*/


    IClientRepository _repository;
    private byte[] ciferedContent;
    private String cryptoConfig;
    private String requestConfirmation;
    private byte[] nonce4plus1;
    private byte[] nonce5;
    private Map<String, String> cryptoConfigMap = new HashMap<>();




    public byte[] getCiferedContent() {
        return ciferedContent;
    }


    @Override
    public Map<String, String> getCryptoConfigMap() {
        return cryptoConfigMap;
    }

    @Override
    public byte[] getNonce5() {
        return nonce5;
    }

    public String getRequestConfirmation() {
        return requestConfirmation;
    }

    public byte[] getNonce4plus1() {
        return nonce4plus1;
    }


    public TypeFourResult(byte[] content) throws Exception {
        _repository = new ClientRepository();
        ciferedContent = content;
        decifer(content);
    }



    private void decifer(byte[] ciferedContet) throws Exception{

        PrivateKey privateKey = _repository.getPrivateKey();
        PublicKey publicServerKey = _repository.getServerPublicKey();


        byte[] cifSize = new byte[2];
        System.arraycopy(ciferedContet,0, cifSize,0,2);

        byte[] signSize = new byte[2];
        System.arraycopy(ciferedContet,2, signSize,0,2);


        byte[] cifContent = new byte[bytesToInt(cifSize)];
        System.arraycopy(ciferedContet,4,cifContent,0,bytesToInt(cifSize));
        String deciferedContent = deciferCiferedContent(cifContent, privateKey);


        byte[] signContent = new byte[bytesToInt(signSize)];
        System.arraycopy(ciferedContet,4+bytesToInt(cifSize),signContent,0,bytesToInt(signSize));
        checkSignature(deciferedContent, signContent, publicServerKey);



        // MISSING HASH

    }

    private String deciferCiferedContent(byte[] cifContent, PrivateKey privateKey) throws Exception{

        Cipher cipher=Cipher.getInstance("ECIES", "BC");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] plaintext = cipher.doFinal(cifContent);

        //System.out.println("plain : " + Utils.stringInRed(new String(plaintext)));

        String[] content = new String(plaintext).split(";");


        String[] pairs = new String(plaintext).split(";");



        for (String pair : pairs) {

            if(pair.startsWith("request")){
                this.requestConfirmation = pair.split(":")[1];
            }
            else if(pair.startsWith("nonce4plus1")){
                this.nonce4plus1 = pair.split(":")[1].getBytes();
            }
            else if(pair.startsWith("nonce5")){
                this.nonce5 = pair.split(":")[1].getBytes();
            }

            else {
                // Split each pair into key and value
                String[] keyValue = pair.split(":", 2); // Limit to 2 splits to handle cases with ':' in the value
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim();
                    String value = keyValue[1].trim();
                    cryptoConfigMap.put(key, value);
                }
            }


        }


        /*
        // Print the parsed key-value pairs
        for (Map.Entry<String, String> entry : cryptoConfigMap.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }

        // Example: Access specific values
        String confidentiality = cryptoConfigMap.get("IV");
        System.out.println("IV: " + confidentiality);

         */

        return new String(plaintext);
    }

    private void checkSignature(String deciferedData, byte[] signContent, PublicKey publicKey) throws Exception{

        Signature signature = Signature.getInstance("SHA256withECDSA", "BC");

        signature.initVerify(publicKey);
        signature.update(deciferedData.getBytes());
        boolean isVerified = signature.verify(signContent);

        if(!isVerified){
            throw new Exception("Signature not verified");
        }
    }

    private static int bytesToInt(byte[] bytes) {
        if (bytes == null || bytes.length != 2) {
            throw new IllegalArgumentException("Byte array must be exactly 2 bytes long");
        }
        return ((bytes[0] & 0xFF) << 8) | (bytes[1] & 0xFF);
    }
}
