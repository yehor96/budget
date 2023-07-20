package common.factory;

import lombok.experimental.UtilityClass;
import yehor.budget.entity.Expense;
import yehor.budget.web.dto.ExpensesByTagDto;
import yehor.budget.web.dto.full.ExpenseFullDto;
import yehor.budget.web.dto.limited.ExpenseLimitedDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static common.factory.CategoryFactory.DEFAULT_CATEGORY_ID;
import static common.factory.CategoryFactory.defaultCategory;
import static common.factory.CategoryFactory.defaultCategoryFullDto;
import static common.factory.CategoryFactory.secondCategory;
import static common.factory.TagFactory.DEFAULT_TAG_ID;
import static common.factory.TagFactory.defaultTag;
import static common.factory.TagFactory.defaultTagFullDto;
import static common.factory.TagFactory.secondTag;
import static common.factory.TagFactory.secondTagFullDto;
import static java.util.Collections.emptyList;

@UtilityClass
public class ExpenseFactory {

    public static final long DEFAULT_EXPENSE_ID = 1L;

    public static ExpenseFullDto defaultExpenseFullDto() {
        return ExpenseFullDto.builder()
                .id(DEFAULT_EXPENSE_ID)
                .value(new BigDecimal("10.00"))
                .date(LocalDate.now())
                .isRegular(true)
                .category(defaultCategoryFullDto())
                .tags(Collections.singleton(defaultTagFullDto()))
                .note("Some information")
                .build();
    }

    public static Expense defaultExpense() {
        return Expense.builder()
                .id(DEFAULT_EXPENSE_ID)
                .value(new BigDecimal("10.00"))
                .date(LocalDate.now())
                .isRegular(true)
                .category(defaultCategory())
                .tags(Collections.singleton(defaultTag()))
                .note("Some information")
                .build();
    }

    public static ExpenseLimitedDto defaultExpenseLimitedDto() {
        return ExpenseLimitedDto.builder()
                .value(new BigDecimal("10.00"))
                .date(LocalDate.now())
                .isRegular(true)
                .categoryId(DEFAULT_CATEGORY_ID)
                .tagIds(Collections.singleton(DEFAULT_TAG_ID))
                .note("Some information")
                .build();
    }

    public static ExpenseFullDto secondExpenseFullDto() {
        return ExpenseFullDto.builder()
                .id(2L)
                .value(new BigDecimal("100.00"))
                .date(LocalDate.now().minusDays(1))
                .isRegular(false)
                .category(defaultCategoryFullDto())
                .tags(Collections.singleton(secondTagFullDto()))
                .build();
    }

    public static Expense secondExpense() {
        return Expense.builder()
                .id(2L)
                .value(new BigDecimal("100.00"))
                .date(LocalDate.now().minusDays(1))
                .isRegular(false)
                .category(defaultCategory())
                .tags(Collections.singleton(secondTag()))
                .build();
    }

    public static ExpenseFullDto thirdExpenseFullDto() {
        return ExpenseFullDto.builder()
                .id(3L)
                .value(new BigDecimal("15.50"))
                .date(LocalDate.now().minusDays(2))
                .isRegular(false)
                .category(defaultCategoryFullDto())
                .tags(Collections.singleton(defaultTagFullDto()))
                .build();
    }

    public static Expense thirdExpense() {
        return Expense.builder()
                .id(3L)
                .value(new BigDecimal("15.50"))
                .date(LocalDate.now().minusDays(2))
                .isRegular(false)
                .category(defaultCategory())
                .tags(Collections.singleton(defaultTag()))
                .build();
    }

    public static Expense expenseWithAnotherCategory() {
        return Expense.builder()
                .id(4L)
                .value(new BigDecimal("11.00"))
                .date(LocalDate.now())
                .isRegular(true)
                .category(secondCategory())
                .build();
    }

    public static List<ExpenseFullDto> defaultExpenseFullDtoList() {
        ExpenseFullDto expenseFullDto1 = defaultExpenseFullDto();
        ExpenseFullDto expenseFullDto2 = secondExpenseFullDto();
        ExpenseFullDto expenseFullDto3 = thirdExpenseFullDto();
        return List.of(expenseFullDto1, expenseFullDto2, expenseFullDto3);
    }

    public static List<Expense> defaultExpenseList() {
        Expense expense1 = defaultExpense();
        Expense expense2 = secondExpense();
        Expense expense3 = thirdExpense();
        return List.of(expense1, expense2, expense3);
    }

    public static List<Expense> multipleCategoriesExpenseList() {
        Expense expense1 = defaultExpense();
        Expense expense2 = secondExpense();
        Expense expense3 = thirdExpense();
        Expense expense4 = expenseWithAnotherCategory();
        return List.of(expense1, expense2, expense3, expense4);
    }

    public static ExpensesByTagDto defaultExpenseByTagDto() {
        return ExpensesByTagDto.builder()
                .total(new BigDecimal("25.50"))
                .expenses(List.of(defaultExpenseFullDto(), thirdExpenseFullDto()))
                .build();
    }

    public static ExpensesByTagDto emptyExpenseByTagDto() {
        return ExpensesByTagDto.builder()
                .total(BigDecimal.ZERO)
                .expenses(emptyList())
                .build();
    }

}
