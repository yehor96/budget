package yehor.budget.web.dto;

import lombok.Builder;
import lombok.Data;
import yehor.budget.web.dto.full.IncomeSourceFullDto;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class TotalIncomeDto {
    private List<IncomeSourceFullDto> incomeSources;
    private BigDecimal totalIncome;
}
