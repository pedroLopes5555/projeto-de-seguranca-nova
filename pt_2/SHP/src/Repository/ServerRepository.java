package Repository;

import Objects.User;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ServerRepository implements IServerRepository {

    private String PATH_TO_USER_DATABASE = "src/cfg/Server/userdatabase.txt";
    private List<User> Users;
    private PrivateKey privateKey;
    private  String curve;
    private String privKeyBase64;
    private String pubKeyBase64;

    private static final String PATH_TO_KEY = "src/cfg/Server/ServerECCKeyPair.sec";



    public ServerRepository() throws Exception {
        Users = loadUsers();
        loadKey();
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public User getUserById(String userId) {
        for (User item : Users) {
            if(item.getUserId().equals(userId)){
                return item;
            }
        }
        return null;
    }


    public PublicKey createPublicKey(String bas64PubKey) throws Exception{

        /*now we will generate the pubKey*/

        byte[] decodePubFromBase64 = Base64.getDecoder().decode(bas64PubKey);
        KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "BC");
        EncodedKeySpec KeySpec = new X509EncodedKeySpec(decodePubFromBase64);
        return keyFactory.generatePublic(KeySpec);
    }



    private List<User> loadUsers() throws Exception{
        var result = new ArrayList<User>();
        File file = new File(PATH_TO_USER_DATABASE);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;


        while ((st = br.readLine()) != null){
            if(st.charAt(0) != '#'){
                //System.out.println("linha: " + st);
                String[] elements = st.split(":");
                result.add(new User(elements[0], elements[1], elements[2].getBytes(), createPublicKey(elements[3])));

                for(String i: elements){
                    System.out.println(i);
                }
            }
        }
        return result;
    }

    private void loadKey() throws Exception {
        loadBase64Keys();

        /*Generate Priv Key*/
        //decode the base 64 privKey
        byte[] privKeyEncoded = Base64.getDecoder().decode(privKeyBase64);
        KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "BC");
        EncodedKeySpec KeySpec = new PKCS8EncodedKeySpec(privKeyEncoded);
        this.privateKey =  keyFactory.generatePrivate(KeySpec);
    }

    private void loadBase64Keys() throws Exception {
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
