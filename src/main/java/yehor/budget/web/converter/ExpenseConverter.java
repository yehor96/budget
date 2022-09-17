package yehor.budget.web.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import yehor.budget.entity.Expense;
import yehor.budget.entity.Tag;
import yehor.budget.repository.CategoryRepository;
import yehor.budget.repository.TagRepository;
import yehor.budget.web.dto.full.ExpenseFullDto;
import yehor.budget.web.dto.limited.ExpenseLimitedDto;

import static java.util.stream.Collectors.toSet;

@Component
@RequiredArgsConstructor
public class ExpenseConverter {

    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    public ExpenseFullDto convert(Expense expense) {
        return ExpenseFullDto.builder()
                .id(expense.getId())
                .value(expense.getValue())
                .date(expense.getDate())
                .isRegular(expense.getIsRegular())
                .categoryId(expense.getCategory().getId())
                .tagIds(expense.getTags().stream().map(Tag::getId).collect(toSet()))
                .note(expense.getNote())
                .build();
    }

    public Expense convert(ExpenseLimitedDto expenseDto) {
        return Expense.builder()
                .value(expenseDto.getValue())
                .date(expenseDto.getDate())
                .isRegular(expenseDto.getIsRegular())
                .category(categoryRepository.getById(expenseDto.getCategoryId()))
                .tags(expenseDto.getTagIds().stream().map(tagRepository::getById).collect(toSet()))
                .note(expenseDto.getNote())
                .build();
    }

    public Expense convert(ExpenseFullDto expenseDto) {
        return Expense.builder()
                .id(expenseDto.getId())
                .value(expenseDto.getValue())
                .date(expenseDto.getDate())
                .isRegular(expenseDto.getIsRegular())
                .category(categoryRepository.getById(expenseDto.getCategoryId()))
                .tags(expenseDto.getTagIds().stream().map(tagRepository::getById).collect(toSet()))
                .note(expenseDto.getNote())
                .build();
    }
}