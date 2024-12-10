import Objects.MessageType;
import Objects.SHPSocket;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Client {

    public static void main(String[] args) throws Exception {
        Socket socket = null;
        DataInputStream inputStream = null;
        DataOutputStream outputStream = null;

        try {
            // Connect to the server on localhost and port 5000
            socket = new Socket("localhost", 5000);
            System.out.println("Connected to server.");

            // Get the input and output streams from the socket
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());

            // Prepare a byte[] to send to the server
            String message = "hello server";

            SHPSocket shpSocket = new SHPSocket(MessageType.TYPE1, message.getBytes(StandardCharsets.UTF_8));

            // Send the byte[] to the server
            outputStream.writeInt(shpSocket.getSocketContent().length);  // Send the length of the byte array first
            outputStream.write(shpSocket.getSocketContent());  // Send the byte array


            System.out.println("Sent message in String to server: " + message);
            System.out.println("Sent message in byte[] to server: " + shpSocket.getSocketContent());






            //wait for server side response

            // Read the modified byte[] from the server
            int byteArrayLength = inputStream.readInt();  // First, read the length of the byte array
            byte[] receivedData = new byte[byteArrayLength];
            inputStream.readFully(receivedData);  // Read the actual byte array

            // Print the modified byte[] received from the server
            System.out.println("Received modified byte[] from server: " + new String(receivedData));

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
