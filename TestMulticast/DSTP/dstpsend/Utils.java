package DSTP.dstpsend;
/**
 * Material/Labs para SRSC 
 **/

/**
 * Auxiliar
 * Some conversion functions
 */
public class Utils
{
    private static String	digits = "0123456789abcdef";
    
    /**
     * Return string hexadecimal from byte array of certain size
     * 
     * @param data : bytes to convert
     * @param length : nr of bytes in data block to be converted
     * @return  hex : hexadecimal representation of data
     */

   public static String toHex(byte[] data, int length)
    {
        StringBuffer	buf = new StringBuffer();
        
        for (int i = 0; i != length; i++)
        {
            int	v = data[i] & 0xff;
            
            buf.append(digits.charAt(v >> 4));
            buf.append(digits.charAt(v & 0xf));
        }
        
        return buf.toString();
    }
    
    /**
     * Return data in byte array from string hexadecimal
     * 
     * @param data : bytes to be converted
     * @return : hexadecimal repersentatin of data
     */
    public static String toHex(byte[] data)
    {
        return toHex(data, data.length);
    }


    public static void printInRed(String message){
        // ANSI escape code for red text
        String red = "\u001B[31m";
        // ANSI escape code to reset to default
        String reset = "\u001B[0m";
        System.out.println(red + message + reset);
    }

    public static byte[] toByteArray(String var0) {
        byte[] var1 = new byte[var0.length()];
        char[] var2 = var0.toCharArray();
  
        for(int var3 = 0; var3 != var2.length; ++var3) {
           var1[var3] = (byte)var2[var3];
        }
  
        return var1;
     }



     //chat gpt function:
     public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
    

        // Method to convert an integer to a 16-bit byte array
        public static byte[] intToByteArray(int value) {
            // Create a 2-byte array for 16 bits
            byte[] byteArray = new byte[2];
    
            // Extract high byte and low byte
            byteArray[0] = (byte) ((value >> 8) & 0xFF); // High byte
            byteArray[1] = (byte) (value & 0xFF);        // Low byte
    
            return byteArray;
        }

    // Function to combine two byte arrays
    public static byte[] combineArrays(byte[] array1, byte[] array2) {
        byte[] combined = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, combined, 0, array1.length);
        System.arraycopy(array2, 0, combined, array1.length, array2.length);
        return combined;
    }

    
    // Method to convert a 16-bit byte array to a decimal integer
    public static int byteArrayToInt(byte[] byteArray) {
        // Check if the input array is of size 2
        if (byteArray.length != 2) {
            throw new IllegalArgumentException("Input byte array must be 16 bits (2 bytes).");
        }

        // Combine bytes to form the decimal value
        int highByte = byteArray[0] & 0xFF; // Mask to ensure it's treated as unsigned
        int lowByte = byteArray[1] & 0xFF;  // Mask to ensure it's treated as unsigned

        // Return the combined integer
        return (highByte << 8) | lowByte;
    }



}



