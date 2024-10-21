
import java.io.*;
import java.net.*;
import java.security.spec.KeySpec;
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
    byte[] ciphertext = null;


    for(;;)
    {
    ServerSocket ss = new ServerSocket(port);

    // Receive on my TCP socket

    try {
      Socket s = ss.accept();
      try {
        DataInputStream is = new DataInputStream(s.getInputStream());
        ciphertext = new byte[is.readInt()];
        is.read(ciphertext);

        System.out.println("----------------------------------------------");          System.out.println("Ciphertext recebido (em HEX) ...:\n"
             +Utils.toHex(ciphertext, ciphertext.length)
             + " Size: " +ciphertext.length);

      } 
      finally {
        try {
          s.close();
        } catch (Exception e) {

          // Nothing by now ... Exception handler if you want
        } 
      } 
    } 

    finally {
      try {
        ss.close();
      } catch (Exception e) {

        // Nothing by now ... Exception Handler if you want
      } 
    }

    // Decryption

    System.out.println("Decrypt received ciphertext ...");

    Cipher cipher = Cipher.getInstance(ciphersuite);
    cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
  
    byte[] plainText= new byte[cipher.getOutputSize(ciphertext.length)];
    int ptLength=cipher.update(ciphertext,0, ciphertext.length, plainText,0);
    
    ptLength += cipher.doFinal(plainText, ptLength);
    System.out.println("Plaintext in HEX: "+ Utils.toHex(plainText, ptLength) +
		       " Size: " +ptLength );

    String msgoriginal= new String(plainText);

    System.out.println("----------------------------------------------");      
    System.out.println("MSG Original Plaintext: "+ msgoriginal );
    System.out.println("----------------------------------------------");      
    }
  } 
}

