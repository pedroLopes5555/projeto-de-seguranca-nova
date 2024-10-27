/*
 * hjUDPproxy, for use in 2024
 */
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import DSTP.dstpdecript.DecriptDatagram;
import DSTP.dstpdecript.EncriptedDatagramResoult;

class hjUDPproxy {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Use: hjUDPproxy <endpoint1> <endpoint2>");
            System.out.println("<endpoint1>: endpoint for receiving stream");
            System.out.println("<endpoint2>: endpoint of media player");

            System.out.println("Ex: hjUDPproxy 224.2.2.2:9000  127.0.0.1:8888");
            System.out.println("Ex: hjUDPproxy 127.0.0.1:10000 127.0.0.1:8888");
            System.exit(0);
        }

        String remote = args[0]; // receive mediastream from this remote endpoint
        String destinations = args[1]; // resend mediastream to this destination endpoint

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
            EncriptedDatagramResoult decryptedResult = DecriptDatagram.GetDecriptedDatagram(receivedData);

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
