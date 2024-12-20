import dstpsend.GetEncryptedDatagram;
import java.net.*;
import java.util.*;

public class MulticastSender {

    public static void main(String[] args ) throws Exception {
	    if( args.length != 4 ) {
		    System.err.println("usage: java MulticastSender  grupo_multicast porto time-interval criptoconfig_path") ;
		    System.exit(0) ;
	    }
    
        int more=20; // change if needed, send 20 time a MCAST message
        int port = Integer.parseInt( args[1]) ;
        InetAddress group = InetAddress.getByName( args[0] ) ;
        int timeinterval = Integer.parseInt( args[2]) ;
        String msg;

        if( !group.isMulticastAddress() ) {
            System.err.println("Multicast address required...") ;
            System.exit(0) ;
        }

        MulticastSocket ms = new MulticastSocket() ;
        do {
            String msgsecret="topcsecret message, sent on: ";
            String msgdate = new Date().toString();
            msg=msgsecret+msgdate;

            byte[] dstpDatagram = GetEncryptedDatagram.getEncryptedDatagram(msg.getBytes(), 1, args[3]);


        ms.send( new DatagramPacket( dstpDatagram, dstpDatagram.length, group, port ) ) ;

	    --more;    // Tirar o comentario se quizer mandar apenas "more" numero de vezes

        try {
            Thread.sleep(1000*timeinterval);
        } 
        catch (InterruptedException e) { }

        } while( more >0 ) ;
        msg="fim!";
        ms.send( new DatagramPacket( msg.getBytes(), msg.getBytes().length, group, port ) ) ;
        ms.close();
	    
    }
}

