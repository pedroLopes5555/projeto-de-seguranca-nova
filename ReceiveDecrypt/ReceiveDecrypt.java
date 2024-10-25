import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;  


/**
 * Decifra
 */
public class ReceiveDecrypt {

  public static void main(String args[]) throws Exception {

    Integer  port=5999; 

    // Load data
    IConfigReader configReader = new ConfigReader();
    var config = configReader.getConfig();
    //var keys = configReader.getkeys();  


    String ciphersuite = config.get(ConfigKey.CONFIDENTIALITY.getValue());  // Retrieve the ciphersuite

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

    SecretKey key = KeyRing.readSecretKey(config.get(ConfigKey.SYMMETRIC_KEY.getValue()) , config.get(ConfigKey.CONFIDENTIALITY.getValue()).substring(0,3));
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
    if(ciphersuiteMode.toUpperCase().equals("GCM")){
      System.out.println("using CGM");
			cipher.init(Cipher.DECRYPT_MODE, key, gcmParameterSpec);
		}else{
			cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
		}
    


		// Version -> 16 bits
    byte[] version = new byte[] { 0x00, 0x01 };
    // Release -> 8 bits
    byte[] release = new byte[] { 0x01 };
    // Payload length -> 16 bits
    byte[] payloadLen = new byte[] { 0x00, 0x01 };
    int headerLen = version.length + release.length + payloadLen.length;

    // Divide Datagram
    MessageDigest hash = MessageDigest.getInstance(config.get(ConfigKey.H.getValue()));
    byte[] UDPPayload = Arrays.copyOfRange(UDPDatagram, headerLen, UDPDatagram.length);



    var integrity = config.get(ConfigKey.INTEGRITY.getValue());
      
      switch (integrity) {
          case "H" -> {
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
              
              if(!isMessageValid){
                Utils.printInRed("Integrity not confirmed!!");
                //System.out(o);
              }
              
              //recalculate original message
              byte[] plainText= new byte[cipher.getOutputSize(ciphertext.length)];
              int ptLength=cipher.update(ciphertext,0, ciphertext.length, plainText,0);
              ptLength += cipher.doFinal(plainText, ptLength);

              String originalMessage = new String(plainText);
              System.out.println("----------------------------------------------");
              System.out.println("MSG Original Plaintext: "+ originalMessage);
          }
          case "HMAC" -> {

            //hget the size of the HMAC from the config
            Mac hMac = Mac.getInstance(config.get(ConfigKey.MAC.getValue()));
            byte[] hmacKeyBytes = Utils.hexStringToByteArray(config.get(ConfigKey.MACKEY.getValue()));
            SecretKeySpec hMacKey = new SecretKeySpec(hmacKeyBytes, config.get(ConfigKey.MAC.getValue()));
            hMac.init(hMacKey);
            
            // Calculate the size of the ciphertext
            int hmacSize = hMac.getMacLength();
            int ciphertextSize = UDPPayload.length - hmacSize;
            
            //thate the ciphertext and HMAC from UDPPayload
            byte[] ciphertext = Arrays.copyOfRange(UDPPayload, 0, ciphertextSize);
            byte[] receivedHmac = Arrays.copyOfRange(UDPPayload, ciphertextSize, UDPPayload.length);

            


                              // Decrypt the ciphertext
            byte[] plainText = new byte[cipher.getOutputSize(ciphertext.length)];
            int ptLength = cipher.update(ciphertext, 0, ciphertext.length, plainText, 0);
            ptLength += cipher.doFinal(plainText, ptLength);


            //calc HMAC for the ciphertext
            hMac.update(plainText);
            byte[] computedHmac = hMac.doFinal();

            // Check integrity
            boolean isHmacValid = Arrays.equals(computedHmac, receivedHmac);

            if(!isHmacValid){
              System.out.println("Integrity Test Failed");
              System.exit(0);
            } 
            
              }
          default -> {
              Utils.printInRed("Not Valid Integrity Field ->  INTEGRITY:" + integrity);
              System.exit(0);
          }
      }
   
    }
  } 
}
