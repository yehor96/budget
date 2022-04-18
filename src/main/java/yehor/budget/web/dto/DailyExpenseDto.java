package yehor.budget.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class DailyExpenseDto {
    private int value;
    private LocalDate date;
}