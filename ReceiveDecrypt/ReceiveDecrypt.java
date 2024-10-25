
import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.util.Arrays;
import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * Decifra
 */
public class ReceiveDecrypt {

  public static void main(String args[]) throws Exception {

    Integer  port=5999; 

    // ANSI escape code for red text
    String red = "\u001B[31m";
    // ANSI escape code to reset to default
    String reset = "\u001B[0m";


    // Load data
    IConfigReader configReader = new ConfigReader();
    var config = configReader.getConfig();
    //var keys = configReader.getkeys();  


    String ciphersuite = config.get("CONFIDENTIALITY");  // Retrieve the ciphersuite

    // --------------------- Check if it is GCM mode
    int index = ciphersuite.indexOf("/");
    String ciphersuiteMode = "";
          
    if (index != -1) {
      ciphersuiteMode = ciphersuite.split("/")[1];
    }
    // --------------------- Check if it is GCM mode

    byte[] ivBytes= new byte[] {
      0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
      0x08, 0x09, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15
        };

    IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
    GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, ivBytes);

    System.out.println("\nWait cyphertext o port: " +port);
    System.out.println("Ciphersuite:" +ciphersuite );
    System.out.println();

    SecretKey key = KeyRing.readSecretKey(config.get("SYMMETRIC_KEY") , config.get("CONFIDENTIALITY").substring(0,3));
    byte[] UDPPayload = null;


    for(;;)
    {
    ServerSocket ss = new ServerSocket(port);


    try {
      Socket s = ss.accept();
      try {
        DataInputStream is = new DataInputStream(s.getInputStream());
        UDPPayload = new byte[is.readInt()];
        is.read(UDPPayload);
        System.out.println("----------------------------------------------");
            
      } 
      finally {
        try {
          s.close();
        } catch (Exception e) {

        } 
      } 
    } 

    finally {
      try {
        ss.close();
      } catch (Exception e) {
      } 
    }


    Cipher cipher = Cipher.getInstance(ciphersuite);
    if(ciphersuiteMode.toUpperCase().equals("GCM")){
			cipher.init(Cipher.DECRYPT_MODE, key, gcmParameterSpec);
		}else{
			cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
		}
    



    MessageDigest hash = MessageDigest.getInstance(config.get("H"));

    int cyphertextSize = UDPPayload.length - hash.getDigestLength(); 
    byte[] ciphertext = Arrays.copyOfRange(UDPPayload, 0, cyphertextSize);
    byte[] receivedHash = Arrays.copyOfRange(UDPPayload, cyphertextSize, cyphertextSize + hash.getDigestLength());


    hash.update(ciphertext);
    byte[] computedHash = hash.digest();

    Boolean isMessageValid = Arrays.equals(computedHash, receivedHash);

        





    if(!isMessageValid){
      System.out.println(red + "Packet Integrity not conformed!" + reset);
    }else{
      Utils.printInRed(isMessageValid.toString());
    }
    

    //decypher plaintext
    byte[] plainText= new byte[cipher.getOutputSize(ciphertext.length)];
    int ptLength=cipher.update(ciphertext,0, ciphertext.length, plainText,0);
    ptLength += cipher.doFinal(plainText, ptLength);
    String msgoriginal= new String(plainText);
    System.out.println("----------------------------------------------");      
    System.out.println("MSG Original Plaintext: "+ msgoriginal );
  

   
    }
  } 
}

