package src;
import java.io.*;
import java.net.*;

public class Main {
    public static void main(String[] args) {
        String hostname = "localhost"; // Server's hostname or IP address
        int port = 6789; // Server's port number
        
        try (Socket socket = new Socket(hostname, port)) {
            System.out.println("Connected to the server");

            // Create input and output streams for communication
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            // Send a message to the server
            String message = "Hello, Server!";
            output.println(message);
            System.out.println("Sent: " + message);

            // Read response from the server
            String serverResponse = input.readLine();
            System.out.println("Received: " + serverResponse);

        } catch (UnknownHostException ex) {
            System.err.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.err.println("I/O error: " + ex.getMessage());
        }
    }
}
