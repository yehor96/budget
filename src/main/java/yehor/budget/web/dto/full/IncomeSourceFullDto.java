package yehor.budget.web.dto.full;

import lombok.Builder;
import lombok.Data;
import yehor.budget.common.Currency;
import yehor.budget.service.client.currency.Exchangeable;

import java.math.BigDecimal;

@Data
@Builder
public class IncomeSourceFullDto implements Exchangeable {
    private Long id;
    private String name;
    private BigDecimal value;
    private Currency currency;
    private Integer accrualDayOfMonth;
}
