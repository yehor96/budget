package yehor.budget.common.date;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yehor.budget.common.SettingsListener;
import yehor.budget.common.SettingsNotificationManager;
import yehor.budget.entity.Settings;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class DateManager implements SettingsListener {

    private static final Logger LOG = LogManager.getLogger(DateManager.class);

    private Boolean isBudgetDateValidation;
    @Getter
    private LocalDate startDate;
    @Getter
    private LocalDate endDate;

    public DateManager(Settings settings) {
        startDate = settings.getBudgetStartDate();
        endDate = settings.getBudgetEndDate();
        isBudgetDateValidation = settings.getIsBudgetDateValidation();
    }

    public void updateBudgetDatesIfNecessary(LocalDate date) {
        boolean shouldUpdateDb = false;
        if (date.isAfter(endDate)) {
            LOG.info("End date is changed from {} to {}", endDate, date);
            endDate = date;
            shouldUpdateDb = true;
        } else if (date.isBefore(startDate)) {
            LOG.info("Start date is changed from {} to {}", startDate, date); //TODO test
            startDate = date;
            shouldUpdateDb = true;
        }

        if (shouldUpdateDb) {
            notifySettingsUpdate();
        }
    }

    public LocalDate parse(String value) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Provided value is not a valid date " + value);
        }
    }

    public boolean isWithinBudget(LocalDate date) {
        if (Boolean.FALSE.equals(isBudgetDateValidation)) {
            return true;
        }
        return Interval.of(startDate, endDate).isWithin(date);
    }

    public boolean areWithinBudget(LocalDate date1, LocalDate date2) {
        if (Boolean.FALSE.equals(isBudgetDateValidation)) {
            return true;
        }
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
        if (Boolean.FALSE.equals(isBudgetDateValidation)) {
            return;
        }
        if (date.isBefore(startDate)) {
            throw new IllegalArgumentException(incorrectDateArgumentMessage() + " Provided date is " + date);
        }
    }

    public void validateDatesWithinBudget(LocalDate date1, LocalDate date2) {
        if (Boolean.FALSE.equals(isBudgetDateValidation)) {
            return;
        }
        if (!areWithinBudget(date1, date2)) {
            throw new IllegalArgumentException(
                    incorrectDateArgumentMessage() + String.format(" Provided dates are %s and %s", date1, date2));
        }
    }

    public void validateDatesInSequentialOrder(LocalDate date1, LocalDate date2) {
        if (date1.isAfter(date2)) {
            throw new IllegalArgumentException(String.format("Reversed order of dates: %s and %s", date1, date2));
        }
    }

    public void validateMonthWithinBudget(FullMonth fullMonth) {
        if (Boolean.FALSE.equals(isBudgetDateValidation)) {
            return;
        }

        Integer year = fullMonth.getYear();
        int month = fullMonth.getMonth().getValue();
        int startYear = startDate.getYear();
        int endYear = endDate.getYear();
        int startMonth = startDate.getMonthValue();
        int endMonth = endDate.getMonthValue();

        if ((startYear > year || endYear < year)
                || (startYear == year && startMonth > month)
                || (endYear == year && endMonth < month)) {
            throw new IllegalArgumentException("Provided value is invalid " + fullMonth);
        }
    }

    public void validateMonthsInSequentialOrder(FullMonth startMonth, FullMonth endMonth) {
        int startYearValue = startMonth.getYear();
        int endYearValue = endMonth.getYear();
        int startMonthValue = startMonth.getMonth().getValue();
        int endMonthValue = endMonth.getMonth().getValue();

        if (startYearValue > endYearValue ||
                (startYearValue == endYearValue && startMonthValue > endMonthValue)) {
            throw new IllegalArgumentException(
                    String.format("Reversed order of provided months: %s and %s", startMonth, endMonth));
        }
    }

    @Override
    public void onUpdate(Settings settings) {
        isBudgetDateValidation = settings.getIsBudgetDateValidation();
    }

    private void notifySettingsUpdate() {
        Settings settings = Settings.builder()
                .budgetEndDate(endDate)
                .budgetStartDate(startDate)
                .build();
        SettingsNotificationManager.updateListeners(this.getClass(), settings);
    }

    private String incorrectDateArgumentMessage() {
        return "Date argument is out of budget. Start date is " + startDate + ". End date is " + endDate + ".";
    }
}
