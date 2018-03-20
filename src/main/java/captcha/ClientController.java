package captcha;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Class to handle all requests related to client registration
 */
@RestController
@RequestMapping("/client")
public class ClientController {

    /**
     * Contain all existing clients in database
     */
    @Autowired
    private ClientRepository clientRepository;

    /**
     * Handles get request 'client/request' to create new client
     * @return Client object as json string
     */
    @RequestMapping(method = RequestMethod.GET, value = "/register")
    public Client register() {
        Client client = new Client(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        return clientRepository.save(client);
    }
}
