package yehor.budget.web.dto.limited;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class ExpenseLimitedDto {
    private BigDecimal value;
    private LocalDate date;
    private boolean isRegular;
    private Long categoryId;
    private Set<Long> tagIds;
    private String note;
}
