package Objects;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.PublicKey;

public class User {

    private String userId;
    private String password;
    private byte[] hasehdPassword;
    private byte[] randomSalt;
    private PublicKey pubKey;


    public User(String userId, String password) throws Exception {
        this.userId = userId;
        this.password = password;
        getPasswordHash();
    }

    public User(String userId, String password, byte[] randomSalt, PublicKey pubKey) throws Exception {
        this.userId = userId;
        this.password = password;
        this.randomSalt = randomSalt;
        this.pubKey = pubKey;
        getPasswordHash();
    }

    public byte[] getHasehdPassword() {
        return hasehdPassword;
    }

    private void getPasswordHash() throws Exception{
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        this.hasehdPassword = digest.digest(this.password.getBytes(StandardCharsets.UTF_8));
    }
    public byte[] getRandomSalt() {
        return randomSalt;
    }

    public PublicKey getECCPublicKey() {
        return pubKey;
    }

    public User(String userId) {
        this.userId = userId;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public boolean isEmpty(){
        return (this.userId == null || this.password == null);
    }
}
