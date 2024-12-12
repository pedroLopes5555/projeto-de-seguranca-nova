package Repository;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface IClientRepository {

    public PrivateKey getPrivateKey();

    public PublicKey getServerPublicKey();
}
