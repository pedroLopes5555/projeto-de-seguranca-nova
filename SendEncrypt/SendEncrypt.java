import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
public class SendEncrypt {






	private static byte[] createUDPDatagram(byte[] ciphertext, byte[] digest) {

		//Version -> 16 bits
        byte[] version = new byte[] { 0x00, 0x01 };
        //Release -> 8 bits
        byte[] release = new byte[] { 0x01 };
        //Payload length -> 16 bits
        byte[] payloadLen = new byte[] { 0x00, 0x01 };
    
        int headerLen = version.length + release.length + payloadLen.length;

		Utils.printInRed("tamanho do header:	" + headerLen);
		Utils.printInRed("tamanho do payload: 	" + (ciphertext.length + digest.length));

        byte[] combined = new byte[headerLen + ciphertext.length + digest.length];

        System.arraycopy(version, 0, combined, 0, version.length);
        System.arraycopy(release, 0, combined, version.length, release.length);
        System.arraycopy(payloadLen, 0, combined, version.length + release.length, payloadLen.length);
        System.arraycopy(ciphertext, 0, combined, headerLen, ciphertext.length);
        System.arraycopy(digest, 0, combined, headerLen + ciphertext.length, digest.length);


        return combined;
    }





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
	



	private static String prompt(String prompt) throws IOException {
		System.out.print(prompt);
		System.out.flush();
		BufferedReader input = 
			new BufferedReader(new InputStreamReader(System.in));
		String response = input.readLine();
		System.out.println();
		return response;
	} 





  public static void main(String args[]) throws Exception {

	// if (args.length != 2) {
	// System.out.println("Usar: SenEncrypt <hostname> <port>");
	// 	System.exit(-1);
	// }

	
	//get arguments
	String desthost= "localhost"; // Default;
	Integer destport = 5999; // Default;

	// Load data
	IConfigReader configReader = new ConfigReader();
	var config = configReader.getConfig();
	//var keys = configReader.getkeys();

	String ciphersuite = config.get(ConfigKey.CONFIDENTIALITY.getValue());  // Retrieve the ciphersuite
	
	System.out.println(ciphersuite);

	byte[] ivBytes= new byte[] {
		0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
		0x08, 0x09, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15 
	};

	IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

	System.out.println("\nDestino:" +desthost + " Porto:" +destport);
	System.out.println("Ciphersuite a usar: " 
	+ ciphersuite);

	String plaintext="INIT";

	SecretKey key = KeyRing.readSecretKey(config.get(ConfigKey.SYMMETRIC_KEY.getValue()), 
			config.get(ConfigKey.CONFIDENTIALITY.getValue()).substring(0,3));

	//--------------------------------------------------------
	//--------------------------------------------------------


	boolean debug = true;

	for(;;)
	{

		plaintext = prompt("Mensagem Plaintext: ");
		if (plaintext.equals("exit!")) break;
		byte[] ptextbytes= plaintext.getBytes();
		System.out.println("--------------------------------------------");

		
		Cipher cipher = Cipher.getInstance(ciphersuite);
		cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
		byte[] ciphertext = cipher.doFinal(plaintext.getBytes());


		
		String integrity = config.get(ConfigKey.INTEGRITY.getValue());
		System.out.println(integrity);



		//variables
		byte[] digest;
		byte[] datagram;
		
		if(integrity.equals("H")){
			//use H
			//create the hash algotrithm isntance
			MessageDigest hash = MessageDigest.getInstance(config.get(ConfigKey.H.getValue()));
			hash.update(ciphertext);
			digest = hash.digest();
			//digest is the cihertext hashed

			datagram = createUDPDatagram(ciphertext, digest);
			//tampering atack example
			//			payload[4] ^= '1' ^ '9';
			//----------------
			Socket s = new Socket(desthost, destport);
			sendUDPDatagram(datagram, s);

			Utils.printInRed("tamanho do datagrama: " + datagram.length);


		}else if(integrity.equals("HMAC")){
			//HMAC is a Hash-based Message Authentication Code (Hashed MAC).
			// HMAC is calculated signature as HMAC(“Message”, secret, digest-algorithm)


			// Key macKey = KeyRing.readSecretKey(config.get(ConfigKey.MACKEY.getValue()) , config.get(ConfigKey.MAC.getValue()));

			// var hashFunction = config.get(ConfigKey.MAC.getValue());
			// Mac hMac = Mac.getInstance(hashFunction);
			

			// cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
			// byte[] cipherText = new byte[cipher.getOutputSize(plaintext.length() + hMac.getMacLength())];
			// int ctLength = cipher.update(Utils.toByteArray(plaintext), 0, plaintext.length(), cipherText, 0);

			// hMac.init(hMacKey);
			// hMac.update(Utils.toByteArray(plaintext));
			// digest = hMac.doFinal();


			// datagram = createUDPDatagram(cipherText, digest);
			// Socket s = new Socket(desthost, destport);
			// sendUDPDatagram(datagram, s);

			// Utils.printInRed("chegou aqui");

		}else{
			Utils.printInRed("Not Valid Integrity Field ->  INTEGRITY:" + integrity);
			System.exit(0);
			}
		}
		System.exit(0);
	}





}

