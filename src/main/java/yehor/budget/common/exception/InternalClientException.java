package yehor.budget.common.exception;

public class InternalClientException extends RuntimeException {
    public InternalClientException(String message, Exception e) {
        super(message, e);
    }
}
