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
import java.util.ArrayList;
import java.util.List;

public class ServerRepository implements IServerRepository {

    String PATH_TO_USER_DATABASE = "src/cfg/Server/userdatabase.txt";
    List<User> Users;


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



    public PublicKey getUserPublicKey(String userId) throws Exception{
        String pubKeyXHex = "676e313a4c371cd09d2249ec006066b869b794f031f319603e34b90ccd00feec";
        String pubKeyYHex = "acddeda86501ce1fa8c0fc9d339d18b2231fc96bae7b73afee8a95ace4fdc91a";
        String curve = "secp256k1";


        //generate the Eliptic curve
        ECNamedCurveParameterSpec spec = org.bouncycastle.jce.ECNamedCurveTable.getParameterSpec(curve);
        ECParameterSpec params = new ECNamedCurveSpec(
                spec.getName(),
                spec.getCurve(),
                spec.getG(),
                spec.getN(),
                spec.getH(),
                spec.getSeed());

        //generate the key factory
        KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "BC");


        // Reconstruct public key, by the coordinate of the Eliptic curve
        org.bouncycastle.math.ec.ECPoint bcPoint = spec.getCurve().createPoint(
                new java.math.BigInteger(pubKeyXHex, 16),
                new java.math.BigInteger(pubKeyYHex, 16));


        //AI
        // Convert BouncyCastle ECPoint to java.security.spec.ECPoint
        java.security.spec.ECPoint javaPoint = new java.security.spec.ECPoint(
                bcPoint.getAffineXCoord().toBigInteger(),
                bcPoint.getAffineYCoord().toBigInteger()
        );

        ECPublicKeySpec publicKeySpec = new ECPublicKeySpec(javaPoint, params);
        return keyFactory.generatePublic(publicKeySpec);

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
                result.add(new User(elements[0], elements[1]));

                for(String i: elements){
                    System.out.println(i);
                }
            }
        }
        return result;
    }
}
