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

    @Override
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

    @Override
    public int getUdpPort() {
        return udpPort;
    }

    public TypeThreeResult(byte[] encryptedPayload, String userId) throws  Exception{
        _repository = new ServerRepository();
        deciferPayload(encryptedPayload, userId);
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

    private void deciferPayload(byte[] encriptedPayload, String userId) throws Exception{

        String password = _repository.getUserById(userId).getPassword();
        PublicKey publicKey = _repository.getUserById(userId).getECCPublicKey();

        byte[] pbeSize = new byte[2];
        System.arraycopy(encriptedPayload,0, pbeSize,0,2);

        byte[] signSize = new byte[2];
        System.arraycopy(encriptedPayload,2, signSize,0,2);


        byte[] pbeContent = new byte[bytesToInt(pbeSize)];
        System.arraycopy(encriptedPayload,4,pbeContent,0,bytesToInt(pbeSize));
        String deciferedContent = deciferPbeContent(pbeContent, password);


        byte[] signContent = new byte[bytesToInt(signSize)];
        System.arraycopy(encriptedPayload,4+bytesToInt(pbeSize),signContent,0,bytesToInt(signSize));
        checkSignature(deciferedContent, signContent, publicKey);

        //HASH MISSING




    }

    private String deciferPbeContent(byte[] pbeContent, String password) throws Exception{
        if(password == null){
            throw new Exception("invalid user id");
        }

        byte[] salt = new byte[] { 0x7d, 0x60, 0x43, 0x5f, 0x02, (byte)0xe9, (byte)0xe0, (byte)0xae };
        int iterationCount = 2048;

        PBEKeySpec pbeSpec = new PBEKeySpec(password.toCharArray());
        SecretKeyFactory keyFact = SecretKeyFactory.getInstance("PBEWITHSHA256AND192BITAES-CBC-BC","BC");
        Key sKey= keyFact.generateSecret(pbeSpec);

        Cipher cDec = Cipher.getInstance("PBEWITHSHA256AND192BITAES-CBC-BC","BC");
        cDec.init(Cipher.DECRYPT_MODE, sKey, new PBEParameterSpec(salt, iterationCount));

        String deciferedText =  new String(cDec.doFinal(pbeContent));

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

        return deciferedText;
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
