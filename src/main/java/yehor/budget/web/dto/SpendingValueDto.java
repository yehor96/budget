package yehor.budget.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SpendingValueDto {
    private int value;
}
