package yehor.budget.web.dto.full;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class IncomeSourceFullDto {
    private Long id;
    private String name;
    private BigDecimal value;
}
