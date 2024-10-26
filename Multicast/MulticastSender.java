import DSTP.dstpsend.GetEncryptedDatagram;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MulticastSender {
    private static final String MULTICAST_ADDRESS = "224.0.0.3"; // multicast address (within 224.0.0.0 - 239.255.255.255 range)
    private static final int PORT = 4446; // port for communication
    
    public static void main(String[] args) throws Exception {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            String message = "Hello, Multicast!";

            byte[] data = message.getBytes();

            byte[] encryptedDatagramData = GetEncryptedDatagram.getEncryptedDatagram(data, 0);
            
            DatagramPacket packet = new DatagramPacket(
                encryptedDatagramData, encryptedDatagramData.length, group, PORT);
            
            // Send the packet
            socket.send(packet);
            System.out.println("Message sent: " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
