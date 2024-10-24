import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
public class SendEncrypt {


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

	// --------------------------------------------- Check data
	ICheckCFG checkCFG = new CheckCFG();
	boolean validConfig = checkCFG.validateConfig(config);

	if(!validConfig){
		System.out.println("CFG file invalid");
		System.exit(0);
	}
	// --------------------------------------------- Check data

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

	for(;;)
	{

		plaintext = prompt("Mensagem Plaintext: ");
		if (plaintext.equals("exit!")) break;
		byte[] ptextbytes= plaintext.getBytes();
		System.out.println("--------------------------------------------");

		
		Cipher cipher = Cipher.getInstance(ciphersuite);
		cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
		byte[] ciphertext = cipher.doFinal(plaintext.getBytes());


		
		String integrity = config.get("INTEGRITY");
		System.out.println(integrity);



		//variables
		MessageDigest hash;
		byte[] digest;
		
		if(integrity.equals("H")){
			//use H

			hash = MessageDigest.getInstance(config.get("H"));

			hash.update(ciphertext);
			digest = hash.digest();

			int digestLen = digest.length;
			Utils.printInRed("tamanho da ashe enviada:" + digest.length);
			Utils.printInRed("tamanho da ciphertext enviada:" + ciphertext.length);
			



			var payload = createUDPPayload(ciphertext, digest);

			Socket s = new Socket(desthost, destport);
			sendUDPPayload(payload, s);
	
		}else if(integrity.equals("HMAC")){
			//Use hash
		}else{
			Utils.printInRed("Not Valid Integrity Field ->  INTEGRITY:" + integrity);
			System.exit(0);
			}
		}
		System.exit(0);
	}





	public static byte[] createUDPPayload(byte[] ciphertext, byte[] digest) {
        //combine in the same payload the cypher test and the hash
				byte[] combined = new byte[ciphertext.length + digest.length];
				System.arraycopy(ciphertext, 0, combined, 0, ciphertext.length);
				System.arraycopy(digest, 0, combined, ciphertext.length, digest.length);


        return combined;
    }





	private static void sendUDPPayload(byte[] UDPPayload, Socket s) {


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
}

