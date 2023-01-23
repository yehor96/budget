package yehor.budget.common.date;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import yehor.budget.common.SettingsListener;
import yehor.budget.common.SettingsNotificationManager;
import yehor.budget.entity.Settings;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DateManager implements SettingsListener {

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
            log.info("End date is changed from {} to {}", endDate, date);
            endDate = date;
            shouldUpdateDb = true;
        } else if (date.isBefore(startDate)) {
            log.info("Start date is changed from {} to {}", startDate, date);
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

    public boolean isValidLocalDatePattern(String pattern) {
        try {
            LocalDate.parse(pattern);
            return true;
        } catch (DateTimeParseException e) {
            return false;
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

    public LocalDate getMonthEndDate(LocalDate date) {
        return LocalDate.of(
                date.getYear(),
                date.getMonth(),
                getLastDayOfMonthByDate(date)
        );
    }

    public void validateDayOfMonth(Integer day) {
        if (day < 1 || day > 31) {
            throw new IllegalArgumentException("Provided value is not a day of month - " + day);
        }
    }

    public int getLastDayOfMonthByDate(LocalDate date) {
        int lastDay;
        if (date.getMonth() == (Month.FEBRUARY)) {
            if (Year.isLeap(date.getYear())) {
                lastDay = 29;
            } else {
                lastDay = 28;
            }
        } else {
            lastDay = date.getMonth().maxLength();
        }
        return lastDay;
    }
}
