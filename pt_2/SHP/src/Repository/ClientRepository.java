package Repository;

import Objects.User;
import org.bouncycastle.jce.ECNamedCurveTable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECPrivateKeySpec;

public class ClientRepository  implements  IClientRepository{


    private static final String PATH_TO_KEY = "src/cfg/Client/ClientECCKeyPair.sec";
    PublicKey publickKey;
    PrivateKey privateKey;
    String curve;


    public ClientRepository() throws Exception{
        loadKeys();
    }



    public byte[] getPrivateKey(){
        var a = new byte[10];

        return a;
    }





    private void loadKeys() throws  Exception{
        byte[] privateKeyBytes;

        String privateKeyString = "";
        String publicKeyString = "";

        File file = new File(PATH_TO_KEY);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        String[] lines = new String[3];
        int i = 0;
        while ((st = br.readLine()) != null){
            lines[i] = st;
            i++;
        }


        for (String line : lines) {
            if (line.startsWith("Curve:")) {
                this.curve = line.split(":")[1];
            }

            else if (line.startsWith("PrivateKey:")) {
                privateKeyString = line.split(":")[1]
                        .replace("[", "")
                        .replace("]", "")
                        .replace(":", "");

            }

            else if (line.startsWith("PublicKey:")) {
                publicKeyString = line.split(":")[1];
            }
        }


        if(privateKeyString != "" && publicKeyString != ""){
            privateKeyBytes = hexStringToByteArray(privateKeyString);
            ECPrivateKey privateKey = generatePrivateKeyFromBytes(privateKeyBytes);

        }


    }


    private ECPrivateKey generatePrivateKeyFromBytes(byte[] privateKeyBytes) throws Exception {
        // EC key pair generator uses a byte array for the private key
        ECPrivateKeySpec privateKeySpec = new ECPrivateKeySpec(new java.math.BigInteger(1, privateKeyBytes), new ECGenParameterSpec(this.curve));
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        return (ECPrivateKey) keyFactory.generatePrivate(privateKeySpec);

    }


    // Helper method to convert hex string to byte array
    private static byte[] hexStringToByteArray(String s) {
        String[] hexStringArray = s.split(":");
        byte[] byteArray = new byte[hexStringArray.length];
        for (int i = 0; i < hexStringArray.length; i++) {
            byteArray[i] = (byte) Integer.parseInt(hexStringArray[i], 16);
        }
        return byteArray;
    }

    // Helper method to convert byte array to hex string
    private static String bytesToHex(byte[] byteArray) {
        StringBuilder sb = new StringBuilder();
        for (byte b : byteArray) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

}
