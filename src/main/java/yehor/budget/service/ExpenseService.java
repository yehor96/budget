package yehor.budget.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yehor.budget.entity.Category;
import yehor.budget.entity.Expense;
import yehor.budget.manager.date.DateManager;
import yehor.budget.repository.CategoryRepository;
import yehor.budget.repository.ExpenseRepository;
import yehor.budget.web.converter.ExpenseConverter;
import yehor.budget.web.dto.ExpenseDto;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static yehor.budget.exception.CategoryExceptionProvider.categoryDoesNotExistException;
import static yehor.budget.exception.ExpenseExceptionProvider.expenseWithIdDoesNotExistException;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseConverter expenseConverter;
    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    public BigDecimal findSumInInterval(LocalDate dateFrom, LocalDate dateTo) {
        return expenseRepository.findSumInInterval(dateFrom, dateTo);
    }

    public List<ExpenseDto> findAllInInterval(LocalDate dateFrom, LocalDate dateTo) {
        List<Expense> expenses = expenseRepository.findAllInInterval(dateFrom, dateTo);
        return expenses.stream()
                .map(expenseConverter::convert)
                .toList();
    }

    public void save(ExpenseDto expenseDto) {
        Category category = findCategoryById(expenseDto.getCategoryId());
        Expense expense = expenseConverter.convert(expenseDto);
        expense.setCategory(category);
        expenseRepository.save(expense);
        DateManager.updateEndDateIfNecessary(expense.getDate());
    }

    public ExpenseDto findById(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> expenseWithIdDoesNotExistException(id));
        Category category = findCategoryById(expense.getCategory().getId());
        ExpenseDto expenseDto = expenseConverter.convert(expense);
        expenseDto.setCategoryId(category.getId());
        return expenseDto;
    }

    @Transactional
    public void updateById(Long id, ExpenseDto expenseDto) {
        validateExists(id);
        Category category = findCategoryById(expenseDto.getCategoryId());
        Expense expense = expenseConverter.convert(expenseDto);
        expense.setId(id);
        expense.setCategory(category);
        expenseRepository.updateById(expense);
        DateManager.updateEndDateIfNecessary(expense.getDate());
    }

    public void deleteById(Long id) {
        validateExists(id);
        expenseRepository.deleteById(id);
    }

    private void validateExists(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw expenseWithIdDoesNotExistException(id);
        }
    }

    private Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> categoryDoesNotExistException(id));
    }
}
