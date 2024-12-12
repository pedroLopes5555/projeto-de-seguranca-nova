import BusinessLogic.DeciferResult.DeciferResult;
import BusinessLogic.ISHPCifer;
import BusinessLogic.ISHPDecifer;
import BusinessLogic.SHPCifer;
import BusinessLogic.SHPDecifer;
import BusinessLogic.UdpConnection.UDPConnecrtion;
import DSTP.DecriptDatagram;
import DSTP.EncriptedDatagramResoult;
import DSTP.GetEncryptedDatagram;
import Objects.MessageType;
import Objects.SHPSocket.SHPSocket;
import Objects.SHPSocket.SHPSocketUtils;

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class StreamServer {
    static ISHPCifer _cifer;
    static ISHPDecifer _decifer;


    public static void main(String[] args) throws Exception {


        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        UDPConnecrtion udpConnection = new UDPConnecrtion();

        _cifer = new SHPCifer();
        _decifer = new SHPDecifer();

        SHPSocketUtils utils = new SHPSocketUtils();

        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        DataInputStream inputStream = null;
        DataOutputStream outputStream = null;

        String clientIp = "";
        try {
            // Create a ServerSocket on port 5000
            serverSocket = new ServerSocket(5000);
            System.out.println("Server started, waiting for a client to connect... -> port: " + 5000);
            // Accept incoming client connection
            clientSocket = serverSocket.accept();
            System.out.println("Client connected.");
            System.out.println(" ----------------- ");

            clientIp = clientSocket.getInetAddress().getHostAddress();
            System.out.println("Client connected from IP: " + clientIp);



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


            udpConnection.setRequestedMovie(type3Result.getRequest());
            udpConnection.setPort(type3Result.getUdpPort());




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





        int size;
        int count = 0;
        long time;


        System.out.println(udpConnection.getRequestedMovie());

        if(!Objects.equals(udpConnection.getRequestedMovie(), "cars")){
            throw new Exception("movie dosent exist on how database");
        }

        DataInputStream g = new DataInputStream(new FileInputStream("C:/Users/valen/Desktop/projeto-de-seguranca-nova/pt_2/cars.dat"));
        //DataInputStream g = new DataInputStream(new FileInputStream("/home/pedro/NOVA/seguranca/projeto-de-seguranca-nova/pt_2/cars.dat"));
        byte[] buff = new byte[65000];

        DatagramSocket socket = new DatagramSocket();
        InetSocketAddress addr = new InetSocketAddress(clientIp, udpConnection.getPort());

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
            byte[] encryptedData = GetEncryptedDatagram.getEncryptedDatagram(buff, count, "src/cfg/Server/ciphersuite.conf" );
            p.setData(encryptedData, 0, encryptedData.length);
            p.setSocketAddress(addr);

            // Calculate delay to match streaming time
            long t = System.nanoTime();
            Thread.sleep(Math.max(0, ((time - q0) - (t - t0)) / 1000000));

            // Send the encrypted packet
            socket.send(p);
        }

        System.out.println("\nEND ! packets with frames sent: " + count);



    }


    private static InetSocketAddress parseSocketAddress(String socketAddress) {
        String[] split = socketAddress.split(":");
        String host = split[0];
        int port = Integer.parseInt(split[1]);
        return new InetSocketAddress(host, port);
    }
}
