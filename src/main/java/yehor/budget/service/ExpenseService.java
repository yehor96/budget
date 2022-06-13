package yehor.budget.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yehor.budget.entity.Expense;
import yehor.budget.exception.ExpenseExceptionProvider;
import yehor.budget.manager.date.DateManager;
import yehor.budget.repository.ExpenseRepository;
import yehor.budget.web.converter.ExpenseConverter;
import yehor.budget.web.dto.ExpenseDto;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseConverter expenseConverter;
    private final ExpenseRepository expenseRepository;

    public int findSumInInterval(LocalDate dateFrom, LocalDate dateTo) {
        return expenseRepository.findSumInInterval(dateFrom, dateTo);
    }

    public List<ExpenseDto> findAllInInterval(LocalDate dateFrom, LocalDate dateTo) {
        List<Expense> expenses = expenseRepository.findAllInInterval(dateFrom, dateTo);
        return expenses.stream()
                .map(expenseConverter::convert)
                .toList();
    }

    public void save(ExpenseDto expenseDto) {
        if (expenseRepository.existsById(expenseDto.getId())) {
            throw ExpenseExceptionProvider.getExpenseWithIdExistsException(expenseDto.getId());
        }
        Expense expense = expenseConverter.convert(expenseDto);
        expenseRepository.save(expense);
        DateManager.updateEndDateIfNecessary(expense.getDate());
    }

    public ExpenseDto findById(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> ExpenseExceptionProvider.getExpenseWithIdDoesNotExistException(id));
        return expenseConverter.convert(expense);
    }

    @Transactional
    public void updateById(Long id, ExpenseDto expenseDto) {
        if (!expenseRepository.existsById(id)) {
            throw ExpenseExceptionProvider.getExpenseWithIdDoesNotExistException(id);
        }
        Expense expense = expenseConverter.convert(expenseDto);
        expense.setId(id);
        expenseRepository.updateById(expense);
    }
}
