/**
 * Material/Labs for SRSC 21/22, Sem-1
 * hj
 **/

// GenerateKey.java

import java.io.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.KeySpec;

public class GenerateKey {



  public static final String ALGORITHM = "AES";
  public static final Integer KEYSIZE = 256;     // 128, 256 bits
  public static final String KEYRING = "keyring";
    

  /**
   * main()
   */

  public static void main(String[] args) throws Exception {

    // Key generation for the chosen Alg. 

    KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM);
    kg.init(KEYSIZE);
    SecretKey key = kg.generateKey();

    // We will store in a file (as a keyring, as a keystore file)
    // ... Good idea ? Better idea to store/manage the key more securely?

    OutputStream os = new FileOutputStream(KEYRING);
    try {
      os.write(key.getEncoded());
      System.out.println("----------------------------------------------");
      System.out.println("Key " +ALGORITHM +" with "+KEYSIZE +" bits ");
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









