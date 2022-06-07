package yehor.budget.exception;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import yehor.budget.manager.date.DateManager;

import java.time.LocalDate;

@UtilityClass
public class DateExceptionProvider {

    private static final String OUT_OF_BUDGET_EXCEPTION_MESSAGE = "Date is out of budget period. Start date is "
            + DateManager.START_DATE + ", end date is " + DateManager.getEndDate();

    public static ResponseStatusException getOutOfBudgetDateArgumentException(LocalDate date) {
        return new CustomResponseStatusException(HttpStatus.BAD_REQUEST,
                OUT_OF_BUDGET_EXCEPTION_MESSAGE + ". Provided date is " + date);
    }

    public static ResponseStatusException getOutOfBudgetDateArgumentException(LocalDate date1, LocalDate date2) {
        return new CustomResponseStatusException(HttpStatus.BAD_REQUEST,
                OUT_OF_BUDGET_EXCEPTION_MESSAGE + ". Provided dates are " + date1 + " and " + date2);
    }

    public static ResponseStatusException getIllegalDateArgumentProvidedException(String value) {
        return new CustomResponseStatusException(HttpStatus.BAD_REQUEST, "Provided value is not valid: " + value);
    }
}
