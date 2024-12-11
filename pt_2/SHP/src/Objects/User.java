package Objects;

public class User {

    private String userId;
    private String password;
    private byte[] randomSalt;
    private  byte[] ECCPublicKey;


    public User(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public User(String userId, String password, byte[] randomSalt, byte[] ECCPublicKey) {
        this.userId = userId;
        this.password = password;
        this.randomSalt = randomSalt;
        this.ECCPublicKey = ECCPublicKey;
    }


    public byte[] getRandomSalt() {
        return randomSalt;
    }

    public byte[] getECCPublicKey() {
        return ECCPublicKey;
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
