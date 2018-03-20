package captcha;

import captcha.Exceptions.InconsistencyException;
import captcha.Exceptions.NotFoundException;
import captcha.Exceptions.TimeOutException;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

/**
 * Class to handle all request related to CAPTCHA tasks
 */
@RestController
@RequestMapping("/captcha")
public class CaptchaController {
    private final static int ID_LEN = 8; // Length of id of CAPTCHA task
    private final static int TOKEN_LEN = 8; // Length of verification token
    private final static int ANSWER_LEN = 6; // Length of answer of CAPTCHA task

    /**
     * Contain all existing clients in database
     */
    @Autowired
    private ClientRepository clients;

    /**
     * Contain all existing CAPTCHA tasks in database
     */
    @Autowired
    private CaptchaTaskRepository captchaTasks;

    /**
     * Contain all existing tokens in database
     */
    @Autowired
    private TokenRepository tokens;

    /**
     * Handles get request 'captcha/new' to create new CAPTCHA task
     * @param clientPublicKey client public key from request
     * @return jsonString
     * @throws NotFoundException if client from request don't exist
     */
    @RequestMapping(method = RequestMethod.GET, value = "/new", produces = MediaType.APPLICATION_JSON_VALUE)
    public String newTask(@RequestParam(value = "public") String clientPublicKey) {

        Client client = clients.findByPublicKey(clientPublicKey);
        if (client == null)
            throw new NotFoundException("Client not found");

        String id = RandomStringUtils.randomAlphanumeric(ID_LEN);
        String answer = RandomStringUtils.randomAlphanumeric(ANSWER_LEN);
        Instant expireDateInstant = Calendar.getInstance().getTime().toInstant().
                plusSeconds(Long.parseLong(System.getProperty("ttl")));

        CaptchaTask captchaTask = new CaptchaTask(id, answer, client, Date.from(expireDateInstant));
        return captchaTasks.save(captchaTask).toJSON();
    }

    /**
     * Handles get request 'captcha/image' to get image of task
     * @param clientPublicKey client public key from request
     * @param taskId CAPTCHA task id from request
     * @return image as byte array
     * @throws NotFoundException if client or task from task don't exist
     * @throws InconsistencyException if task from request don't belong to client from request
     * @throws TimeOutException if the for solution has expired
     */
    @RequestMapping(method = RequestMethod.GET, value = "/image", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getImage(@RequestParam(value = "public") String clientPublicKey,
                           @RequestParam(value = "request") String taskId) throws IOException {

        Client client = clients.findByPublicKey(clientPublicKey);
        if (client == null)
            throw new NotFoundException("Client not found");

        CaptchaTask captchaTask;
        if (captchaTasks.findById(taskId).isPresent()) {
            captchaTask = captchaTasks.findById(taskId).get();
        } else {
            throw new NotFoundException("Task not found");
        }

        if (!captchaTask.getClient().equals(client))
            throw new InconsistencyException("Task do not belong to client");

        if (captchaTask.getExpireDate().before(Calendar.getInstance().getTime())) {
            captchaTasks.delete(captchaTask);
            throw new TimeOutException("The time for solution has expired");
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(captchaTask.getImage(), "png", stream);
        return stream.toByteArray();
    }

    /**
     * Handles post request 'captcha/solve' to check clients answer and return
     * verification token if there is no errors
     * @param clientPublicKey client public key from request
     * @param taskId CAPTCHA task id from request
     * @param taskAnswer client's solution from request
     * @return Token object as json string
     * @throws NotFoundException if client or task from task don't exist
     * @throws InconsistencyException if task from request don't belong to client from request
     * @throws TimeOutException if the for solution has expired
     */
    @RequestMapping(method = RequestMethod.POST, value = "/solve")
    public Token checkSolution(@RequestParam(value = "public") String clientPublicKey,
                               @RequestParam(value = "request") String taskId,
                               @RequestParam(value = "answer") String taskAnswer) {

        Client client = clients.findByPublicKey(clientPublicKey);
        if (client == null)
            throw new NotFoundException("Client not found");

        CaptchaTask captchaTask;
        if (captchaTasks.findById(taskId).isPresent()) {
            captchaTask = captchaTasks.findById(taskId).get();
        } else {
            throw new NotFoundException("Task not found");
        }

        if (!captchaTask.getClient().equals(client))
            throw new InconsistencyException("Task do not belong to client");

        if (captchaTask.getExpireDate().before(Calendar.getInstance().getTime())) {
            captchaTasks.delete(captchaTask);
            throw new TimeOutException("The time for solution has expired");
        }

        if (!captchaTask.getAnswer().equals(taskAnswer))
            throw new InconsistencyException("Wrong solution");

        captchaTasks.delete(captchaTask);

        String tokenValue = RandomStringUtils.randomAlphanumeric(TOKEN_LEN);
        Token token = new Token(tokenValue, client);
        return tokens.save(token);
    }

    /**
     * Handles get request 'captcha/verify'
     * @param clientPrivateKey client public key from request
     * @param tokenValue verification token value from request
     * @return VerificationResponse object as json string
     */
    @RequestMapping(method = RequestMethod.GET, value = "/verify")
    public VerificationResponse verifyToken(@RequestParam(value = "secret") String clientPrivateKey,
                                       @RequestParam(value = "response") String tokenValue) {

        Client client = clients.findByPrivateKey(clientPrivateKey);
        if (client == null)
            return new VerificationResponse(false, "Client not found");

        Token token;
        if (tokens.findById(tokenValue).isPresent()) {
            token = tokens.findById(tokenValue).get();
        } else {
            return new VerificationResponse(false, "Token not found");
        }

        if (!token.getClient().equals(client))
            return new VerificationResponse(false, "Token do not belong to client");

        tokens.delete(token);

        return new VerificationResponse(true, null);
    }
}
