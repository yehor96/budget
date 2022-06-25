package yehor.budget.web.dto.limited;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryLimitedDto {
    private String name;
}
