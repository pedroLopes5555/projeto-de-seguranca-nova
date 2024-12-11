package Repository;

import Objects.User;

public interface IServerRepository {

    public User getUserById(String userId);
}
