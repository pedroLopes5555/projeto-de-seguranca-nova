/**
 * Material/Labs for SRSC 21/22, Sem-1
 * hj
 **/

// GenerateKey.java

import java.io.*;
import java.util.HexFormat;
import javax.crypto.*;

public class GenerateKey {



  public static final String ALGORITHM = "AES";
  public static final Integer KEYSIZE = 256;     // 128, 256 bits
  public static final String KEYRING = "keyring";
    
  public static String bytesToHex(byte[] bytes) {
    // Using Java 17's HexFormat for conversion to Hex string
    return HexFormat.of().formatHex(bytes);
  }

  /**
   * main()
   */

  public static void main(String[] args) throws Exception {

    // Key generation for the chosen Alg. 

    KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM);
    kg.init(KEYSIZE);
    SecretKey key = kg.generateKey();

    byte[] keyBytes = key.getEncoded();

    String hexKey = bytesToHex(keyBytes);


    
    

    // We will store in a file (as a keyring, as a keystore file)
    // ... Good idea ? Better idea to store/manage the key more securely?

    OutputStream os = new FileOutputStream(KEYRING);
    try {
      os.write(key.getEncoded());
      System.out.println("----------------------------------------------");
      System.out.println("Key " +ALGORITHM +" with "+KEYSIZE +" bits ");
      System.out.println("Key in hexadecimal: \n" + hexKey +"\n");
      System.out.println("Key stored in the file  " + KEYRING + "...");
      System.out.println("----------------------------------------------");

    } 
    finally {
      try {
        os.close();
      } catch (Exception e) {

        // ... Nothing by now ... Your exception handler if/when required

      } 
    } 
  } 

}









