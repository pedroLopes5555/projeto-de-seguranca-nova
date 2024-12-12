package Repository;

import Objects.User;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;

import java.io.*;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.*;
import java.util.ArrayList;
import java.util.Base64;

public class ClientRepository  implements  IClientRepository{


    private static final String PATH_TO_KEY = "src/cfg/Client/ClientECCKeyPair.sec";
    private PublicKey publickKey;
    private PrivateKey privateKey;
    private  String curve;
    private String privKeyBase64;
    private String pubKeyBase64;


    public ClientRepository() throws Exception{
        loadKeys();
    }



    public PrivateKey getPrivateKey(){
        return privateKey;
    }


    private void loadKeys() throws  Exception{
        loadBase64Keys();

        /*Generate Priv Key*/
        //decode the base 64 privKey
        byte[] privKeyEncoded = Base64.getDecoder().decode(privKeyBase64);
        KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "BC");
        EncodedKeySpec KeySpec = new PKCS8EncodedKeySpec(privKeyEncoded);
        this.privateKey =  keyFactory.generatePrivate(KeySpec);

        /*now we will generate the pubKey*/

        byte[] decodePubFromBase64 = Base64.getDecoder().decode(pubKeyBase64);
        KeyFactory keyFactory_2 = KeyFactory.getInstance("ECDSA", "BC");
        EncodedKeySpec KeySpec_2 = new X509EncodedKeySpec(decodePubFromBase64);
        this.publickKey = keyFactory_2.generatePublic(KeySpec_2);


    }



    private void loadBase64Keys() throws IOException {
        File file = new File(PATH_TO_KEY);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;


        while ((st = br.readLine()) != null){
            if(st.startsWith("Curve")){
                this.curve = st.split(":")[1];
            }

            if(st.startsWith("PrivateKey")){
                this.privKeyBase64 = st.split(":")[1];
            }

            if(st.startsWith("PublicKey")){
                this.pubKeyBase64 = st.split(":")[1];
            }


        }
    }


}
