package yehor.budget.manager.date;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import yehor.budget.exception.CustomExceptionManager;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Component
public class DateManager {

    public static final LocalDate START_DATE = LocalDate.of(2022, 3, 22);

    @Getter
    @Setter
    private static LocalDate endDate = LocalDate.of(2022, 3, 31);

    public LocalDate parse(String value) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw CustomExceptionManager.getIllegalDateArgumentProvidedException(value);
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
            throw CustomExceptionManager.getOutOfBudgetDateArgumentException(date);
        }
    }

    public void validateDateAfterStart(LocalDate date) {
        if (date.isBefore(START_DATE)) {
            throw CustomExceptionManager.getOutOfBudgetDateArgumentException(date);
        }
    }

    public void validateDatesWithinBudget(LocalDate date1, LocalDate date2) {
        if (!areWithinBudget(date1, date2)) {
            throw CustomExceptionManager.getOutOfBudgetDateArgumentException(date1, date2);
        }
    }

    public void validateDatesInSequentialOrder(LocalDate date1, LocalDate date2) {
        if (date1.isAfter(date2)) {
            throw new IllegalArgumentException("Reversed order of provided dates: " + date1 + " and " + date2);
        }
    }

}
