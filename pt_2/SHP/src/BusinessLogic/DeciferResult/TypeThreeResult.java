package BusinessLogic.DeciferResult;

import Repository.IServerRepository;
import Repository.ServerRepository;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.Key;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Arrays;

public class TypeThreeResult extends  DeciferResult{


    private IServerRepository _repository;
    private byte[] result;
    private String request;
    private String userId;
    private byte[] nonce3plus1;
    private byte[] nonce4;
    private int udpPort;


    public IServerRepository get_repository() {
        return _repository;
    }

    public byte[] getResult() {
        return result;
    }

    public String getRequest() {
        return request;
    }

    public String getUserId() {
        return userId;
    }

    public byte[] getNonce3plus1() {
        return nonce3plus1;
    }

    @Override
    public byte[] getNonce4() {
        return nonce4;
    }

    public int getUdpPort() {
        return udpPort;
    }

    public TypeThreeResult(byte[] encryptedPayload, String userId) throws  Exception{
        _repository = new ServerRepository();
        this.result = deciferPayload(encryptedPayload, userId);
    }


    @Override
    public String toString() {
        return "TypeThreeResult{" +
                ", request='" + request + '\'' +
                ", userId='" + userId + '\'' +
                ", nonce3plus1=" + Arrays.toString(nonce3plus1) +
                ", nonce4=" + Arrays.toString(nonce4) +
                ", udpPort=" + udpPort +
                '}';
    }

    private byte[] deciferPayload(byte[] encriptedPayload, String userId) throws Exception{

        byte[] test = new byte[10];
        String password = _repository.getUserById(userId).getPassword();

        byte[] salt = new byte[] { 0x7d, 0x60, 0x43, 0x5f, 0x02, (byte)0xe9, (byte)0xe0, (byte)0xae };
        int iterationCount = 2048;

        PBEKeySpec pbeSpec = new PBEKeySpec(password.toCharArray());
        SecretKeyFactory keyFact = SecretKeyFactory.getInstance("PBEWITHSHA256AND192BITAES-CBC-BC","BC");
        Key sKey= keyFact.generateSecret(pbeSpec);

        Cipher cDec = Cipher.getInstance("PBEWITHSHA256AND192BITAES-CBC-BC","BC");
        cDec.init(Cipher.DECRYPT_MODE, sKey, new PBEParameterSpec(salt, iterationCount));


        String deciferedText =  new String(cDec.doFinal(encriptedPayload));

        //request:streaming;userid:paulinho@gmail.com;nonce3:M��k�Z`�Z#�i�;nonce4:}`C_��;udpport:3000
        String[] content = deciferedText.split(";");

        for(String element: content){

            if(element.startsWith("request")){
                this.request = element.split(":")[1];
            }
            if(element.startsWith("userid")){
                this.userId = element.split(":")[1];
            }
            if(element.startsWith("nonce3")){
                this.nonce3plus1 = element.split(":")[1].getBytes();
            }
            if(element.startsWith("nonce4")){
                this.nonce4 = element.split(":")[1].getBytes();
            }
            if(element.startsWith("udpport")){
                this.udpPort = Integer.parseInt(element.split(":")[1]);
            }
        }


//
//        PublicKey publicKey = _repository.getUserById(userId).getECCPublicKey();
//
//        Signature signature = Signature.getInstance("SHA256withECDSA", "BC");
//
//
//        signature.initVerify(publicKey);
//        signature.update("message".getBytes());
//        boolean isVerified = signature.verify(encriptedPayload);
//
//        System.out.println("Signature Verified: " + isVerified);


        return result;
    }

}
