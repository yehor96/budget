package yehor.budget.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CustomResponseStatusException extends ResponseStatusException {
    public CustomResponseStatusException(HttpStatus status, String message) {
        super(status, message);
    }
}
