package DSTP.dstpdecript;
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
public class DecriptDatagram {

  public static byte[] GetDecriptedDatagram(byte[] UDPDatagram) throws Exception {
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

    // --------------------- Get ivBytes from cfg
    String ivHex = config.get(ConfigKey.IV.getValue());
    if(ivHex.length()%2 != 0){
      ivHex += ivHex.charAt(ivHex.length()-1);
    }

    byte[] ivBytes= new byte[ivHex.length()/2];

    for (int i = 0; i < ivHex.length(); i += 2) {
          ivBytes[i / 2] = (byte) ((Character.digit(ivHex.charAt(i), 16) << 4)
                              + Character.digit(ivHex.charAt(i+1), 16));
      }
    // --------------------- Get ivBytes from cfg

    IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
    GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, ivBytes);

    System.out.println("Ciphersuite:" +ciphersuite );
    System.out.println();

    SecretKey key = KeyRing.readSecretKey(config.get(ConfigKey.SYMMETRIC_KEY.getValue()) , config.get(ConfigKey.CONFIDENTIALITY.getValue()).substring(0,3));


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
              byte[] plainTextAndSequenceNumber= new byte[cipher.getOutputSize(ciphertext.length)];

              int ptLength=cipher.update(ciphertext,0, ciphertext.length, plainTextAndSequenceNumber,0);
              ptLength += cipher.doFinal(plainTextAndSequenceNumber, ptLength);

              byte[] sequenceNumber = Arrays.copyOfRange(plainTextAndSequenceNumber, 0, 2);
              byte[] plainText = Arrays.copyOfRange(plainTextAndSequenceNumber, 2, plainTextAndSequenceNumber.length);
              
              System.out.println("tamanho do sequence number: " + sequenceNumber.length);
              System.out.println("tamanho do plaintex: " + plainText.length);
              System.out.println("tamanho dos 2 juntos: " + plainTextAndSequenceNumber.length);

              System.out.println("valor do sequence number: " + Arrays.toString(sequenceNumber));
		          System.out.println("2juntos: " + Arrays.toString(plainTextAndSequenceNumber));

              return plainText;
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

            
            byte[] plainTextAndSequenceNumber= new byte[cipher.getOutputSize(ciphertext.length)];
            byte[] sequenceNumber = Arrays.copyOfRange(plainTextAndSequenceNumber, 0, 2);
            byte[] plainText = Arrays.copyOfRange(plainTextAndSequenceNumber, 2, plainTextAndSequenceNumber.length);
            

            // Decrypt the ciphertext
            int ptLength = cipher.update(ciphertext, 0, ciphertext.length, plainTextAndSequenceNumber, 0);
            ptLength += cipher.doFinal(plainTextAndSequenceNumber, ptLength);


            System.out.println("tamanho do sequence number: " + sequenceNumber.length);
            System.out.println("tamanho do plaintex: " + plainText.length);
            System.out.println("tamanho dos 2 juntos: " + plainTextAndSequenceNumber.length);

            System.out.println("valor do sequence number: " + Arrays.toString(sequenceNumber));
            System.out.println("2juntos: " + Arrays.toString(plainTextAndSequenceNumber));


            //calc HMAC for the ciphertext
            hMac.update(plainTextAndSequenceNumber);
            byte[] computedHmac = hMac.doFinal();

            // Check integrity
            boolean isHmacValid = Arrays.equals(computedHmac, receivedHmac);

            if(!isHmacValid){
              System.out.println("Integrity Test Failed");
              //System.exit(0);
            }    
            
            return plainTextAndSequenceNumber;

          }
          default -> {
              Utils.printInRed("Not Valid Integrity Field ->  INTEGRITY:" + integrity);
              System.exit(0);
          }
      }
   
    return null;
  } 
}
