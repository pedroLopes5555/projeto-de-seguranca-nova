package Objects.Payload;


/*
* PBEH(password), Salt, Counter (request, userID, Nonce3+1, Nonce4 , udp_port),
DigitalSig (request, userID, Nonce3+1, Nonce4 , udp_port ),
HMACkmac (X)
*
* MsgType 3 size: Size depending on used cryptographic constructions in message components
request: the request, according to the application (ex., movie or files to transfer)
PBE() : Must choose a secure PasswordBasedEncryption scheme
DigitlSig() : an ECDSA Signature, made with the client ECC private key (with a selected curve)
HMAC(): Must choose a secure HMAC construction, with the kmac derived from the password
X: the content of all (encrypted and signed) components in the message, to allow a fast message
authenticity and integrity check
* */

public class PayloadType3 extends Payload{
}
