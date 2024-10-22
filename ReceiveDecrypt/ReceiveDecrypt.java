
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

    String ciphersuite="AES/CTR/NoPadding";

    if (args.length==1) {
	    port=Integer.parseInt(args[0]);
    }

    if (args.length==2) {
      port=Integer.parseInt(args[0]);
      ciphersuite=args[1];
    }


    byte[] ivBytes= new byte[] {
      0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
      0x08, 0x09, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15
        };

    IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

    System.out.println("\nWait cyphertext o port: " +port);
    System.out.println("Ciphersuite:" +ciphersuite );
    System.out.println();

    SecretKey key = KeyRing.readSecretKey(); 
    byte[] UDPPayload = null;


    for(;;)
    {
    ServerSocket ss = new ServerSocket(port);

    // Receive on my TCP socket

    try {
      Socket s = ss.accept();
      try {
        DataInputStream is = new DataInputStream(s.getInputStream());
        UDPPayload = new byte[is.readInt()];
        is.read(UDPPayload);

        System.out.println("----------------------------------------------");          System.out.println("Ciphertext recebido (em HEX) ...:\n"
             +Utils.toHex(UDPPayload, UDPPayload.length)
             + " Size: " +UDPPayload.length);

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

    // Decryption

    System.out.println("Decrypt received ciphertext ...");

    Cipher cipher = Cipher.getInstance(ciphersuite);
    cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
    MessageDigest hash = MessageDigest.getInstance("SHA1");


    //cyphertext size is the payload size - hash size
    
    //create a ashe just to get the size
		byte[] digest = hash.digest();

    int cyphertextSize = UDPPayload.length - digest.length;
    
    //decompose the udpPayload
    byte[] ciphertext = Arrays.copyOfRange(UDPPayload, 0, cyphertextSize);
    byte[] receivedHash = Arrays.copyOfRange(UDPPayload, cyphertextSize, cyphertextSize + digest.length);

    
    

    //decypher plaintext
    byte[] plainText= new byte[cipher.getOutputSize(ciphertext.length)];
    int ptLength=cipher.update(ciphertext,0, ciphertext.length, plainText,0);
    ptLength += cipher.doFinal(plainText, ptLength);
    System.out.println("Plaintext in HEX: "+ Utils.toHex(plainText, ptLength) +
		       " Size: " +ptLength );
    String msgoriginal= new String(plainText);
    System.out.println("----------------------------------------------");      
    System.out.println("MSG Original Plaintext: "+ msgoriginal );
  

    //integrity test by hashing the cyphertext
    hash.update(ciphertext);
		digest = hash.digest();
    System.out.println("hash comparaction : " + Arrays.equals(digest, receivedHash));

    System.out.println("----------------------------------------------");      
    }
  } 
}

