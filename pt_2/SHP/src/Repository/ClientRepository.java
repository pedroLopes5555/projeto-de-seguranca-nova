package Repository;

import Objects.User;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPrivateKeySpec;

public class ClientRepository  implements  IClientRepository{


    private static final String PATH_TO_KEY = "src/cfg/Client/ClientECCKeyPair.sec";
    private PublicKey publickKey;
    private PrivateKey privateKey;
    private  String curve;


    public ClientRepository() throws Exception{
        loadKeys();
    }



    public PrivateKey getPrivateKey(){
        return privateKey;
    }


    private void loadKeys() throws  Exception{

        String curve = "secp256k1";
        String privateKeyHex = "56:b1:25:8b:7d:59:00:b8:dc:df:4d:37:f4:51:47:87:21:e2:a5:95";


        // Reconstruct private key
        ECNamedCurveParameterSpec spec = org.bouncycastle.jce.ECNamedCurveTable.getParameterSpec(curve);
        ECParameterSpec params = new ECNamedCurveSpec(
                spec.getName(),
                spec.getCurve(),
                spec.getG(),
                spec.getN(),
                spec.getH(),
                spec.getSeed());

        ECPrivateKeySpec privateKeySpec = new ECPrivateKeySpec(
                new java.math.BigInteger(privateKeyHex, 16),
                params);
        KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "BC");
        privateKey = keyFactory.generatePrivate(privateKeySpec);

    }



}
