package rs.ac.ftn.isa.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class TooManyCommentsException extends RuntimeException {
    public TooManyCommentsException(String message) {
        super(message);
    }
}
