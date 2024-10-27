package dstpsend;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;


public class KeyRing {



  public static final String ALGORITHM = "AES"; 
  public static final String KEYRING = "keyring"; 


  public static SecretKey hexStringToSecretKey(String hexKey, String algorithm) {
    byte[] keyBytes = hexStringToByteArray(hexKey);
    return new SecretKeySpec(keyBytes, algorithm);
  }
  
  //Convert a Hex String to a byte array (chat gpt method)
  public static byte[] hexStringToByteArray(String s) {
    int len = s.length();
    byte[] data = new byte[len / 2];


    for (int i = 0; i < len; i += 2) {
      data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
    }

    return data;
  }




  public static SecretKey readSecretKey(String keyInString, String algorithm) throws Exception {

    return hexStringToSecretKey(keyInString, algorithm);
  } 
}

