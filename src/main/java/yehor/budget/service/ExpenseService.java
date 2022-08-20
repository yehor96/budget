package yehor.budget.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import yehor.budget.common.date.DateManager;
import yehor.budget.common.exception.ObjectNotFoundException;
import yehor.budget.entity.Category;
import yehor.budget.entity.Expense;
import yehor.budget.entity.Tag;
import yehor.budget.repository.CategoryRepository;
import yehor.budget.repository.ExpenseRepository;
import yehor.budget.repository.TagRepository;
import yehor.budget.web.converter.ExpenseConverter;
import yehor.budget.web.dto.full.ExpenseFullDto;
import yehor.budget.web.dto.limited.ExpenseLimitedDto;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private static final Logger LOG = LogManager.getLogger(ExpenseService.class);

    private final ExpenseConverter expenseConverter;
    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final DateManager dateManager;

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

        fillTags(expense, expenseDto.getTagIds());

        expenseRepository.save(expense);
        LOG.info("{} is saved", expense);
        dateManager.updateBudgetDatesIfNecessary(expense.getDate());
    }

    public ExpenseFullDto getById(Long id) {
        Expense expense = expenseRepository.getById(id);
        return expenseConverter.convert(expense);
    }

    @Transactional
    public void update(ExpenseFullDto expenseDto) {
        validateExists(expenseDto.getId());
        Expense expense = expenseConverter.convert(expenseDto);

        Category category = findCategoryById(expenseDto.getCategoryId());
        expense.setCategory(category);

        fillTags(expense, expenseDto.getTagIds());

        expenseRepository.save(expense);
        LOG.info("{} is updated", expense);
        dateManager.updateBudgetDatesIfNecessary(expense.getDate());
    }

    public void deleteById(Long id) {
        validateExists(id);
        expenseRepository.deleteById(id);
        LOG.info("Expense with id {} is deleted", id);
    }

    private void validateExists(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new ObjectNotFoundException(String.format("Expense with id %s does not exist", id));
        }
    }

    private Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Category with id %s does not exist", id)));
    }

    private Tag findTagById(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Tag with id %s does not exist", id)));
    }

    private void fillTags(Expense expense, Set<Long> tagIds) {
        Set<Tag> tags = tagIds.stream()
                .map(this::findTagById)
                .collect(Collectors.toSet());
        expense.setTags(tags);
    }
}
