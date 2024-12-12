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

    public byte[] getCiferedContent() {
        return ciferedContent;
    }



    @Override
    public String getCryptoConfig() {
        return cryptoConfig;
    }

    public String getRequestConfirmation() {
        return requestConfirmation;
    }

    public byte[] getNonce4plus1() {
        return nonce4plus1;
    }

    public byte[] getNonce5() {
        return nonce5;
    }

    public TypeFourResult(byte[] content) throws Exception {
        _repository = new ClientRepository();
        ciferedContent = content;
        decifer(content);
    }



    private void decifer(byte[] ciferedContet) throws Exception{

        /*
        var privKey = _repository.getPrivateKey();
        Cipher cipher= Cipher.getInstance("ECIES", "BC");
        cipher.init(Cipher.DECRYPT_MODE, privKey);
        byte[] plaintext = cipher.doFinal(ciferedContet);

        System.out.println("desencriptou: " + new String(plaintext));
        */

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


        for(String element: content){

            if(element.startsWith("request")){
                this.requestConfirmation = element.split(":")[1];
            }
            if(element.startsWith("nonce4plus1")){
                this.nonce4plus1 = element.split(":")[1].getBytes();
            }
            if(element.startsWith("nonce5")){
                this.nonce5 = element.split(":")[1].getBytes();
            }
            else {
                this.cryptoConfig = element;
            }
        }

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
