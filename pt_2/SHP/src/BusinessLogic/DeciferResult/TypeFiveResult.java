package BusinessLogic.DeciferResult;

import DSTP.DecriptDatagram;
import DSTP.EncriptedDatagramResoult;
import Repository.IServerRepository;
import Repository.ServerRepository;

public class TypeFiveResult extends DeciferResult{

    private String message;
    private byte[] nonce;
    private IServerRepository _repository;


    public TypeFiveResult(byte[] encryptedMessage) throws Exception{
        _repository = new ServerRepository();
        decript(encryptedMessage);
    }



    private void decript(byte[] encryptedMessage) throws Exception{
        EncriptedDatagramResoult result =  DecriptDatagram.GetDecriptedDatagram(encryptedMessage, "src/cfg/Server/ciphersuite.conf");

        System.out.println("Server Recebeu: " +  new String(result.getPtextBytes()));
    }
}
