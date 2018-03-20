package captcha.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception which caused by time expiration
 * Generates response with status code 408 'Request time out'
 */
@ResponseStatus(value = HttpStatus.REQUEST_TIMEOUT)
public class TimeOutException extends RuntimeException {

    public TimeOutException(String message) {
        super(message);
    }
}
