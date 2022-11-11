package yehor.budget.web.dto.limited;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized //annotation used when Dto only contains one field
public class ActorLimitedDto {
    private String name;
}
