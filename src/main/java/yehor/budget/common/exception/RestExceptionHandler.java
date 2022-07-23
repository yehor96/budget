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
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler {

    private static final Logger LOG = LogManager.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Object> exceptionHandler(Throwable exception, HttpServletRequest request) {
        if (exception.getClass().equals(ResponseStatusException.class)) {
            throw (ResponseStatusException) exception;
        } else {
            LOG.error("Unknown error occurred", exception);
            return new ResponseEntity<>(unknownErrorBody(request), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Map<String, Object> unknownErrorBody(HttpServletRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", 500);
        body.put("error", "Internal Server Error");
        body.put("message", "Unknown error occurred");
        body.put("path", request.getRequestURI());
        return body;
    }

}
