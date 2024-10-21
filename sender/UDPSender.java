import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class UDPSender {
    public static void main(String[] args) {
        // Ensure the sender receives 2 arguments: IP and port
        if (args.length != 2) {
            System.out.println("Usage: java UDPSender <IP Address> <Port>");
            return;
        }

        DatagramSocket socket = null;
        try {
            // Parse the IP address and port from command-line arguments
            InetAddress receiverAddress = InetAddress.getByName(args[0]);
            int port = Integer.parseInt(args[1]);

            // Create a UDP socket
            socket = new DatagramSocket();

            // Create a scanner to read input from the user
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter the message to send: ");
            String message = scanner.nextLine();

            // Convert the message to a byte array
            byte[] buffer = message.getBytes();

            // Create a packet to send to the receiver
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receiverAddress, port);

            // Send the packet
            socket.send(packet);

            // Show the message that was sent
            System.out.println("Message sent: " + message);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
}
