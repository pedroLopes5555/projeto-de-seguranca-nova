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

public class Client {

    static ISHPCifer _cifer;
    static ISHPDecifer _decifer;

    public static void main(String[] args) throws Exception {

        _cifer = new SHPCifer();
        _decifer = new SHPDecifer();

        SHPSocketUtils utils = new SHPSocketUtils();

        Socket socket = null;
        DataInputStream inputStream = null;
        DataOutputStream outputStream = null;

        try {
            // Connect to the server on localhost and port 5000
            socket = new Socket("localhost", 5000);
            System.out.println("Connected to server.");
            System.out.println(" ----------------- ");

            // Get the input and output streams from the socket
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());




            // Sending message type 1 into the server.

            // Creating the socket with the userId
            String userId = "valentimspaulo@gmail.com";
            SHPSocket shpSocket = new SHPSocket(MessageType.TYPE1, _cifer.createPayload(MessageType.TYPE1, userId));

            // Send the byte[] to the server
            outputStream.writeInt(shpSocket.getSocketContent().length);  // Send the length of the byte array first
            outputStream.write(shpSocket.getSocketContent());  // Send the byte array

            System.out.println("Sent this message to server: " + userId);



            //RECEIVING ANOTHER MEASSGE TYPE 1 JUST FOR TEST
            // -------------------------------------------------------------------------------------------------------
            int byteArrayLength = inputStream.readInt();
            byte[] receivedEncryptedData = new byte[byteArrayLength];
            inputStream.readFully(receivedEncryptedData);

            DeciferResult receivedMessage = _decifer.getPayload(utils.getMessageType(receivedEncryptedData), receivedEncryptedData);

            // Process the received byte[] into string
            System.out.println("Received the message: " + receivedMessage.toString());
            // -------------------------------------------------------------------------------------------------------



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
