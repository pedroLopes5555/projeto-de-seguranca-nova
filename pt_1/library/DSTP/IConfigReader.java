package DSTP;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.*;




public interface IConfigReader {
    public Map<String, String> getConfig();
    public Map<String, String> getConfigFromString(String cfg);
    public  List<String> getkeys();
}



class ConfigReader implements   IConfigReader{


    private String _configFilePath;

    public ConfigReader(String configFilePath) {
        _configFilePath = configFilePath;
    }



    public ConfigReader() {
    }
    

    
    public Map<String, String> getConfig() {
        
        Map<String, String> result = new HashMap<>();

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
                    String value = keyValue[1].trim();
                    result.put(key, value);  // Store in the HashMap
                    //System.out.println("chave : " + key + " valor + " + value);
                } else {
                    System.out.println("Invalid configuration entry: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }




    public Map<String, String> getConfigFromString(String cfg){

        Map<String, String> resultMap = new HashMap<>();
        
        // Split the input string into lines
        String[] lines = cfg.split("\\n");
        
        // Process each line
        for (String line : lines) {
            // Split the line into key and value at the first colon
            String[] keyValue = line.split(":", 2); // Limit to 2 parts to handle values with colons
            if (keyValue.length == 2) {
                resultMap.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        
        return resultMap;

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
