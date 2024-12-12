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
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.Arrays;

public class Main {
    static ISHPCifer _cifer;
    static ISHPDecifer _decifer;


    public static void main(String[] args) {

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

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

            Thread.sleep(1000);

            // Recive the message type1
            int byteArrayLength = inputStream.readInt();
            byte[] receivedEncryptedData = new byte[byteArrayLength];
            inputStream.readFully(receivedEncryptedData);
            //decifer the message
            DeciferResult receivedMessage = _decifer.getPayloadType1(receivedEncryptedData);
            // Process the received byte[] into string
            String userId = receivedMessage.getValue();
            System.out.println("Received the message type 1: " + receivedMessage.toString());
            // -------------------------------------------------------------------------------------------------------
            Thread.sleep(1000);


            //return the Message type 2
            SHPSocket shpSocket = new SHPSocket(MessageType.TYPE2, _cifer.createPayloadType2());
            outputStream.writeInt(shpSocket.getSocketContent().length);  // Send the length of the byte array first
            outputStream.write(shpSocket.getSocketContent());  // Send the byte array
            System.out.println("Returned Message type 2");
            // -------------------------------------------------------------------------------------------------------
            Thread.sleep(1000);


            //Recive The message type 3
            byteArrayLength = inputStream.readInt();
            receivedEncryptedData = new byte[byteArrayLength];
            inputStream.readFully(receivedEncryptedData);
            var type3Result = _decifer.getPayloadType3(receivedEncryptedData, userId);
            System.out.println("Received type 3");
            System.out.println(type3Result.toString());




            //return the Message type 4
            shpSocket = new SHPSocket(MessageType.TYPE4, _cifer.createPayloadType4(userId, "streaming",type3Result.getNonce4()));
            outputStream.writeInt(shpSocket.getSocketContent().length);  // Send the length of the byte array first
            outputStream.write(shpSocket.getSocketContent());  // Send the byte array
            System.out.println("Returned Message type 4");
            // -------------------------------------------------------------------------------------------------------

            Thread.sleep(1000);


            //Recive The message type 5
            byteArrayLength = inputStream.readInt();
            receivedEncryptedData = new byte[byteArrayLength];
            inputStream.readFully(receivedEncryptedData);
            var type5Result = _decifer.getPayloadType5(receivedEncryptedData);

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
