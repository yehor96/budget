package yehor.budget.web.dto.full;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class BalanceItemFullDto {
    private Long id;
    private ActorFullDto actor;
    private BigDecimal cash;
    private BigDecimal card;
}
