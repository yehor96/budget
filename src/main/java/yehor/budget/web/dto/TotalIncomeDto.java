package yehor.budget.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import yehor.budget.common.Currency;
import yehor.budget.web.dto.full.IncomeSourceFullDto;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TotalIncomeDto {
    private List<IncomeSourceFullDto> incomeSources;
    private BigDecimal total;
    private Currency totalCurrency;
}
