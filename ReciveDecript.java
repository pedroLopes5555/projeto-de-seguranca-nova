import DSTP.dstpdecript.DecriptDatagram;
import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class ReciveDecript {
    public static void main(String[] args) throws Exception  {
        
        Integer port = 5999; // The port to listen on

        byte[] UDPDatagram = null;


            for(;;)
            {
            ServerSocket ss = new ServerSocket(port);


            try {
                Socket s = ss.accept();
                try {
                    DataInputStream is = new DataInputStream(s.getInputStream());
                    UDPDatagram = new byte[is.readInt()];
                    is.read(UDPDatagram);
                    System.out.println("----------------------------------------------");
                        
                } 
                finally {
                    try {
                        s.close();
                    } catch (Exception e) {

                    } 
                } 
            } 


            finally {
                try {
                    ss.close();
                    } catch (Exception e) {
                } 
            }


            System.out.println("estou aqui");
            if(UDPDatagram != null){
                byte[] decripted = DecriptDatagram.GetDecriptedDatagram(UDPDatagram);
                System.out.println("recebeste: " + new String(decripted));
            }



        }
    }
}