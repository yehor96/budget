package yehor.budget.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yehor.budget.entity.DailyExpense;
import yehor.budget.exception.ExpenseExceptionProvider;
import yehor.budget.manager.date.DateManager;
import yehor.budget.repository.ExpenseRepository;
import yehor.budget.web.converter.ExpenseConverter;
import yehor.budget.web.dto.DailyExpenseDto;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseConverter expenseConverter;
    private final ExpenseRepository expenseRepository;

    public DailyExpenseDto findByDate(LocalDate date) {
        DailyExpense expense = expenseRepository.findByDate(date)
                .orElseThrow(() -> ExpenseExceptionProvider.getNoExpenseForDateException(date));
        return expenseConverter.convertToDto(expense);
    }

    public int findSumInInterval(LocalDate dateFrom, LocalDate dateTo) {
        return expenseRepository.findSumInInterval(dateFrom, dateTo);
    }

    public List<DailyExpenseDto> findAllInInterval(LocalDate dateFrom, LocalDate dateTo) {
        List<DailyExpense> expenses = expenseRepository.findAllInInterval(dateFrom, dateTo);
        return expenses.stream()
                .map(expenseConverter::convertToDto)
                .toList();
    }

    @Transactional
    public void save(DailyExpenseDto dailyExpenseDto) {
        DailyExpense expense = expenseConverter.convertToEntity(dailyExpenseDto);
        validateExpenseDoNotExist(expense);
        expenseRepository.save(expense);
        DateManager.updateEndDateIfNecessary(expense.getDate());
    }

    @Transactional
    public void updateByDate(DailyExpenseDto dailyExpenseDto) {
        DailyExpense dailyExpense = expenseConverter.convertToEntity(dailyExpenseDto);
        validateExpenseExists(dailyExpense);
        expenseRepository.updateByDate(dailyExpense);
    }

    private void validateExpenseDoNotExist(DailyExpense dailyExpense) {
        expenseRepository.findByDate(dailyExpense.getDate())
                .ifPresent(e -> {
                    throw ExpenseExceptionProvider.getExpenseInDateAlreadyExistsException(dailyExpense.getDate());
                });
    }

    private void validateExpenseExists(DailyExpense dailyExpense) {
        expenseRepository.findByDate(dailyExpense.getDate())
                .orElseThrow(() -> ExpenseExceptionProvider.getNoExpenseForDateException(dailyExpense.getDate()));
    }
}
