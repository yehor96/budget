package yehor.budget.web.dto.limited;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class IncomeSourceLimitedDto {
    private String name;
    private BigDecimal value;
}
