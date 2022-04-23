package yehor.budget.exception;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import yehor.budget.manager.date.DateManager;

import java.time.LocalDate;

@UtilityClass
public class CustomExceptionManager {

    private static final String OUT_OF_BUDGET_EXCEPTION_MESSAGE = "Date is out of budget period. Start date is "
            + DateManager.START_DATE + ", end date is " + DateManager.getEndDate();

    public static ResponseStatusException getOutOfBudgetDateArgumentException(LocalDate date) {
        return new CustomException(HttpStatus.BAD_REQUEST,
                OUT_OF_BUDGET_EXCEPTION_MESSAGE + ". Provided date is " + date);
    }

    public static ResponseStatusException getOutOfBudgetDateArgumentException(LocalDate date1, LocalDate date2) {
        return new CustomException(HttpStatus.BAD_REQUEST,
                OUT_OF_BUDGET_EXCEPTION_MESSAGE + ". Provided dates are " + date1 + " and " + date2);
    }

    public static ResponseStatusException getIllegalDateArgumentProvidedException(String value) {
        return new CustomException(HttpStatus.BAD_REQUEST, "Provided value is not valid: " + value);
    }

    public static ResponseStatusException getDateNotFoundException(LocalDate date) {
        return new CustomException(HttpStatus.NOT_FOUND, "Records for " + date + " are not found.");
    }

    public static class CustomException extends ResponseStatusException {
        public CustomException(HttpStatus status, String message) {
            super(status, message);
        }
    }
}
