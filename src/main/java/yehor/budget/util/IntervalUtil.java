package yehor.budget.util;

import yehor.budget.util.model.Interval;

import java.time.LocalDate;
import java.util.Objects;

import static yehor.budget.util.Constants.START_DATE;

public class IntervalUtil {

    private static Interval budgetInterval;

    public boolean isWithinBudget(LocalDate date) {
        return getBudgetInterval().isWithin(date);
    }

    public boolean areWithinBudget(LocalDate date1, LocalDate date2) {
        return isWithinBudget(date1) && isWithinBudget(date2);
    }

    private static Interval getBudgetInterval() {
        if (Objects.isNull(budgetInterval)) {
            budgetInterval = Interval.of(START_DATE, LocalDate.now());
        }
        return budgetInterval;
    }

}
