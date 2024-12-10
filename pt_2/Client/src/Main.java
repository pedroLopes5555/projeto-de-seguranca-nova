import Objects.SHPSocket;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) throws Exception {
        String hostname = "localhost"; // Server's hostname or IP address
        int port = 6789; // Server's port number

        try (Socket socket = new Socket(hostname, port)) {
            System.out.println("Connected to the server");

            // Create input and output streams for communication
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            // Send a message to the server
            String message = "Hello, Server!";
            SHPSocket socketContent = new SHPSocket((byte) 0x01,(byte) 0x01,(byte) 0x01,message.getBytes(StandardCharsets.UTF_8));
            output.println(socketContent.getSocketContent().toString());

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