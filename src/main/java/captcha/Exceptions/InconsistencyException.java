package captcha.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception which caused by some inconsistency
 * Generates response with status code 409 'Conflict'
 */
@ResponseStatus(value = HttpStatus.CONFLICT)
public class InconsistencyException extends RuntimeException {

    public InconsistencyException(String message) {
        super(message);
    }

}
