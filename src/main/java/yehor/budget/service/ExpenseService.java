package yehor.budget.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import yehor.budget.common.date.DateManager;
import yehor.budget.entity.Category;
import yehor.budget.entity.Expense;
import yehor.budget.repository.CategoryRepository;
import yehor.budget.repository.ExpenseRepository;
import yehor.budget.web.converter.ExpenseConverter;
import yehor.budget.web.dto.full.ExpenseFullDto;
import yehor.budget.web.dto.limited.ExpenseLimitedDto;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static yehor.budget.web.exception.CategoryExceptionProvider.categoryDoesNotExistException;
import static yehor.budget.web.exception.ExpenseExceptionProvider.expenseWithIdDoesNotExistException;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private static final Logger LOG = LogManager.getLogger(ExpenseService.class);

    private final ExpenseConverter expenseConverter;
    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    public BigDecimal findSumInInterval(LocalDate dateFrom, LocalDate dateTo) {
        return expenseRepository.findSumInInterval(dateFrom, dateTo);
    }

    public List<ExpenseFullDto> findAllInInterval(LocalDate dateFrom, LocalDate dateTo) {
        List<Expense> expenses = expenseRepository.findAllInInterval(dateFrom, dateTo);
        return expenses.stream()
                .map(expenseConverter::convert)
                .toList();
    }

    public void save(ExpenseLimitedDto expenseDto) {
        Expense expense = expenseConverter.convert(expenseDto);

        Category category = findCategoryById(expenseDto.getCategoryId());
        expense.setCategory(category);

        expenseRepository.save(expense);
        LOG.info("{} is saved", expense);
        DateManager.updateEndDateIfNecessary(expense.getDate());
    }

    public ExpenseFullDto findById(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> expenseWithIdDoesNotExistException(id));
        return expenseConverter.convert(expense);
    }

    @Transactional
    public void updateById(ExpenseFullDto expenseDto) {
        validateExists(expenseDto.getId());
        Expense expense = expenseConverter.convert(expenseDto);

        Category category = findCategoryById(expenseDto.getCategoryId());
        expense.setCategory(category);

        expenseRepository.updateById(expense);
        LOG.info("{} is updated", expense);
        DateManager.updateEndDateIfNecessary(expense.getDate());
    }

    public void deleteById(Long id) {
        validateExists(id);
        expenseRepository.deleteById(id);
        LOG.info("Expense with id {} is deleted", id);
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
