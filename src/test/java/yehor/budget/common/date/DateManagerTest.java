package yehor.budget.common.date;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import yehor.budget.web.exception.CustomResponseStatusException;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DateManagerTest {

    private final DateManager dateManager = new DateManager();

    @Test
    void testGetMonthsListIn() {
        FullMonth startMonth = FullMonth.of(Month.JANUARY, 2022);
        FullMonth month1 = FullMonth.of(Month.FEBRUARY, 2022);
        FullMonth month2 = FullMonth.of(Month.MARCH, 2022);
        FullMonth month3 = FullMonth.of(Month.APRIL, 2022);
        FullMonth endMonth = FullMonth.of(Month.MAY, 2022);

        List<FullMonth> expectedMonths = List.of(startMonth, month1, month2, month3, endMonth);

        List<FullMonth> actualMonths = dateManager.getMonthsListIn(startMonth, endMonth);

        assertEquals(expectedMonths, actualMonths);
    }

    @Test
    void testGetMonthsListInTheSameMonth() {
        FullMonth month = FullMonth.of(Month.JANUARY, 2022);

        List<FullMonth> expectedMonths = List.of(month);

        List<FullMonth> actualMonths = dateManager.getMonthsListIn(month, month);

        assertEquals(expectedMonths, actualMonths);
    }

    @Test
    void testValidateMonthWithinBudgetSuccess() {
        LocalDate now = LocalDate.now();
        FullMonth month = FullMonth.of(now.getMonth(), now.getYear());

        dateManager.validateMonthWithinBudget(month);
    }

    @Test
    void testValidateMonthWithinBudgetFailure() {
        LocalDate date = DateManager.START_DATE.minusMonths(1);
        FullMonth month = FullMonth.of(date.getMonth(), date.getYear());

        try {
            dateManager.validateMonthWithinBudget(month);
        } catch (Exception e) {
            assertEquals(CustomResponseStatusException.class, e.getClass());
            CustomResponseStatusException exception = (CustomResponseStatusException) e;
            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
            assertTrue(Objects.requireNonNull(exception.getReason()).contains("Provided value is invalid " + month));
        }
    }

    @Test
    void testValidateMonthsInSequentialOrderSuccess() {
        FullMonth startMonth = FullMonth.of(Month.JANUARY, 2022);
        FullMonth endMonth = FullMonth.of(Month.MAY, 2022);

        dateManager.validateMonthsInSequentialOrder(startMonth, endMonth);
    }

    @Test
    void testValidateMonthsInSequentialOrderFailure() {
        FullMonth startMonth = FullMonth.of(Month.MAY, 2022);
        FullMonth endMonth = FullMonth.of(Month.JANUARY, 2022);

        try {
            dateManager.validateMonthsInSequentialOrder(startMonth, endMonth);
        } catch (Exception e) {
            assertEquals(CustomResponseStatusException.class, e.getClass());
            CustomResponseStatusException exception = (CustomResponseStatusException) e;
            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
            assertEquals("Reversed order of provided months: " + startMonth + " and " + endMonth, exception.getReason());
        }
    }
}