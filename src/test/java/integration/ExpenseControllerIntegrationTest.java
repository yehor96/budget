package integration;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import yehor.budget.web.dto.full.ExpenseFullDto;

import java.time.LocalDate;
import java.util.List;

import static common.provider.ExpenseProvider.defaultExpenseFullDto;
import static common.provider.ExpenseProvider.defaultExpenseFullDtoList;
import static common.provider.ExpenseProvider.updatedExpenseFullDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ExpenseControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    @Order(1)
    void testGetExpenseById() {
        ExpenseFullDto expectedExpense = defaultExpenseFullDto();

        ExpenseFullDto actualExpense = getExpenseById("1");

        assertEquals(expectedExpense, actualExpense);
    }

    @Test
    @Order(1)
    void testGetExpensesInIntervalExpense() {
        List<ExpenseFullDto> expectedExpenses = defaultExpenseFullDtoList();

        LocalDate dateFrom = LocalDate.now().minusDays(2);
        LocalDate dateTo = LocalDate.now();

        List<ExpenseFullDto> expensesInInterval = getExpensesInInterval(dateFrom, dateTo);

        assertThat(expensesInInterval).containsAll(expectedExpenses);
    }

    @Test
    @Order(2)
    void testUpdateExpense() {
        ExpenseFullDto newInformationExpense = defaultExpenseFullDto();
        newInformationExpense.setId(4L);
        ExpenseFullDto expenseToBeUpdated = updatedExpenseFullDto();

        ExpenseFullDto actualUpdatedExpense = getExpenseById("4");
        assertEquals(expenseToBeUpdated, actualUpdatedExpense);

        updateExpense(newInformationExpense);

        actualUpdatedExpense = getExpenseById("4");
        assertEquals(newInformationExpense, actualUpdatedExpense);
    }

}
