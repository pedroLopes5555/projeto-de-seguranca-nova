/*
* hjStreamServer.java 
* Streaming server: emitter of video streams (movies)
* Can send in unicast or multicast IP for client listeners
* that can play in real time the transmitted movies
*/

import java.io.*;
import java.net.*;
import DSTP.dstpsend.GetEncryptedDatagram;

class hjStreamServer {

	static public void main(String[] args) throws Exception {
		if (args.length != 3) {
			System.out.println("Use: hjStramSrver <movie> <ip-multicast-address> <port>");
			System.out.println("Ex: hjStreamSrver  <movie> 224.2.2.2 9000");
			System.out.println(" or: hjStreamSrver  <movie> <ip-unicast-address> <port>");
			System.out.println("Ex: hjStreamSrver  <movie> 127.0.0.1 10000");

			System.exit(-1);
		}

		int size;
		int count = 0;
		long time;

		DataInputStream g = new DataInputStream(new FileInputStream(args[0]));
		byte[] buff = new byte[65000];

		DatagramSocket socket = new DatagramSocket();
		InetSocketAddress addr = new InetSocketAddress(args[1], Integer.parseInt(args[2]));

		DatagramPacket p = new DatagramPacket(buff, buff.length, addr);
		long t0 = System.nanoTime(); // reference time
		long q0 = 0;

		while (g.available() > 0) {
			// Read packet data from the input stream
			size = g.readShort();
			time = g.readLong();

			if (count == 0) q0 = time; // reference time in the stream
			count += 1;

			g.readFully(buff, 0, size);

			// Encrypt the datagram data before sending
			byte[] encryptedData = GetEncryptedDatagram.getEncryptedDatagram(buff, count);
			p.setData(encryptedData, 0, encryptedData.length);
			p.setSocketAddress(addr);

			// Calculate delay to match streaming time
			long t = System.nanoTime();
			Thread.sleep(Math.max(0, ((time - q0) - (t - t0)) / 1000000));

			// Send the encrypted packet
			socket.send(p);
		}

		System.out.println("\nEND ! packets with frames sent: " + count);
	}
}
