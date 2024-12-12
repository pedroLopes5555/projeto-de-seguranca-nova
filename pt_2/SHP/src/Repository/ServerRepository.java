package Repository;

import Objects.User;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ServerRepository implements IServerRepository {

    private String PATH_TO_USER_DATABASE = "src/cfg/Server/userdatabase.txt";
    private List<User> Users;



    public ServerRepository() throws Exception {
        Users = loadUsers();
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
}
