import BusinessLogic.DeciferResult.DeciferResult;
import BusinessLogic.ISHPCifer;
import BusinessLogic.ISHPDecifer;
import BusinessLogic.SHPCifer;
import BusinessLogic.SHPDecifer;
import Objects.MessageType;
import Objects.SHPSocket.SHPSocket;
import Objects.SHPSocket.SHPSocketUtils;

import java.io.*;
import java.net.*;

public class Main {

    static ISHPCifer _cifer;
    static ISHPDecifer _decifer;

    public static void main(String[] args) {
        _cifer = new SHPCifer();
        _decifer = new SHPDecifer();

        SHPSocketUtils utils = new SHPSocketUtils();

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
            System.out.println(" ----------------- ");

            // Get the input and output streams from the client socket
            inputStream = new DataInputStream(clientSocket.getInputStream());
            outputStream = new DataOutputStream(clientSocket.getOutputStream());




            // Read the byte[] from the client
            int byteArrayLength = inputStream.readInt();
            byte[] receivedEncryptedData = new byte[byteArrayLength];
            inputStream.readFully(receivedEncryptedData);

            DeciferResult receivedMessage = _decifer.getPayload(utils.getMessageType(receivedEncryptedData), receivedEncryptedData);

            // Process the received byte[] into string
            System.out.println("Received the message: " + receivedMessage.toString());



            //SENDING ANOTHER MEASSGE TYPE 2 JUST FOR TEST
            // -------------------------------------------------------------------------------------------------------
            String message = "Hi, " + receivedMessage.toString() + " we are glad to comunicate with you";
            SHPSocket shpSocket = new SHPSocket(MessageType.TYPE2, _cifer.createPayload(MessageType.TYPE2));

            // Send the byte[] to the server
            outputStream.writeInt(shpSocket.getSocketContent().length);  // Send the length of the byte array first
            outputStream.write(shpSocket.getSocketContent());  // Send the byte array

            System.out.println("Sent this message to server: " + message);
            // -------------------------------------------------------------------------------------------------------



        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
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
