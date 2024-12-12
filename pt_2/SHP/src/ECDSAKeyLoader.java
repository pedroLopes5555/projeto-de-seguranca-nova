import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;

import java.math.BigInteger;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class ECDSAKeyLoader {
    public static void main(String[] args) throws Exception {
        // Add the BouncyCastle provider
        Security.addProvider(new BouncyCastleProvider());
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        // Values provided
        String curve = "secp256k1";
        String privateKeyHex = "56b1258b7d5900b8dcdf4d37f451478721e2a595";
        String pubKeyXHex = "dee5e293897264d73f6970e2d4e6ce817f6e12debb729accc091a1b5d5eea181";
        String pubKeyYHex = "31ed1700f9a70eeeda4ae106b7db37d68f5d5c4c8c2edf1f5644a74bce3d6483";



        // Reconstruct private key
        ECNamedCurveParameterSpec spec = org.bouncycastle.jce.ECNamedCurveTable.getParameterSpec(curve);
        ECParameterSpec params = new ECNamedCurveSpec(
                spec.getName(),
                spec.getCurve(),
                spec.getG(),
                spec.getN(),
                spec.getH(),
                spec.getSeed());

        ECPrivateKeySpec privateKeySpec = new ECPrivateKeySpec(new java.math.BigInteger(privateKeyHex, 16),
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