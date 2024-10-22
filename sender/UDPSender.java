


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

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
            // Defaults para a Cifra... Se quizer passe em parametro a seguir


            //inicialize cypher
            String ciphersuite="AES/CTR/NoPadding";

            byte[] ivBytes= new byte[] {
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
                0x08, 0x09, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15 
                    };

            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            System.out.println("Ciphersuite a usar: " 
            + ciphersuite);

            SecretKey key = KeyRing.readSecretKey();

            //encript data:

            //-------------------

            //message to a byte array
            //byte[] buffer = message.getBytes();

            Cipher cipher = Cipher.getInstance(ciphersuite);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
            byte[] ciphertext = cipher.doFinal(message.getBytes());

            //create datagram and send
            DatagramPacket packet = new DatagramPacket(ciphertext, ciphertext.length, receiverAddress, port);
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
