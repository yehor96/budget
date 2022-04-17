package yehor.budget.exception;

import yehor.budget.manager.date.DateManager;

import java.time.LocalDate;

public class OutOfBudgetDateArgumentException extends RuntimeException {

    private static final String EXCEPTION_MESSAGE =
            "Date is out of budget period. Start date is " + DateManager.START_DATE +
                    ", end date is " + DateManager.getEndDate();

    public OutOfBudgetDateArgumentException(LocalDate date) {
        super(EXCEPTION_MESSAGE + ". Provided date is " + date);
    }

    public OutOfBudgetDateArgumentException(LocalDate date1, LocalDate date2) {
        super(EXCEPTION_MESSAGE + ". Provided dates are " + date1 + " and " + date2);
    }
}
