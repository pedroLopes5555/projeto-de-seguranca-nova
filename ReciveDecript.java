import DSTP.dstpdecript.DecriptDatagram;
import DSTP.dstpdecript.EncriptedDatagramResoult;
import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;


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
                System.out.println("\n");
                EncriptedDatagramResoult result =  DecriptDatagram.GetDecriptedDatagram(UDPDatagram);
                System.out.println("recebeste: " + new String(result.getPtextBytes()));
                System.out.println("numero de sequencia : " + Arrays.toString(result.getSequenceNumber()));

            }



        }
    }
}