import DSTP.dstpdecript.DecriptDatagram;
import DSTP.dstpdecript.EncriptedDatagramResoult;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;

public class MulticastReceiverAlternative {
    private static final String MULTICAST_ADDRESS = "224.0.0.3"; // same address as sender
    private static final int PORT = 4446; // same port as sender

    public static void main(String[] args) {


        if (args.length != 2) {
            System.err.println("usage: java MulticastReceiver multicast_group port");
            System.exit(0);
        }

        int port = Integer.parseInt(args[1]);

        
        if (!group.isMulticastAddress()) {
            System.err.println("Multicast address required...");
            System.exit(0);
        }

        
        try (MulticastSocket socket = new MulticastSocket(PORT)) {
            
            InetAddress group = InetAddress.getByName(args[0]);
            
            
            // Use a NetworkInterface for joining the group
            NetworkInterface networkInterface = NetworkInterface.getByName("eth0"); 
            if (networkInterface == null) {
                System.out.println("Network interface not found. Please specify a valid network interface.");
                return;
            }
            
            // Join the group using SocketAddress and NetworkInterface
            socket.joinGroup(new InetSocketAddress(group, PORT), networkInterface);
            
            System.out.println("Waiting for encrypted messages...");

            byte[] buffer = new byte[256];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            
            // Receive a packet
            socket.receive(packet);
            
            // Get the encrypted data from the packet
            byte[] encryptedData = new byte[packet.getLength()];
            System.arraycopy(packet.getData(), 0, encryptedData, 0, packet.getLength());

            // Decrypt the received data
            EncriptedDatagramResoult result = DecriptDatagram.GetDecriptedDatagram(encryptedData);
            
            // Display the decrypted message and sequence number
            System.out.println("Decrypted message received: " + new String(result.getPtextBytes()));
            System.out.println("Sequence number: " + result.getSequenceNumber());
            
            // Leave the group after receiving the message
            socket.leaveGroup(new InetSocketAddress(group, PORT), networkInterface);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // Handle exceptions related to decryption
            System.err.println("Decryption error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
