package yehor.budget.web.dto.full;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class FutureExpenseFullDto {
    private final Long id;
    private BigDecimal value;
    private LocalDate date;
}
