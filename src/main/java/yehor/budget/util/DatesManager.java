package yehor.budget.util;

import org.springframework.stereotype.Component;
import yehor.budget.exception.OutOfBudgetDateArgumentException;
import yehor.budget.util.model.Interval;

import java.time.LocalDate;

import static yehor.budget.util.Constants.END_DATE;
import static yehor.budget.util.Constants.START_DATE;

@Component
public class DatesManager {

    private static final Interval BUDGET_INTERVAL = Interval.of(START_DATE, END_DATE);

    public boolean isWithinBudget(LocalDate date) {
        return BUDGET_INTERVAL.isWithin(date);
    }

    public boolean areWithinBudget(LocalDate date1, LocalDate date2) {
        return isWithinBudget(date1) && isWithinBudget(date2);
    }

    public void validateDate(LocalDate date) {
        if (!isWithinBudget(date)) {
            throw new OutOfBudgetDateArgumentException(date);
        }
    }

    public void validateDates(LocalDate date1, LocalDate date2) {
        if (date1.isAfter(date2)) {
            throw new IllegalArgumentException("Reversed order of provided dates: " + date1 + " and " + date2);
        }
        if (!areWithinBudget(date1, date2)) {
                throw new OutOfBudgetDateArgumentException(date1, date2);
        }
    }

}
