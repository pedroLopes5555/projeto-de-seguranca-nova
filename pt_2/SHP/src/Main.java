import java.io.*;
import java.net.*;

public class Main {

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        DataInputStream inputStream = null;
        DataOutputStream outputStream = null;

        try {
            // Create a ServerSocket on port 5000
            serverSocket = new ServerSocket(5000);
            System.out.println("Server started, waiting for a client to connect...");

            // Accept incoming client connection
            clientSocket = serverSocket.accept();
            System.out.println("Client connected.");

            // Get the input and output streams from the client socket
            inputStream = new DataInputStream(clientSocket.getInputStream());
            outputStream = new DataOutputStream(clientSocket.getOutputStream());

            // Read the byte[] from the client
            int byteArrayLength = inputStream.readInt();
            byte[] receivedData = new byte[byteArrayLength];
            inputStream.readFully(receivedData);

            // Process the received byte[] (for example, print it)
            System.out.println("Received byte[]: " + new String(receivedData));



            // Modify the byte array (simple example, changing all bytes to uppercase)
            for (int i = 0; i < receivedData.length; i++) {
                receivedData[i] = (byte) Character.toUpperCase((char) receivedData[i]);
            }

            // Send the modified byte[] back to the client
            outputStream.writeInt(receivedData.length);  // Send the length of the modified byte array
            outputStream.write(receivedData);  // Send the modified byte array
            System.out.println("Sent modified byte[] back to client.");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
                if (clientSocket != null) clientSocket.close();
                if (serverSocket != null) serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
