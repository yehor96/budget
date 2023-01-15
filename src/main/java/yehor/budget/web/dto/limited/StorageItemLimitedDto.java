package yehor.budget.web.dto.limited;

import lombok.Builder;
import lombok.Data;
import yehor.budget.common.Currency;

import java.math.BigDecimal;

@Data
@Builder
public class StorageItemLimitedDto {
    private final String name;
    private final Currency currency;
    private final BigDecimal value;
}
