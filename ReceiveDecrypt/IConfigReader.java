import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.*;




public interface IConfigReader {

    public Map<String, String> getConfig();
    public  List<String> getkeys();
}



class ConfigReader implements   IConfigReader{



    public ConfigReader() {
        super();
    }
    private String _configFilePath = "cryptoconfig.txt";


    @Override
    public Map<String, String> getConfig() {
        
        Map<String, String> result = new HashMap<>();

        //Adding configuration settings to the map
        result.put("CONFIDENTIALITY", "AES/CTR/NoPadding");
        result.put("SYMMETRIC_KEY", "c4e7c66127029c05fa2e309d84a716d6d2f8cb63fd5b7753c86d37703f30969e");
        result.put("SYMMETRIC_KEY_SIZE", "256");
        result.put("IV_SIZE", "128"); // Changed "IV Size" to "IV_SIZE" for consistency
        result.put("IV", "11223344556677889900AABBCCDDEEFF");
        result.put("INTEGRITY", "H");
        result.put("H", "SHA256");
        result.put("MAC", "HMAC");
        result.put("MACKEY", "1234567890ABCDEF1234567890ABCDEF");
        result.put("MACKEY_SIZE", "256");
        // try (BufferedReader br = new BufferedReader(new FileReader(_configFilePath))) {
        //     String line;
        //     while ((line = br.readLine()) != null) {
        //         // Skip empty lines or lines that don't contain a colon
        //         if (line.trim().isEmpty() || !line.contains(":")) {
        //             continue;
        //         }
        //         // Clear unwanted characters (non-printable or special) from the line
        //         line = line.replaceAll("^[^\\x20-\\x7E]+", ""); // Keep only printable ASCII characters

        //         // Split the line at the colon ':' to get the key-value pair
        //         String[] keyValue = line.split(":", 2);
        //         if (keyValue.length == 2) {
        //             String key = keyValue[0].trim();
        //             String value = keyValue[1].trim();
        //             result.put(key, value);  // Store in the HashMap
        //             //System.out.println("chave : " + key + " valor + " + value);
        //         } else {
        //             System.out.println("Invalid configuration entry: " + line);
        //         }
        //     }
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }

        return result;
    }



    @Override
    public  List<String> getkeys() {
        List<String> stringList = new ArrayList<String>();
        

        try (BufferedReader br = new BufferedReader(new FileReader(_configFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Skip empty lines or lines that don't contain a colon
                if (line.trim().isEmpty() || !line.contains(":")) {
                    continue;
                }
                // Clear unwanted characters (non-printable or special) from the line
                line = line.replaceAll("^[^\\x20-\\x7E]+", ""); // Keep only printable ASCII characters

                // Split the line at the colon ':' to get the key-value pair
                String[] keyValue = line.split(":", 2);
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim();
                    stringList.add(key);  // Store in the HashMap
                    //System.out.println("chave : " + key + " valor + " + value);
                } else {
                    System.out.println("Invalid configuration entry: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Ensure the method always returns a result (empty map if there was an exception)
        return stringList;
    }

}
