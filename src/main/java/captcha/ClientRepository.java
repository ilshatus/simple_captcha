package captcha;

import org.springframework.data.repository.CrudRepository;

public interface ClientRepository extends CrudRepository<Client, Long> {
    Client findByPrivateKey(String privateKey);

    Client findByPublicKey(String publicKey);
}
