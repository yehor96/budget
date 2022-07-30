package yehor.budget.common.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
public class RestExceptionHandler {

    private static final Logger LOG = LogManager.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Object> exceptionHandler(Throwable e, HttpServletRequest request) {
        if (e.getClass().equals(ResponseStatusException.class)) {
            ResponseStatusException exception = (ResponseStatusException) e;
            Map<String, Object> responseObject = buildResponseError(request, exception.getStatus(), exception.getReason());
            return new ResponseEntity<>(responseObject, exception.getStatus());
        } else {
            LOG.error("Unknown error occurred", e);
            Map<String, Object> responseObject = buildResponseError(request, INTERNAL_SERVER_ERROR, "Unknown error occurred");
            return new ResponseEntity<>(responseObject, INTERNAL_SERVER_ERROR);
        }
    }

    private Map<String, Object> buildResponseError(HttpServletRequest request, HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", request.getRequestURI());
        return body;
    }

}
