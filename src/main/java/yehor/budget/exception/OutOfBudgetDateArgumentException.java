package yehor.budget.exception;

import java.time.LocalDate;

import static yehor.budget.util.Constants.END_DATE;
import static yehor.budget.util.Constants.START_DATE;

public class OutOfBudgetDateArgumentException extends RuntimeException {

    private static final String EXCEPTION_MESSAGE =
            "Date is out of budget period. Start date is " + START_DATE + ", end date is " + END_DATE;

    public OutOfBudgetDateArgumentException(LocalDate date) {
        super(EXCEPTION_MESSAGE + ". Provided date is " + date);
    }

    public OutOfBudgetDateArgumentException(LocalDate date1, LocalDate date2) {
        super(EXCEPTION_MESSAGE + ". Provided dates are " + date1 + " and " + date2);
    }
}
