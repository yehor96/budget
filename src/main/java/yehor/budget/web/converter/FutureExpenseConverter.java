package yehor.budget.web.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import yehor.budget.entity.FutureExpense;
import yehor.budget.web.dto.full.FutureExpenseFullDto;
import yehor.budget.web.dto.limited.FutureExpenseLimitedDto;

@Component
@RequiredArgsConstructor
public class FutureExpenseConverter {

    public FutureExpense convert(FutureExpenseLimitedDto futureExpenseDto) {
        return FutureExpense.builder()
                .date(futureExpenseDto.getDate())
                .value(futureExpenseDto.getValue())
                .build();
    }

    public FutureExpenseFullDto convert(FutureExpense futureExpense) {
        return FutureExpenseFullDto.builder()
                .id(futureExpense.getId())
                .date(futureExpense.getDate())
                .value(futureExpense.getValue())
                .build();
    }
}
