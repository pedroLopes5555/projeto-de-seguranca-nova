import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPReceiver {
    public static void main(String[] args) {
        DatagramSocket socket = null;
        try {
            // Create a UDP socket and bind it to a port
            socket = new DatagramSocket(9876);
            byte[] buffer = new byte[1024];  // Buffer to hold incoming data

            System.out.println("Receiver is running and waiting for messages...");

            while (true) {
                // Clear the buffer for every new message
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                // Receive the packet
                socket.receive(packet);

                // Convert the packet's data to a string
                String message = new String(packet.getData(), 0, packet.getLength());

                // Show the received message
                System.out.println("Message received: " + message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
}
