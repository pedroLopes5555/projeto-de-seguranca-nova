import BusinessLogic.DeciferResult.DeciferResult;
import BusinessLogic.ISHPCifer;
import BusinessLogic.ISHPDecifer;
import BusinessLogic.SHPCifer;
import BusinessLogic.SHPDecifer;
import Objects.MessageType;
import Objects.SHPSocket.SHPSocket;
import Objects.SHPSocket.SHPSocketUtils;
import Objects.User;

import java.io.*;
import java.net.*;
import java.security.Security;

public class Client {

    static ISHPCifer _cifer;
    static ISHPDecifer _decifer;

    public static void main(String[] args) throws Exception {

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        User user = new User("paulinho@gmail.com", "passwordsecretadopaulinho");


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

            SHPSocket shpSocket = new SHPSocket(MessageType.TYPE1, _cifer.createPayloadType1(user.getUserId()));
            // Send the byte[] to the server
            outputStream.writeInt(shpSocket.getSocketContent().length);  // Send the length of the byte array first
            outputStream.write(shpSocket.getSocketContent());  // Send the byte array
            System.out.println("Sent this message to server: " + user.getUserId());



            //Reciving the message type2
            // -------------------------------------------------------------------------------------------------------
            int byteArrayLength = inputStream.readInt();
            byte[] receivedEncryptedData = new byte[byteArrayLength];
            inputStream.readFully(receivedEncryptedData);
            //decifer
            DeciferResult receivedMessage = _decifer.getPayloadType2(receivedEncryptedData);
            System.out.println("Received the message: " + receivedMessage.toString());
            // -------------------------------------------------------------------------------------------------------

            Thread.sleep(1000);


            //Sending the type 3 Message:
            // Send the byte[] to the server
            byte[] test = { 0x01, 0x02, 0x03};

            shpSocket = new SHPSocket(MessageType.TYPE3, _cifer.createPayloadType3(user, receivedMessage.getNonce3()));
            outputStream.writeInt(shpSocket.getSocketContent().length);  // Send the length of the byte array first
            outputStream.write(shpSocket.getSocketContent());  // Send the byte array
            System.out.println("Sent this message type 3 to server: " + shpSocket.getSocketContent());



            //Reciving the message type4
            // -------------------------------------------------------------------------------------------------------
            byteArrayLength = inputStream.readInt();
            receivedEncryptedData = new byte[byteArrayLength];
            inputStream.readFully(receivedEncryptedData);
            //decifer
            System.out.println("Received the message type 4: " + new String(receivedEncryptedData));
            // -------------------------------------------------------------------------------------------------------

            //Sending the type 5 Message:
            // Send the byte[] to the server
            byte[] test2 = { 0x05, 0x06};
            outputStream.writeInt(test.length);  // Send the length of the byte array first
            outputStream.write(test);  // Send the byte array
            System.out.println("Sent this message type 5 to server: " + new String(test));






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
