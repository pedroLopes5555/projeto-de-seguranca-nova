package BusinessLogic.DeciferResult;

import Repository.ClientRepository;
import Repository.IClientRepository;
import Repository.ServerRepository;

import javax.crypto.Cipher;

public class TypeFourResult extends DeciferResult{



    /*
    *   Ekpubclient (request-confirmation, Nonce4+1, Nonce5, crypto config),
        DigitalSig (request-confirmation, userID, Nonce4+1, Nonce5 , crypto config),
        HMACkmac (X)*/


    IClientRepository _repository;
    private byte[] ciferedContent;
    private String cryptoConfig;                //string just for now



    public TypeFourResult(byte[] content, String userId) throws Exception {
        _repository = new ClientRepository();
        ciferedContent = content;
        decifer(content);
    }



    private void decifer(byte[] ciferedContet) throws Exception{

        var privKey = _repository.getPrivateKey();
        Cipher cipher= Cipher.getInstance("ECIES", "BC");
        cipher.init(Cipher.DECRYPT_MODE, privKey);
        byte[] plaintext = cipher.doFinal(ciferedContet);

        System.out.println("desencriptou: " + new String(plaintext));

    }
}
