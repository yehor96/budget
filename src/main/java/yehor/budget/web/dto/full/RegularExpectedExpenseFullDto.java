package yehor.budget.web.dto.full;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegularExpectedExpenseFullDto {
    private List<RowRegularExpectedExpenseFullDto> rows;
    private BigDecimal total1to7;
    private BigDecimal total8to14;
    private BigDecimal total15to21;
    private BigDecimal total22to31;
    private BigDecimal total;
}
