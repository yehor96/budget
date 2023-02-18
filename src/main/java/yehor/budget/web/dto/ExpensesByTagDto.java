package yehor.budget.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import yehor.budget.web.dto.full.ExpenseFullDto;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpensesByTagDto {
    private BigDecimal total;
    private List<ExpenseFullDto> expenses;
}
