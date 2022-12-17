package yehor.budget.web.dto.limited;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class BalanceItemLimitedDto {
    private Long actorId;
    private BigDecimal cash;
    private BigDecimal card;
}
