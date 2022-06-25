package yehor.budget.web.converter;

import org.springframework.stereotype.Component;
import yehor.budget.entity.Expense;
import yehor.budget.web.dto.full.ExpenseFullDto;
import yehor.budget.web.dto.limited.ExpenseLimitedDto;

@Component
public class ExpenseConverter {

    public ExpenseFullDto convert(Expense expense) {
        return ExpenseFullDto.builder()
                .id(expense.getId())
                .value(expense.getValue())
                .date(expense.getDate())
                .isRegular(expense.getIsRegular())
                .categoryId(expense.getCategory().getId())
                .build();
    }

    public Expense convert(ExpenseLimitedDto expenseDto) {
        return Expense.builder()
                .value(expenseDto.getValue())
                .date(expenseDto.getDate())
                .isRegular(expenseDto.getIsRegular())
                .build();
    }

    public Expense convert(ExpenseFullDto expenseDto) {
        return Expense.builder()
                .id(expenseDto.getId())
                .value(expenseDto.getValue())
                .date(expenseDto.getDate())
                .isRegular(expenseDto.getIsRegular())
                .build();
    }
}