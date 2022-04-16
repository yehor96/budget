package yehor.budget.web.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class DailyExpenseDto {
    private LocalDate date;
    private int value;
}