package dstpsend;

import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;




public class GetEncryptedDatagram {



		private static byte[] createUDPDatagram(byte[] ciphertext, byte[] digest) {
			// Version -> 16 bits
			byte[] version = new byte[] { 0x00, 0x01 };
			// Release -> 8 bits
			byte[] release = new byte[] { 0x01 };
	
			// Calculate the total payload length as the sum of ciphertext and digest lengths
			int payloadLength = ciphertext.length + digest.length; // Total size of payload
			byte[] payloadLen = new byte[2];
			
			// Convert payload length to a 16-bit byte array
			payloadLen[0] = (byte) ((payloadLength >> 8) & 0xFF); // High byte
			payloadLen[1] = (byte) (payloadLength & 0xFF); // Low byte
	
			int headerLen = version.length + release.length + payloadLen.length;
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
	








	public static byte[] getEncryptedDatagram(byte[] ptextbytes, int sequenceNumer, String CfgFilePathString) throws Exception {

		// Load data
		IConfigReader configReader = new ConfigReader(CfgFilePathString);
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

		
		// Combine sequence number and plaintext
		byte[] sequenceNumberArray = Utils.intToByteArray(sequenceNumer);
		byte[] sequenceNumberAndPtext = Utils.combineArrays(sequenceNumberArray, ptextbytes);
		// Encrypt the combined array
		byte[] ciphertext = cipher.doFinal(sequenceNumberAndPtext);


		String integrity = config.get(ConfigKey.INTEGRITY.getValue());

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
									hMac.update(ciphertext); //get the plaintext ashe (in this case we use plain text, in the case above we hash the cypher)
									digest = hMac.doFinal();
									int digestSize = Integer.parseInt(config.get(ConfigKey.MACKEY_SIZE.getValue())) / 5; // 256 bits / 8 bits per byte = 32 bytes
									//create datagram and send
									datagram = createUDPDatagram(ciphertext, digest);


									System.out.println("seqeunce number: " + Arrays.toString(sequenceNumberArray));

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

