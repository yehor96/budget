package yehor.budget.web.converter;

import org.springframework.stereotype.Component;
import yehor.budget.entity.Settings;
import yehor.budget.web.dto.full.SettingsFullDto;
import yehor.budget.web.dto.limited.SettingsLimitedDto;

@Component
public class SettingsConverter {

    public SettingsFullDto convert(Settings settings) {
        return SettingsFullDto.builder()
                .isBudgetDateValidation(settings.getIsBudgetDateValidation())
                .budgetStartDate(settings.getBudgetStartDate())
                .budgetEndDate(settings.getBudgetEndDate())
                .estimatedExpenseWorkerInitDelay(settings.getEstimatedExpenseWorkerInitDelay())
                .estimatedExpenseWorkerPeriod(settings.getEstimatedExpenseWorkerPeriod())
                .estimatedExpenseWorkerEndDateScopePattern(settings.getEstimatedExpenseWorkerEndDateScopePattern())
                .build();
    }

    public Settings convert(SettingsLimitedDto settingsLimitedDto) {
        return Settings.builder()
                .isBudgetDateValidation(settingsLimitedDto.getIsBudgetDateValidation())
                .estimatedExpenseWorkerInitDelay(settingsLimitedDto.getEstimatedExpenseWorkerInitDelay())
                .estimatedExpenseWorkerPeriod(settingsLimitedDto.getEstimatedExpenseWorkerPeriod())
                .estimatedExpenseWorkerEndDateScopePattern(settingsLimitedDto.getEstimatedExpenseWorkerEndDateScopePattern())
                .build();
    }
}
