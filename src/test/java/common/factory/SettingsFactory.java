package common.factory;

import lombok.experimental.UtilityClass;
import yehor.budget.web.dto.full.SettingsFullDto;
import yehor.budget.web.dto.limited.SettingsLimitedDto;

import java.time.LocalDate;

@UtilityClass
public class SettingsFactory {

    public static SettingsFullDto defaultSettingsFullDto() {
        return SettingsFullDto.builder()
                .isBudgetDateValidation(true)
                .budgetStartDate(LocalDate.now().minusDays(30))
                .budgetEndDate(LocalDate.now())
                .build();
    }

    public static SettingsLimitedDto defaultSettingsLimitedDto() {
        return SettingsLimitedDto.builder()
                .isBudgetDateValidation(true)
                .build();
    }
}
