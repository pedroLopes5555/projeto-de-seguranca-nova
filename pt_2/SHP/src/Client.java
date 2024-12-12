import BusinessLogic.DeciferResult.DeciferResult;
import BusinessLogic.ISHPCifer;
import BusinessLogic.ISHPDecifer;
import BusinessLogic.SHPCifer;
import BusinessLogic.SHPDecifer;
import BusinessLogic.UdpConnection.UDPConnecrtion;
import DSTP.*;
import Objects.MessageType;
import Objects.SHPSocket.SHPSocket;
import Objects.SHPSocket.SHPSocketUtils;
import Objects.User;
import org.bouncycastle.jce.provider.BouncyCastleProvider;


import java.io.*;
import java.net.*;
import java.security.Provider;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Client {

    static ISHPCifer _cifer;
    static ISHPDecifer _decifer;


    public static void main(String[] args) throws Exception {

        Map<String, String> map = new HashMap<>();

        Security.addProvider(new BouncyCastleProvider());
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

            Thread.sleep(1000);


            // Sending message type 1 into the server.
            // Creating the socket with the userId

            SHPSocket shpSocket = new SHPSocket(MessageType.TYPE1, _cifer.createPayloadType1(user.getUserId()));
            // Send the byte[] to the server
            outputStream.writeInt(shpSocket.getSocketContent().length);  // Send the length of the byte array first
            outputStream.write(shpSocket.getSocketContent());  // Send the byte array
            System.out.println("Sent this message to server: " + user.getUserId());


            Thread.sleep(1000);



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

            shpSocket = new SHPSocket(MessageType.TYPE5, _cifer.createPayloadType3(user, receivedMessage.getNonce3()));
            outputStream.writeInt(shpSocket.getSocketContent().length);  // Send the length of the byte array first
            outputStream.write(shpSocket.getSocketContent());  // Send the byte array
            System.out.println("Sent this message type 3 to server: " + shpSocket.getSocketContent());




            //Reciving the message type4
            // -------------------------------------------------------------------------------------------------------
            int byteArrayLength1 = inputStream.readInt();
            receivedEncryptedData = new byte[byteArrayLength1];
            inputStream.readFully(receivedEncryptedData);
            receivedMessage = _decifer.getPayloadType4(receivedEncryptedData);
            map = receivedMessage.getCryptoConfigMap();


            Thread.sleep(1000);




            //Sending the type 5 Message:
            shpSocket = new SHPSocket(MessageType.TYPE5, _cifer.createPayloadType5(receivedMessage.getNonce5() , map));
            outputStream.writeInt(shpSocket.getSocketContent().length);  // Send the length of the byte array first
            outputStream.write(shpSocket.getSocketContent());  // Send the byte array
            System.out.println("Sent this message type 5 to server: " + shpSocket.getSocketContent());



            Thread.sleep(1000);


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


        int size;
        int count = 0;
        long time;

        DataInputStream g = new DataInputStream(new FileInputStream("/home/pedro/NOVA/seguranca/projeto-de-seguranca-nova/pt_2/cars.dat"));
        byte[] buff = new byte[65000];

        DatagramSocket s1 = new DatagramSocket();
        InetSocketAddress addr = new InetSocketAddress("127.0.0.1", 9000);

        DatagramPacket p = new DatagramPacket(buff, buff.length, addr);
        long t0 = System.nanoTime(); // reference time
        long q0 = 0;

        while (g.available() > 0) {
            // Read packet data from the input stream
            size = g.readShort();
            time = g.readLong();

            if (count == 0) q0 = time; // reference time in the stream
            count += 1;

            g.readFully(buff, 0, size);

            // Encrypt the datagram data before sending
            if(map == null){
                throw  new Exception("no cfg recived");
            }

            byte[] encryptedData = GetEncryptedDatagram.getEncryptedDatagram(buff, count, map);
            p.setData(encryptedData, 0, encryptedData.length);
            p.setSocketAddress(addr);

            // Calculate delay to match streaming time
            long t = System.nanoTime();
            Thread.sleep(Math.max(0, ((time - q0) - (t - t0)) / 1000000));

            // Send the encrypted packet
            s1.send(p);
        }

        System.out.println("\nEND ! packets with frames sent: " + count);




    }
}
