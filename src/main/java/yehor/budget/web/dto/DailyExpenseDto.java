package yehor.budget.web.dto;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class DailyExpenseDto {

    @Hidden
    private long id;
    private int value;
    private LocalDate date;
}