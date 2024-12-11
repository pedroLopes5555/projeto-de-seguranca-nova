import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;


public class ECDSAKeyLoader {
    public static void main(String[] args) throws Exception {
        // Add the BouncyCastle provider
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        // Values provided
        String curve = "secp256k1";
        String privateKeyHex = "0d:8e:7e:18:9a:2e:af:10:a2:6a:fd:f7:3c:9b:93:bc:1f:0d:fa:e5";
        String pubKeyXHex = "21865c4bb14e425c54cbedd90ff9411dcb76b90cfc5bd9b16609f1b44164dbb9";
        String pubKeyYHex = "a57c4ddc0f101ea39a03401879f85d10437fb28537f1dca624bdb78792d018a5";


        //privateKeyHex = privateKeyHex.replaceAll(":", "");

        // Reconstruct private key
        ECNamedCurveParameterSpec spec = org.bouncycastle.jce.ECNamedCurveTable.getParameterSpec(curve);
        ECParameterSpec params = new ECNamedCurveSpec(
                spec.getName(),
                spec.getCurve(),
                spec.getG(),
                spec.getN(),
                spec.getH(),
                spec.getSeed());

        ECPrivateKeySpec privateKeySpec = new ECPrivateKeySpec(
                new java.math.BigInteger(privateKeyHex, 16),
                params);
        KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "BC");
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        // Reconstruct public key
        org.bouncycastle.math.ec.ECPoint bcPoint = spec.getCurve().createPoint(
                new java.math.BigInteger(pubKeyXHex, 16),
                new java.math.BigInteger(pubKeyYHex, 16));

        // Convert BouncyCastle ECPoint to java.security.spec.ECPoint
        java.security.spec.ECPoint javaPoint = new java.security.spec.ECPoint(
                bcPoint.getAffineXCoord().toBigInteger(),
                bcPoint.getAffineYCoord().toBigInteger()
        );

        ECPublicKeySpec publicKeySpec = new ECPublicKeySpec(javaPoint, params);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);


        // Message to sign
        byte[] message = "This is a test message".getBytes();

        // Sign the message
        Signature signature = Signature.getInstance("SHA256withECDSA", "BC");
        signature.initSign(privateKey, new SecureRandom());
        signature.update(message);
        byte[] sigBytes = signature.sign();

        System.out.println("ECDSA Signature: " + Base64.getEncoder().encodeToString(sigBytes));

        // Verify the signature
        signature.initVerify(publicKey);
        signature.update(message);
        boolean isVerified = signature.verify(sigBytes);

        System.out.println("Signature Verified: " + isVerified);
    }
}