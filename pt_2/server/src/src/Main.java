package src;
import java.io.*;
import java.net.*;

public class Main {
    public static void main(String[] args) {
        int port = 6789; // Server will listen on this port
        
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                // Accept a new client connection
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                // Create input and output streams for communication
                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);

                // Read message from client
                String clientMessage = input.readLine();
                System.out.println("Received: " + clientMessage);

                // Send response to client
                output.println("Hello, you sent: " + clientMessage);

                // Close client connection
                clientSocket.close();
            }
        } catch (IOException ex) {
            System.err.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
