package Repository;

import Objects.User;

import java.security.PublicKey;

public interface IServerRepository {

    public User getUserById(String userId);

}
