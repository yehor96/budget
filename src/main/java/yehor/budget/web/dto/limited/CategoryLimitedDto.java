package yehor.budget.web.dto.limited;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class CategoryLimitedDto {
    private String name;
}
