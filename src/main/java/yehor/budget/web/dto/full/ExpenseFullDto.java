package yehor.budget.web.dto.full;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class ExpenseFullDto {
    private Long id;
    private BigDecimal value;
    private LocalDate date;
    private Boolean isRegular;
    private Long categoryId;
}
