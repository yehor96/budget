package yehor.budget.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyStatistics {

    private BigDecimal totalExpense = BigDecimal.ZERO;
    private BigDecimal totalRegular = BigDecimal.ZERO;
    private BigDecimal totalNonRegular = BigDecimal.ZERO;
    private Map<String, BigDecimal> totalsPerCategory;
}
