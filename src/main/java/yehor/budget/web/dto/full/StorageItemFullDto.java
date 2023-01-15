package yehor.budget.web.dto.full;

import lombok.Builder;
import lombok.Data;
import yehor.budget.common.Currency;

import java.math.BigDecimal;

@Data
@Builder
public class StorageItemFullDto {
    private Long id;
    private String name;
    private Currency currency;
    private BigDecimal value;
}
