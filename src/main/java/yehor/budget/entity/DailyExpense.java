package yehor.budget.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class DailyExpense {
    private int value;
    private LocalDate date;
}
