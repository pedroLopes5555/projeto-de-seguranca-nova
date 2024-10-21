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


            //get message from user
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter the message to send: ");
            String message = scanner.nextLine();
            //---------------------------------------



            //Encrypt the message



            //-------------------

            //message to a byte array
            byte[] buffer = message.getBytes();

            //create datagram and send
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receiverAddress, port);
            socket.send(packet);
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
