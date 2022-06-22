package yehor.budget.web.dto;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryDto {

    @Hidden
    private Long id;
    private String name;
}
