package yehor.budget.web.dto.full;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class BalanceRecordFullDtoWithoutEstimates {
    private Long id;
    private LocalDate date;
    private List<BalanceItemFullDto> balanceItems;
}
