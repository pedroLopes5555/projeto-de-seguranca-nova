package DSTP.dstpsend;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;




public class GetEncryptedDatagram {

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





	public static byte[] getEncryptedDatagram(byte[] ptextbytes) throws Exception {

		// Load data
		IConfigReader configReader = new ConfigReader();
		var config = configReader.getConfig();
		//var keys = configReader.getkeys();

		String ciphersuite = config.get(ConfigKey.CONFIDENTIALITY.getValue());  // Retrieve the ciphersuite
		
		// --------------------- Check if it is GCM mode
		int index = ciphersuite.indexOf("/");
		String ciphersuiteMode = "";
			
		if (index != -1) {
			ciphersuiteMode = ciphersuite.split("/")[1];
		}
		// --------------------- Check if it is GCM mode


		// --------------------- Get ivBytes from cfg
		String ivHex = config.get(ConfigKey.IV.getValue());
		if(ivHex.length()%2 != 0){
			ivHex += ivHex.charAt(ivHex.length()-1);
		}

		byte[] ivBytes= new byte[ivHex.length()/2];

		for (int i = 0; i < ivHex.length(); i += 2) {
			ivBytes[i / 2] = (byte) ((Character.digit(ivHex.charAt(i), 16) << 4)
								+ Character.digit(ivHex.charAt(i+1), 16));
		}
		// --------------------- Get ivBytes from cfg

		IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
		GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, ivBytes);

		System.out.println("Ciphersuite a usar: " 
		+ ciphersuite);

		SecretKey key = KeyRing.readSecretKey(config.get(ConfigKey.SYMMETRIC_KEY.getValue()), 
				config.get(ConfigKey.CONFIDENTIALITY.getValue()).substring(0,3));

		//--------------------------------------------------------
		//--------------------------------------------------------




		
		Cipher cipher = Cipher.getInstance(ciphersuite);
		if(ciphersuiteMode.toUpperCase().equals("GCM")){
			cipher.init(Cipher.ENCRYPT_MODE, key, gcmParameterSpec);
		}else{
			cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
		}


		
		byte[] ciphertext = cipher.doFinal(ptextbytes);

		String integrity = config.get(ConfigKey.INTEGRITY.getValue());
		System.out.println(integrity);


		//variables
		byte[] digest;
		byte[] datagram;

			switch (integrity) {
					case "H" ->		{
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
									//Socket s = new Socket(desthost, destport);
									return datagram;
							}
					case "HMAC" ->	{
									// Use HMAC-based integrity
									Mac hMac = Mac.getInstance(config.get(ConfigKey.MAC.getValue()));

									SecretKeySpec hMacKey = new SecretKeySpec(
													Utils.hexStringToByteArray(config.get(ConfigKey.MACKEY.getValue())),
													config.get(ConfigKey.MAC.getValue())
									);
									//inicialize hashe
									hMac.init(hMacKey);
									hMac.update(ptextbytes); //get the plaintext ashe (in this case we use plain text, in the case above we hash the cypher)
									digest = hMac.doFinal();

									//create datagram and send
									datagram = createUDPDatagram(ciphertext, digest);
									return datagram;
									//Socket s = new Socket(desthost, destport);
									//sendUDPDatagram(datagram, s);
								}
					default -> {
							Utils.printInRed("Not Valid Integrity Field ->  INTEGRITY:" + integrity);
							System.exit(0);
					}
			}

			return null;
	}

	





}

