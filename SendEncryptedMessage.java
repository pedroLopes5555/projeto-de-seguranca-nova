import DSTP.dstpsend.GetEncryptedDatagram;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class SendEncryptedMessage {

    
	private static void sendUDPDatagram(byte[] UDPPayload, Socket s) {

        DataOutputStream os = null;
        try {	
            os = new DataOutputStream(s.getOutputStream());
            os.writeInt(UDPPayload.length);
            os.write(UDPPayload);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Clean up and close the socket and output stream (sim paulinho, esta parte é o nosso amigo ppt)
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (s != null) {
                    s.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) throws Exception {
        String desthost= "localhost"; // Default;
        Integer destport = 5999; // Default;
 
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter your secret message: ");
        String message = scanner.nextLine();
        scanner.close();


        byte[] data = message.getBytes();

        byte[] encryptedDatagramData = GetEncryptedDatagram.getEncryptedDatagram(data);


        try {
            Socket s = new Socket(desthost, destport);
            sendUDPDatagram(encryptedDatagramData, s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
