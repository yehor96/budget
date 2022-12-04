package yehor.budget.web.dto.limited;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SettingsLimitedDto {
    private Boolean isBudgetDateValidation;
    private int estimatedExpenseWorkerInitDelay;
    private int estimatedExpenseWorkerPeriod;
    private String estimatedExpenseWorkerEndDateScopePattern;
}