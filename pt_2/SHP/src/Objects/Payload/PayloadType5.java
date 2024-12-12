package Objects.Payload;

import DSTP.GetEncryptedDatagram;
import Repository.ClientRepository;
import Repository.IClientRepository;
import Repository.IServerRepository;
import com.sun.nio.sctp.SctpSocketOption;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Map;

public class PayloadType5 extends Payload{

    private byte[] nonce;
    private IClientRepository _repository;
    private byte[] encryptedData;

    public PayloadType5(byte[] nonce, Map<String, String> cryptoCfg) throws Exception {
        _repository = new ClientRepository();
        this.payload = getEncryptedData(nonce, cryptoCfg);
    }


    public byte[] getNonce() {
        return nonce;
    }



    private byte[] getEncryptedData(byte[] nonce5, Map<String, String> cryptoCfg) throws Exception {
        byte[] message = "GO".getBytes();
        byte[] nonce5plus1 = addOne(nonce5);
        byte[] mac = new byte[10];
        byte[] data = new byte[message.length + nonce5plus1.length];

        //TODO -> falta fazer a mac function

        System.arraycopy(message, 0, data, 0, message.length);
        System.arraycopy(nonce5plus1, 0, data, message.length, nonce5plus1.length);
        System.out.println("o client vai mandar : " + new String(data));

        return GetEncryptedDatagram.getEncryptedDatagram(data, 1, cryptoCfg );
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
