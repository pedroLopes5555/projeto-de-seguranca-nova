import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigReader {

    public static void main(String[] args) {
        String configFilePath = "cryptoconfig.txt";  // Path to the configuration file
        Map<String, String> configMap = new HashMap<>();
        
        // Read the configuration file
        try (BufferedReader br = new BufferedReader(new FileReader(configFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Skip empty lines or lines that don't contain a colon
                if (line.trim().isEmpty() || !line.contains(":")) {
                    continue;
                }

                // Split the line at the colon ':' to get the key-value pair
                String[] keyValue = line.split(":", 2);
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim();
                    String value = keyValue[1].trim();
                    configMap.put(key, value);  // Store in the HashMap
                    System.out.println("chave : " + key + " valor + " + value);
                } else {
                    System.out.println("Invalid configuration entry: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String key : configMap.keySet()) {
            System.out.println("Key: " + key + ", Value: " + configMap.get(key));
        }
    }
}
