package yehor.budget.web.dto.full;

import lombok.Builder;
import lombok.Data;
import yehor.budget.common.Currency;

import java.math.BigDecimal;

@Data
@Builder
public class StorageItemFullDto {
    private final Long id;
    private final String name;
    private final Currency currency;
    private final BigDecimal value;
}
