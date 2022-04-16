package yehor.budget.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yehor.budget.entity.DailyExpense;
import yehor.budget.repository.ExpenseRepository;
import yehor.budget.web.converter.ExpenseConverter;
import yehor.budget.web.dto.DailyExpenseDto;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private static final ExpenseConverter EXPENSE_CONVERTER = new ExpenseConverter();

    private final ExpenseRepository expenseRepository;

    public DailyExpenseDto findByDate(LocalDate date) {
        DailyExpense expense = expenseRepository.findOne(date).orElseThrow();
        return EXPENSE_CONVERTER.convertToDto(expense);
    }

    public int findSumInInterval(LocalDate dateFrom, LocalDate dateTo) {
        return expenseRepository.findSumInInterval(dateFrom, dateTo);
    }

    public List<DailyExpenseDto> findAllInInterval(LocalDate dateFrom, LocalDate dateTo) {
        List<DailyExpense> expenses = expenseRepository.findAllInInterval(dateFrom, dateTo);
        return expenses.stream()
                .map(EXPENSE_CONVERTER::convertToDto)
                .toList();
    }
}
