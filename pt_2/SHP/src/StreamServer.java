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
            System.out.println("");
            // Create a ServerSocket on port 5000
            serverSocket = new ServerSocket(5000);
            System.out.println("Server started, waiting for a client to connect... -> port: " + 5000);
            System.out.printf("");

            clientSocket = serverSocket.accept();
            clientIp = clientSocket.getInetAddress().getHostAddress();

            System.out.println("Client connected from IP: " + clientIp);
            System.out.println("--------------------------------------------------------------");
            System.out.println("");



            // Get the input and output streams from the client socket
            inputStream = new DataInputStream(clientSocket.getInputStream());
            outputStream = new DataOutputStream(clientSocket.getOutputStream());

            Thread.sleep(1000);


            System.out.println("--------------------------------------------------------------");
            System.out.println("Receiving a Message Type 1 from the Client.");

            int byteArrayLength = inputStream.readInt();
            byte[] receivedEncryptedData = new byte[byteArrayLength];
            inputStream.readFully(receivedEncryptedData);
            DeciferResult receivedMessage = _decifer.getPayloadType1(receivedEncryptedData);
            String userId = receivedMessage.getValue();

            System.out.println("Received the message: " + receivedMessage.toString());
            System.out.println("--------------------------------------------------------------");
            System.out.println("");


            Thread.sleep(1000);


            System.out.println("--------------------------------------------------------------");
            System.out.println("Sending a Message Type 2 into the Client.");

            SHPSocket shpSocket = new SHPSocket(MessageType.TYPE2, _cifer.createPayloadType2());
            outputStream.writeInt(shpSocket.getSocketContent().length);
            outputStream.write(shpSocket.getSocketContent());

            System.out.println("Sent the message: " + shpSocket.getSocketContent());
            System.out.println("--------------------------------------------------------------");
            System.out.println("");


            Thread.sleep(1000);


            System.out.println("--------------------------------------------------------------");
            System.out.println("Receiving a Message Type 3 from the Client.");

            byteArrayLength = inputStream.readInt();
            receivedEncryptedData = new byte[byteArrayLength];
            inputStream.readFully(receivedEncryptedData);
            var type3Result = _decifer.getPayloadType3(receivedEncryptedData, userId);
            udpConnection.setRequestedMovie(type3Result.getRequest());
            udpConnection.setPort(type3Result.getUdpPort());

            System.out.println("Received the message: " + type3Result.toString());
            System.out.println("--------------------------------------------------------------");
            System.out.println("");


            Thread.sleep(1000);


            System.out.println("--------------------------------------------------------------");
            System.out.println("Sending a Message Type 4 into the Client.");

            shpSocket = new SHPSocket(MessageType.TYPE4, _cifer.createPayloadType4(userId, "streaming",type3Result.getNonce4()));
            outputStream.writeInt(shpSocket.getSocketContent().length);
            outputStream.write(shpSocket.getSocketContent());

            System.out.println("Sent the message: " + shpSocket.getSocketContent());
            System.out.println("--------------------------------------------------------------");
            System.out.println("");


            Thread.sleep(1000);


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
