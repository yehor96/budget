package common.factory;

import lombok.experimental.UtilityClass;
import yehor.budget.entity.Settings;
import yehor.budget.web.dto.full.SettingsFullDto;
import yehor.budget.web.dto.limited.SettingsLimitedDto;

import java.time.LocalDate;

@UtilityClass
public class SettingsFactory {

    public static final long DEFAULT_SETTINGS_ID = 1L;

    public static SettingsFullDto defaultSettingsFullDto() {
        return SettingsFullDto.builder()
                .isBudgetDateValidation(true)
                .budgetStartDate(LocalDate.now().minusDays(30))
                .budgetEndDate(LocalDate.now())
                .estimatedExpenseWorkerInitDelay(5)
                .estimatedExpenseWorkerPeriod(5)
                .estimatedExpenseWorkerEndDateScopePattern("1y")
                .build();
    }

    public static SettingsLimitedDto defaultSettingsLimitedDto() {
        return SettingsLimitedDto.builder()
                .isBudgetDateValidation(true)
                .estimatedExpenseWorkerInitDelay(5)
                .estimatedExpenseWorkerPeriod(5)
                .estimatedExpenseWorkerEndDateScopePattern("1y")
                .build();
    }

    public static Settings defaultSettings() {
        return Settings.builder()
                .id(DEFAULT_SETTINGS_ID)
                .isBudgetDateValidation(true)
                .budgetStartDate(LocalDate.now().minusDays(30))
                .budgetEndDate(LocalDate.now())
                .estimatedExpenseWorkerInitDelay(5)
                .estimatedExpenseWorkerPeriod(5)
                .estimatedExpenseWorkerEndDateScopePattern("1y")
                .build();
    }

    public static Settings settingsWithBudgetDateValidationOff() {
        return Settings.builder()
                .id(DEFAULT_SETTINGS_ID)
                .isBudgetDateValidation(false)
                .budgetStartDate(LocalDate.now().minusDays(30))
                .budgetEndDate(LocalDate.now())
                .estimatedExpenseWorkerInitDelay(5)
                .estimatedExpenseWorkerPeriod(5)
                .estimatedExpenseWorkerEndDateScopePattern("1y")
                .build();
    }

    public static Settings settingsWithNonDefaultEstimatedExpenseWorkerProperties() {
        return Settings.builder()
                .id(DEFAULT_SETTINGS_ID)
                .isBudgetDateValidation(false)
                .budgetStartDate(LocalDate.now().minusDays(30))
                .budgetEndDate(LocalDate.now())
                .estimatedExpenseWorkerInitDelay(3)
                .estimatedExpenseWorkerPeriod(3)
                .estimatedExpenseWorkerEndDateScopePattern("5M")
                .build();
    }
}
