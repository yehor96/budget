package yehor.budget.common.date;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import yehor.budget.entity.Settings;
import yehor.budget.service.SettingsService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class DateManager {

    private static final Logger LOG = LogManager.getLogger(DateManager.class);

    private final SettingsService settingsService;

    private static Boolean isBudgetDateValidation;
    @Getter
    private LocalDate startDate;
    @Getter
    private LocalDate endDate;

    public DateManager(SettingsService settingsService) {
        this.settingsService = settingsService;
        Settings settings = settingsService.getSettingsEntity();
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

    public boolean isWithinBudget(LocalDate date) { //TODO test
        if (Boolean.FALSE.equals(isBudgetDateValidation)) {
            return true;
        }
        return Interval.of(startDate, endDate).isWithin(date);
    }

    public boolean areWithinBudget(LocalDate date1, LocalDate date2) { //TODO test
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
        if (Boolean.FALSE.equals(isBudgetDateValidation)) { //TODO test
            return;
        }
        if (date.isBefore(startDate)) {
            throw new IllegalArgumentException(incorrectDateArgumentMessage() + " Provided date is " + date);
        }
    }

    public void validateDatesWithinBudget(LocalDate date1, LocalDate date2) {
        if (Boolean.FALSE.equals(isBudgetDateValidation)) { //TODO test
            return;
        }
        if (!areWithinBudget(date1, date2)) {
            throw new IllegalArgumentException(incorrectDateArgumentMessage() +
                    " Provided dates are " + date1 + " and " + date2);
        }
    }

    public void validateDatesInSequentialOrder(LocalDate date1, LocalDate date2) {
        if (date1.isAfter(date2)) {
            throw new IllegalArgumentException("Reversed order of provided dates: " + date1 + " and " + date2);
        }
    }

    public void validateMonthWithinBudget(FullMonth fullMonth) {
        if (Boolean.FALSE.equals(isBudgetDateValidation)) { //TODO test
            return;
        }

        Integer year = fullMonth.getYear();
        int month = fullMonth.getMonth().getValue();
        int startYear = startDate.getYear();
        int endYear = endDate.getYear();
        int startMonth = startDate.getMonthValue();
        int endMonth = endDate.getMonthValue();

        if (startYear > year || endYear < year) {
            throw new IllegalArgumentException("Provided value is invalid " + fullMonth);
        }
        if (startYear == year && startMonth > month) {
            throw new IllegalArgumentException("Provided value is invalid " + fullMonth);
        }
        if (endYear == year && endMonth < month) {
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
            throw new IllegalArgumentException("Reversed order of provided months: " + startMonth + " and " + endMonth);
        }
    }

    public static void updateWithSettings(Settings settings) {
        isBudgetDateValidation = settings.getIsBudgetDateValidation();
    }

    private void notifySettingsUpdate() {
        Settings settings = Settings.builder()
                .budgetEndDate(endDate)
                .budgetStartDate(startDate)
                .build();
        settingsService.updateSettings(settings);
    }

    private String incorrectDateArgumentMessage() {
        return "Date argument is out of budget. Start date is " + startDate + ". End date is " + endDate + ".";
    }
}
