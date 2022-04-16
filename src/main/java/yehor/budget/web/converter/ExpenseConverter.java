package yehor.budget.web.converter;

import yehor.budget.entity.DailyExpense;
import yehor.budget.web.dto.DailyExpenseDto;

public class ExpenseConverter {

    public DailyExpenseDto convertToDto(DailyExpense dailyExpense) {
        return DailyExpenseDto.builder()
                .value(dailyExpense.getValue())
                .date(dailyExpense.getDate())
                .build();
    }
}