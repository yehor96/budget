package yehor.budget.web.dto.full;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class BalanceRecordFullDto {
    private Long id;
    private LocalDate date;
    private List<BalanceItemFullDto> balanceItems;
    private BigDecimal totalBalance;
    private List<BalanceEstimateDto> balanceEstimates;
}
