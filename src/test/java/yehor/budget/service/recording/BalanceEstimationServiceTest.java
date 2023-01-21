package yehor.budget.service.recording;

import org.junit.jupiter.api.Test;
import yehor.budget.common.date.DateManager;
import yehor.budget.common.date.MonthWeek;
import yehor.budget.service.client.currency.CurrencyRateService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BalanceEstimationServiceTest {

    private final DateManager dateManager = mock(DateManager.class);
    private final CurrencyRateService currencyRateService = mock(CurrencyRateService.class);

    private final BalanceEstimationService balanceEstimationService =
            new BalanceEstimationService(dateManager, currencyRateService);


//    @Test
//    void testGetSumOfThreeWeeksLeftInMonthAfterCurrentWeek() {
//        BigDecimal expectedResult = new BigDecimal("30.00");
//        MonthWeek currentMonthWeek = MonthWeek.DAYS_1_TO_7;
//        Map<MonthWeek, BigDecimal> estimatedExpensePerWeek = Map.of(
//                MonthWeek.DAYS_8_TO_14, new BigDecimal("10.00"),
//                MonthWeek.DAYS_15_TO_21, new BigDecimal("10.00"),
//                MonthWeek.DAYS_22_TO_31, new BigDecimal("10.00")
//        );
//
//        BigDecimal actualResult = balanceEstimationService.getExpensesForFullWeeksLeftInCurrentMonth(
//                currentMonthWeek, estimatedExpensePerWeek);
//
//        assertEquals(expectedResult, actualResult);
//    }
//
//    @Test
//    void testGetZeroWhenNoFullWeeksLeftInMonthAfterCurrentWeek() {
//        BigDecimal expectedResult = BigDecimal.ZERO;
//        MonthWeek currentMonthWeek = MonthWeek.DAYS_22_TO_31;
//        Map<MonthWeek, BigDecimal> estimatedExpensePerWeek = Map.of(
//                MonthWeek.DAYS_22_TO_31, new BigDecimal("10.00")
//        );
//
//        BigDecimal actualResult = balanceEstimationService.getExpensesForFullWeeksLeftInCurrentMonth(
//                currentMonthWeek, estimatedExpensePerWeek);
//
//        assertEquals(expectedResult, actualResult);
//    }

    @Test
    void testGetZeroWhenThereAreNoMoreDaysLeftInWeek() {
        BigDecimal expectedResult = new BigDecimal("00.00");
        MonthWeek currentMonthWeek = MonthWeek.DAYS_22_TO_31;
        LocalDate currentDate = LocalDate.of(2022, 12, 31);
        Map<MonthWeek, BigDecimal> estimatedExpensePerWeek = Map.of(
                MonthWeek.DAYS_22_TO_31, new BigDecimal("10.00")
        );

        when(dateManager.getLastDayOfMonthByDate(currentDate)).thenReturn(31);

        BigDecimal actualResult = balanceEstimationService.getExpensesForDaysLeftInCurrentWeek(
                currentMonthWeek, currentDate, estimatedExpensePerWeek);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testGetValueWhenLastDayOfMonthIs28th() {
        BigDecimal expectedResult = new BigDecimal("40.00");
        MonthWeek currentMonthWeek = MonthWeek.DAYS_22_TO_31;
        LocalDate currentDate = LocalDate.of(2022, 2, 24);
        Map<MonthWeek, BigDecimal> estimatedExpensePerWeek = Map.of(
                MonthWeek.DAYS_22_TO_31, new BigDecimal("70.00")
        );

        when(dateManager.getLastDayOfMonthByDate(currentDate)).thenReturn(28);

        BigDecimal actualResult = balanceEstimationService.getExpensesForDaysLeftInCurrentWeek(
                currentMonthWeek, currentDate, estimatedExpensePerWeek);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testGetValueWhenWeekInMiddleOfMonthAndMaxDaysLeftInCurrentWeek() {
        BigDecimal expectedResult = new BigDecimal("120.00");
        MonthWeek currentMonthWeek = MonthWeek.DAYS_8_TO_14;
        LocalDate currentDate = LocalDate.of(2022, 2, 8);
        Map<MonthWeek, BigDecimal> estimatedExpensePerWeek = Map.of(
                MonthWeek.DAYS_8_TO_14, new BigDecimal("140.00")
        );

        BigDecimal actualResult = balanceEstimationService.getExpensesForDaysLeftInCurrentWeek(
                currentMonthWeek, currentDate, estimatedExpensePerWeek);

        assertEquals(expectedResult, actualResult);
    }
}
