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
                .build();
    }

    public Settings convert(SettingsLimitedDto settingsLimitedDto) {
        return Settings.builder()
                .isBudgetDateValidation(settingsLimitedDto.getIsBudgetDateValidation())
                .build();
    }
}
