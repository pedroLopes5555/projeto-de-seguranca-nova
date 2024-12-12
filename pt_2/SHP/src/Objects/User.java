package Objects;

import java.security.PublicKey;

public class User {

    private String userId;
    private String password;
    private byte[] randomSalt;
    private PublicKey pubKey;


    public User(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public User(String userId, String password, byte[] randomSalt, PublicKey pubKey) {
        this.userId = userId;
        this.password = password;
        this.randomSalt = randomSalt;
        this.pubKey = pubKey;
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
