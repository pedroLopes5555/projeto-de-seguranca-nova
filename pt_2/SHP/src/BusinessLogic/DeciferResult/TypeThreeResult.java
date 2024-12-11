package BusinessLogic.DeciferResult;

import Repository.IRepository;
import Repository.Repository;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.Key;

public class TypeThreeResult extends  DeciferResult{


    private IRepository _repository;
    private byte[] result;

    public TypeThreeResult(byte[] encryptedPayload, String userId) throws  Exception{
        _repository = new Repository();
        this.result = deciferPayload(encryptedPayload, userId);
    }



    @Override
    public String toString() {

        // ANSI escape code for red text
        String red = "\u001B[31m";  // Red color
        String reset = "\u001B[0m"; // Reset to default color

        return "TypeOneResult{" +
                "result='" + red +  new String(result) +reset +  '\'' +
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


        return cDec.doFinal(encriptedPayload);
    }

}
