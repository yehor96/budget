package yehor.budget.web.dto.limited;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class FutureExpenseLimitedDto {
    private BigDecimal value;
    private LocalDate date;
}
