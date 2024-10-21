import java.net.DatagramPacket;
import java.net.DatagramSocket;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class UDPReceiver {
    public static void main(String[] args) {
        DatagramSocket socket = null;
        try {
            // Create a UDP socket and bind it to a port
            socket = new DatagramSocket(9876);
            byte[] buffer = new byte[1024];  // Buffer to hold incoming data



            //decript
            String ciphersuite="AES/CTR/NoPadding";

            //the same as the sender
            byte[] ivBytes= new byte[] {
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
                0x08, 0x09, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15 
            };

            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            //read key
            SecretKey key = KeyRing.readSecretKey(); // Ok, I know / I share the key with sender

            //------



            System.out.println("Receiver is running and waiting for messages...");

            while (true) {
                // Clear the buffer for every new message
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                // Receive the packet
                socket.receive(packet);

                byte[] ciphertext = packet.getData();

                System.out.println("Decrypt received ciphertext ...");

                Cipher cipher = Cipher.getInstance(ciphersuite);
                cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
            
                byte[] plainText= new byte[cipher.getOutputSize(ciphertext.length)];
                int ptLength=cipher.update(ciphertext,0, ciphertext.length, plainText,0);
                
                ptLength += cipher.doFinal(plainText, ptLength);
                System.out.println("Plaintext in HEX: "+ Utils.toHex(plainText, ptLength) +
                        " Size: " +ptLength );

                String msgoriginal= new String(plainText);

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
