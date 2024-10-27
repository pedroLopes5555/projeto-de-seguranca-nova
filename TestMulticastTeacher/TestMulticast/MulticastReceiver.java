import dstpdecript.DecriptDatagram;
import dstpdecript.EncriptedDatagramResoult;
import java.net.*;

public class MulticastReceiver {

    public static void main(String[] args ) throws Exception {
	    if( args.length != 3 ) {
		System.err.println("usage: java MulticastReceiver grupo_multicast porto cryptoconfig_path") ;
		System.exit(0) ;
	    }
 
    int port = Integer.parseInt( args[1]) ;
    InetAddress group = InetAddress.getByName( args[0] ) ;

    if( !group.isMulticastAddress() ) {
	System.err.println("Multicast address required...") ;
	System.exit(0) ;
    }

    MulticastSocket rs = new MulticastSocket(port) ;

    rs.joinGroup(group);

    DatagramPacket p = new DatagramPacket( new byte[65536], 65536 ) ;
    String recvmsg;

    do {

        p.setLength(65536); // resize with max size
	    rs.receive(p) ;

        EncriptedDatagramResoult result = DecriptDatagram.GetDecriptedDatagram(p.getData(), args[2]);

        recvmsg =  new String(result.getPtextBytes());
        
	System.out.println("Msg recebida: "+ recvmsg  ) ;
    } while(!recvmsg.equals("fim!")) ;

    // rs.leave if you want leave from the multicast group ...
    rs.close();
	    
    }
}
