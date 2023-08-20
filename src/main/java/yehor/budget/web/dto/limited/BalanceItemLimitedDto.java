package yehor.budget.web.dto.limited;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class BalanceItemLimitedDto {
    private String itemName;
    private BigDecimal cash;
    private BigDecimal card;
}
