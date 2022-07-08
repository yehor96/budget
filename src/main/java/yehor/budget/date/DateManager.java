package yehor.budget.date;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static yehor.budget.exception.DateExceptionProvider.illegalDateArgumentProvidedException;
import static yehor.budget.exception.DateExceptionProvider.invalidFullMonthException;
import static yehor.budget.exception.DateExceptionProvider.outOfBudgetDateArgumentException;
import static yehor.budget.exception.DateExceptionProvider.reversedOrderOfDatesException;
import static yehor.budget.exception.DateExceptionProvider.reversedOrderOfMonthsException;

@Component
public class DateManager {

    private static final Logger LOG = LogManager.getLogger(DateManager.class);

    public static final LocalDate START_DATE = LocalDate.now().minusDays(30);
    @Getter
    private static LocalDate endDate = LocalDate.now();

    public static void updateEndDateIfNecessary(LocalDate date) {
        if (date.isAfter(endDate)) {
            LOG.info("End date is changed from {} to {}", endDate, date);
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

    public List<FullMonth> getMonthsListIn(FullMonth startMonth, FullMonth endMonth) {
        List<FullMonth> months = new ArrayList<>();
        FullMonth currentMonth = startMonth;
        while (!currentMonth.equals(endMonth)) {
            months.add(currentMonth);
            currentMonth = currentMonth.next();
        }
        months.add(currentMonth);
        return months;
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

    public void validateMonthWithinBudget(FullMonth fullMonth) {
        Integer year = fullMonth.getYear();
        int month = fullMonth.getMonth().getValue();
        int startYear = START_DATE.getYear();
        int endYear = endDate.getYear();
        int startMonth = START_DATE.getMonthValue();
        int endMonth = endDate.getMonthValue();

        if (startYear > year || endYear < year) {
            throw invalidFullMonthException(fullMonth);
        }
        if (startYear == year && startMonth > month) {
            throw invalidFullMonthException(fullMonth);
        }
        if (endYear == year && endMonth < month) {
            throw invalidFullMonthException(fullMonth);
        }
    }

    public void validateMonthsInSequentialOrder(FullMonth startMonth, FullMonth endMonth) {
        int startYearValue = startMonth.getYear();
        int endYearValue = endMonth.getYear();
        int startMonthValue = startMonth.getMonth().getValue();
        int endMonthValue = endMonth.getMonth().getValue();

        if (startYearValue > endYearValue ||
                (startYearValue == endYearValue && startMonthValue > endMonthValue)) {
            throw reversedOrderOfMonthsException(startMonth, endMonth);
        }
    }
}
