package yehor.budget.web.converter;

import org.springframework.stereotype.Component;
import yehor.budget.entity.Expense;
import yehor.budget.web.dto.ExpenseDto;

@Component
public class ExpenseConverter {

    public ExpenseDto convert(Expense expense) {
        return ExpenseDto.builder()
                .id(expense.getId())
                .value(expense.getValue())
                .date(expense.getDate())
                .isRegular(expense.getIsRegular())
                .categoryId(expense.getCategory().getId())
                .build();
    }

    public Expense convert(ExpenseDto expenseDto) {
        return Expense.builder()
                .value(expenseDto.getValue())
                .date(expenseDto.getDate())
                .isRegular(expenseDto.getIsRegular())
                .build();
    }
}