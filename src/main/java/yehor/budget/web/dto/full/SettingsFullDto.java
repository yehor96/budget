package yehor.budget.web.dto.full;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class SettingsFullDto {
    private LocalDate budgetStartDate;
    private LocalDate budgetEndDate;
    private boolean isBudgetDateValidation;
    private int estimatedExpenseWorkerInitDelay;
    private int estimatedExpenseWorkerPeriod;
    private String estimatedExpenseWorkerEndDateScopePattern;
}
