package yehor.budget.web.dto.full;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RowEstimatedExpenseFullDto {
    private Long categoryId;
    private BigDecimal days1to7;
    private BigDecimal days8to14;
    private BigDecimal days15to21;
    private BigDecimal days22to31;
    private BigDecimal totalPerRow;
}
