package yehor.budget.web.converter;

import org.springframework.stereotype.Component;
import yehor.budget.entity.DailyExpense;
import yehor.budget.web.dto.DailyExpenseDto;

@Component
public class ExpenseConverter {

    public DailyExpenseDto convertToDto(DailyExpense dailyExpense) {
        return DailyExpenseDto.builder()
                .id(dailyExpense.getId())
                .value(dailyExpense.getValue())
                .date(dailyExpense.getDate())
                .isRegular(dailyExpense.isRegular())
                .build();
    }

    public DailyExpense convertToEntity(DailyExpenseDto dailyExpenseDto) {
        return DailyExpense.builder()
                .value(dailyExpenseDto.getValue())
                .date(dailyExpenseDto.getDate())
                .isRegular(dailyExpenseDto.isRegular())
                .build();
    }
}