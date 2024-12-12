import BusinessLogic.DeciferResult.DeciferResult;
import BusinessLogic.ISHPCifer;
import BusinessLogic.ISHPDecifer;
import BusinessLogic.SHPCifer;
import BusinessLogic.SHPDecifer;
import DSTP.*;
import Objects.MessageType;
import Objects.SHPSocket.SHPSocket;
import Objects.SHPSocket.SHPSocketUtils;
import Objects.User;
import org.bouncycastle.jce.provider.BouncyCastleProvider;


import java.io.*;
import java.net.*;
import java.security.Security;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Proxy {

    static ISHPCifer _cifer;
    static ISHPDecifer _decifer;


    public static void main(String[] args) throws Exception {

        // Validate the number of arguments
        if (args.length != 7) {
            System.out.println("Usage: java Proxy <username> <password> <server> <tcp_port> <movie> <udp_port> <player_port>");
            return;
        }


        // Parse command-line arguments
        String username = args[0];
        String password = args[1];
        String server = args[2];
        int tcpPort = Integer.parseInt(args[3]);
        String movie = args[4];
        int udpPort = Integer.parseInt(args[5]);
        int playerPort = Integer.parseInt(args[6]);




        Map<String, String> map = new HashMap<>();

        Security.addProvider(new BouncyCastleProvider());
        User user = new User(username, password);
        //passwordsecretadopaulinho

        _cifer = new SHPCifer();
        _decifer = new SHPDecifer();

        SHPSocketUtils utils = new SHPSocketUtils();

        Socket socket = null;
        DataInputStream inputStream = null;
        DataOutputStream outputStream = null;

        try {
            // Connect to the server on localhost and port 5000
            socket = new Socket(server, tcpPort);
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

            //olha, vou receber nesta porta
            shpSocket = new SHPSocket(MessageType.TYPE5, _cifer.createPayloadType3(user, receivedMessage.getNonce3(), udpPort, movie));
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


        String remote = server + ":" + udpPort; // receive mediastream from this remote endpoint
        String destinations = "localhost" + ":" + playerPort; // resend mediastream to this destination endpoint

        System.out.println("a receber de: " + remote);
        System.out.println("a enviar para :" + destinations);

        SocketAddress inSocketAddress = parseSocketAddress(remote);
        Set<SocketAddress> outSocketAddressSet = Arrays.stream(destinations.split(","))
                .map(s -> parseSocketAddress(s))
                .collect(Collectors.toSet());

        DatagramSocket inSocket = new DatagramSocket(inSocketAddress);
        DatagramSocket outSocket = new DatagramSocket();
        byte[] buffer = new byte[4 * 1024]; // Adjust buffer size as necessary

        while (true) {
            DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
            inSocket.receive(inPacket); // Receive incoming packet

            // Decrypt the received data
            byte[] receivedData = Arrays.copyOfRange(inPacket.getData(), 0, inPacket.getLength());
            EncriptedDatagramResoult decryptedResult = DecriptDatagram.GetDecriptedDatagramWhitMap(receivedData, map);

            // Extract the decrypted payload
            byte[] decryptedData = decryptedResult.getPtextBytes();

            // Relay the decrypted packet to each destination
            for (SocketAddress outSocketAddress : outSocketAddressSet) {
                DatagramPacket outPacket = new DatagramPacket(decryptedData, decryptedData.length, outSocketAddress);
                outSocket.send(outPacket);
            }

        }

    }

    private static InetSocketAddress parseSocketAddress(String socketAddress) {
        String[] split = socketAddress.split(":");
        String host = split[0];
        int port = Integer.parseInt(split[1]);
        return new InetSocketAddress(host, port);
    }
}
