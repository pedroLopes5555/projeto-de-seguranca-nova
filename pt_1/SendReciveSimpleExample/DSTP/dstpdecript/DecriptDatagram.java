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

  public static EncriptedDatagramResoult GetDecriptedDatagram(byte[] UDPDatagram, String CfgFilePathString) throws Exception {
		
		// Load data
		IConfigReader configReader = new ConfigReader(CfgFilePathString);
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


    SecretKey key = KeyRing.readSecretKey(config.get(ConfigKey.SYMMETRIC_KEY.getValue()) , config.get(ConfigKey.CONFIDENTIALITY.getValue()).substring(0,3));


    Cipher cipher = Cipher.getInstance(ciphersuite);
    if(ciphersuiteMode.toUpperCase().equals("GCM")){
			cipher.init(Cipher.DECRYPT_MODE, key, gcmParameterSpec);
		}else{
			cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
		}
    

      // Extract the version (2 bytes), release (1 byte), and payload length (2 bytes)
      // Version -> 16 bits
      byte[] version = Arrays.copyOfRange(UDPDatagram, 0, 2);
      
      // Release -> 8 bits
      byte[] release = Arrays.copyOfRange(UDPDatagram, 2, 3);


      
      // Payload length -> 16 bits (2 bytes)
      byte[] payloadLenBytes = Arrays.copyOfRange(UDPDatagram, 3, 5);
      
      // Convert payload length from bytes to an integer
      int payloadLength = ((payloadLenBytes[0] & 0xFF) << 8) | (payloadLenBytes[1] & 0xFF);
      
      // Calculate header length
      int headerLen = version.length + release.length + payloadLenBytes.length;
      
      // Extract the UDP payload based on the payload length
      byte[] UDPPayload = Arrays.copyOfRange(UDPDatagram, headerLen, headerLen + payloadLength);
      


    var integrity = config.get(ConfigKey.INTEGRITY.getValue());
      
      switch (integrity) {
          case "H" -> {
                // Divide Datagram
              MessageDigest hash = MessageDigest.getInstance(config.get(ConfigKey.H.getValue()));
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


              return  new EncriptedDatagramResoult(plainText, sequenceNumber);
		          
          }
          case "HMAC" -> {

            Mac hMac = Mac.getInstance(config.get(ConfigKey.MAC.getValue()));

            SecretKeySpec hMacKey = new SecretKeySpec(
                    Utils.hexStringToByteArray(config.get(ConfigKey.MACKEY.getValue())),
                    config.get(ConfigKey.MAC.getValue()));
                  
            int digestSize = Integer.parseInt(config.get(ConfigKey.MACKEY_SIZE.getValue())) / 8;

            //calculate size of ciphertext
            int cyphertextSize = UDPPayload.length - digestSize;
            //extract ciphertext from UDPPayload (starts at index 0, after the header)
            byte[] sequenceNumberAndPTextCiphered = Arrays.copyOfRange(UDPPayload, 0, cyphertextSize);
            //extract received hash (starts immediately after the ciphertext)
            byte[] receivedHash = Arrays.copyOfRange(UDPPayload, cyphertextSize, UDPPayload.length);

            //recalculate original message
            byte[] plainTextAndSequenceNumber= new byte[cipher.getOutputSize(sequenceNumberAndPTextCiphered.length)];

            int ptLength=cipher.update(sequenceNumberAndPTextCiphered,0, sequenceNumberAndPTextCiphered.length, plainTextAndSequenceNumber,0);
            ptLength += cipher.doFinal(plainTextAndSequenceNumber, ptLength);



            //inicialize hashe
            hMac.init(hMacKey);
            hMac.update(sequenceNumberAndPTextCiphered); 
            byte[] digest = hMac.doFinal();


            Boolean isMessageValid = Arrays.equals(digest, receivedHash);
              
            if(!isMessageValid){
              Utils.printInRed("Integrity not confirmed!!");
              //System.out(o);
            }
            

            //now take the sequence number out of the plainTextAndSequenceNumber
            byte[] sequenceNumber = Arrays.copyOfRange(plainTextAndSequenceNumber, 0, 2);
            byte[] plainText = Arrays.copyOfRange(plainTextAndSequenceNumber, 2, plainTextAndSequenceNumber.length);
        

            return  new EncriptedDatagramResoult(plainText, sequenceNumber);


          }
          default -> {
              Utils.printInRed("Not Valid Integrity Field ->  INTEGRITY:" + integrity);
              System.exit(0);
          }
      }
   
    return null;
  } 
}
