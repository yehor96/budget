package integration;

import org.junit.jupiter.api.Test;
import yehor.budget.web.dto.full.ExpenseFullDto;
import yehor.budget.web.dto.limited.CategoryLimitedDto;
import yehor.budget.web.dto.limited.ExpenseLimitedDto;

import static common.provider.CategoryProvider.defaultCategoryLimitedDto;
import static common.provider.ExpenseProvider.defaultExpenseFullDto;
import static common.provider.ExpenseProvider.defaultExpenseLimitedDto;
import static common.client.MvcClient.parameter;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ExpenseControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    void testPostAndGetExpenseById() {
        ExpenseFullDto expectedExpense = defaultExpenseFullDto();
        ExpenseLimitedDto expenseLimDto = defaultExpenseLimitedDto();
        CategoryLimitedDto categoryLimitedDto = defaultCategoryLimitedDto();

        mvcClient.makePost(CATEGORIES_URL, categoryLimitedDto);
        mvcClient.makePost(EXPENSES_URL, expenseLimDto);

        ExpenseFullDto actualExpense = mvcClient.makeGet(EXPENSES_URL, parameter("id", "1"), ExpenseFullDto.class);

        assertEquals(expectedExpense, actualExpense);
    }
}
