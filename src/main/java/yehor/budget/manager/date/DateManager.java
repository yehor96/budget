package yehor.budget.manager.date;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static yehor.budget.exception.DateExceptionProvider.illegalDateArgumentProvidedException;
import static yehor.budget.exception.DateExceptionProvider.outOfBudgetDateArgumentException;
import static yehor.budget.exception.DateExceptionProvider.reversedOrderOfDatesException;

@Component
public class DateManager {

    public static final LocalDate START_DATE = LocalDate.now().minusDays(30);
    @Getter
    private static LocalDate endDate = LocalDate.now();

    public static void updateEndDateIfNecessary(LocalDate date) {
        if (date.isAfter(endDate)) {
            endDate = date;
        }
    }

    public LocalDate parse(String value) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw illegalDateArgumentProvidedException(value);
        }
    }

    public boolean isWithinBudget(LocalDate date) {
        return Interval.of(START_DATE, endDate).isWithin(date);
    }

    public boolean areWithinBudget(LocalDate date1, LocalDate date2) {
        return isWithinBudget(date1) && isWithinBudget(date2);
    }

    public void validateDateWithinBudget(LocalDate date) {
        if (!isWithinBudget(date)) {
            throw outOfBudgetDateArgumentException(date);
        }
    }

    public void validateDateAfterStart(LocalDate date) {
        if (date.isBefore(START_DATE)) {
            throw outOfBudgetDateArgumentException(date);
        }
    }

    public void validateDatesWithinBudget(LocalDate date1, LocalDate date2) {
        if (!areWithinBudget(date1, date2)) {
            throw outOfBudgetDateArgumentException(date1, date2);
        }
    }

    public void validateDatesInSequentialOrder(LocalDate date1, LocalDate date2) {
        if (date1.isAfter(date2)) {
            throw reversedOrderOfDatesException(date1, date2);
        }
    }

}
