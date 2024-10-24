import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
public class SendEncrypt {


  public static void main(String args[]) throws Exception {

	// ANSI escape code for red text
	String red = "\u001B[31m";
	// ANSI escape code to reset to default
	String reset = "\u001B[0m";

	if (args.length != 2) {
	System.out.println("Usar: SenEncrypt <hostname> <port>");
		System.exit(-1);
	}

	
	//get arguments
	String desthost= args[0]; // Default;
	Integer destport=Integer.parseInt(args[1]); // Default;

	// Load data
	IConfigReader configReader = new ConfigReader();
	var config = configReader.getConfig();
	//var keys = configReader.getkeys();

	String ciphersuite = config.get("CONFIDENTIALITY");  // Retrieve the ciphersuite
	
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

	SecretKey key = KeyRing.readSecretKey(config.get("SYMMETRIC_KEY") , config.get("CONFIDENTIALITY").substring(0,3));

	//--------------------------------------------------------
	//--------------------------------------------------------


	boolean debug = true;

	if(debug){
		for(;;)
		{
	
			plaintext = prompt("Mensagem Plaintext: ");
			if (plaintext.equals("exit!")) break;
			byte[] ptextbytes= plaintext.getBytes();
	
			System.out.println("--------------------------------------------");
			System.out.println("Plaintext em HEX: " + Utils.toHex(ptextbytes, ptextbytes.length));
	

			
			Cipher cipher = Cipher.getInstance(ciphersuite);
			cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
			byte[] ciphertext = cipher.doFinal(plaintext.getBytes());
	
			System.out.println("Mensagem cifrada a enviar (em HEX)...:");
			System.out.println(Utils.toHex(ciphertext, ciphertext.length) + " Size: " +ciphertext.length);
			System.out.println("----------------------------------------------");
	
	
	
			//HASHING
			System.out.println(red + config.get("INTEGRITY").equals("HMAC") + reset);
			System.out.println(red + config.get("INTEGRITY") + reset);

			String integrity = config.get("INTEGRITY");

			System.out.println(integrity);
			
			if(integrity.equals("HMAC")){
				//use HMAC
			}else if(integrity.equals("H")){
				//Use hash
			}else{
				System.out.println(red + "Not Valid Integrity Field ->  INTEGRITY:" + integrity + reset);
				System.exit(0);
			}
			
			
			MessageDigest hash = MessageDigest.getInstance(config.get("H"));
			hash.update(ciphertext);
			byte[] digest = hash.digest();
			System.out.println("hash size = " + digest.length);
			//-----------------------------------------------------------
	
	
			// Enviar cyphertext por um socket
			Socket s = new Socket(desthost, destport);
	
			try {
				DataOutputStream os = new DataOutputStream(s.getOutputStream());
	
	
				//combine in the same payload the cypher test and the hash
				byte[] combined = new byte[ciphertext.length + digest.length];
				System.arraycopy(ciphertext, 0, combined, 0, ciphertext.length);
				System.arraycopy(digest, 0, combined, ciphertext.length, digest.length);

				os.writeInt(combined.length);
				os.write(combined);
				os.close();
			} 
			finally {
					try {
						s.close();
					} catch (Exception e) {
	
					}
				}
			}
			System.exit(0);
		}
	}
	



	public static String prompt(String prompt) throws IOException {
	System.out.print(prompt);
	System.out.flush();
	BufferedReader input = 
		new BufferedReader(new InputStreamReader(System.in));
	String response = input.readLine();
	System.out.println();
	return response;
	} 
}

