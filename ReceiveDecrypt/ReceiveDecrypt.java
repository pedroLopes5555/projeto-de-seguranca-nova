
import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.util.Arrays;
import javax.crypto.*;
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


    byte[] ivBytes= new byte[] {
      0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
      0x08, 0x09, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15
        };

    IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

    System.out.println("\nWait cyphertext o port: " +port);
    System.out.println("Ciphersuite:" +ciphersuite );
    System.out.println();

    SecretKey key = KeyRing.readSecretKey(config.get("SYMMETRIC_KEY") , config.get("CONFIDENTIALITY").substring(0,3));
    byte[] UDPDatagram = null;


    for(;;)
    {
    ServerSocket ss = new ServerSocket(port);


    try {
      Socket s = ss.accept();
      try {
        DataInputStream is = new DataInputStream(s.getInputStream());
        UDPDatagram = new byte[is.readInt()];
        is.read(UDPDatagram);
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
    cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);


    
		// Version -> 16 bits
    byte[] version = new byte[] { 0x00, 0x01 };
    // Release -> 8 bits
    byte[] release = new byte[] { 0x01 };
    // Payload length -> 16 bits
    byte[] payloadLen = new byte[] { 0x00, 0x01 };
    int headerLen = version.length + release.length + payloadLen.length;

    // Divide Datagram
    MessageDigest hash = MessageDigest.getInstance(config.get("H"));
    byte[] UDPPayload = Arrays.copyOfRange(UDPDatagram, headerLen, UDPDatagram.length);

    //calculate size of ciphertext
    int cyphertextSize = UDPPayload.length - hash.getDigestLength(); 
    //extract ciphertext from UDPPayload (starts at index 0, after the header)
    byte[] ciphertext = Arrays.copyOfRange(UDPPayload, 0, cyphertextSize);
    //extract received hash (starts immediately after the ciphertext)
    byte[] receivedHash = Arrays.copyOfRange(UDPPayload, cyphertextSize, UDPPayload.length);

    //check integrity
    hash.update(ciphertext);
    byte[] computedHash = hash.digest();
    Boolean isMessageValid = Arrays.equals(computedHash, receivedHash);

    //print or handle message validity
    System.out.println("Message valid: " + isMessageValid);

    

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

