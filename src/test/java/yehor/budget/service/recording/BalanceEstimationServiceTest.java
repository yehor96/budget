package yehor.budget.service.recording;

import org.junit.jupiter.api.Test;
import yehor.budget.common.date.DateManager;
import yehor.budget.common.date.MonthWeek;
import yehor.budget.entity.recording.BalanceRecord;
import yehor.budget.repository.FutureExpenseRepository;
import yehor.budget.service.client.currency.CurrencyRateService;
import yehor.budget.web.dto.full.BalanceEstimateDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static common.factory.BalanceFactory.balanceRecordWithSetIncomes;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BalanceEstimationServiceTest {

    private final DateManager dateManager = mock(DateManager.class);
    private final CurrencyRateService currencyRateService = mock(CurrencyRateService.class);
    private final FutureExpenseRepository futureExpenseRepository = mock(FutureExpenseRepository.class);

    private final BalanceEstimationService balanceEstimationService =
            new BalanceEstimationService(dateManager, currencyRateService, futureExpenseRepository);

    @Test
    void testGetBalanceEstimation() {
        BigDecimal expectedPreviousTotal = new BigDecimal("200.00");
        LocalDate expectedLastDate1 = LocalDate.of(2023, 1, 31);
        LocalDate expectedLastDate2 = LocalDate.of(2023, 2, 28);
        LocalDate expectedLastDate3 = LocalDate.of(2023, 3, 31);
        List<BalanceEstimateDto> expectedBalanceEstimation = List.of(
                new BalanceEstimateDto( // first month estimate
                        expectedPreviousTotal,
                        new BigDecimal("379.58"),
                        new BigDecimal("20.00"),
                        expectedLastDate1),
                new BalanceEstimateDto( // second month estimate
                        new BigDecimal("-159.58"),
                        new BigDecimal("408.74"),
                        new BigDecimal("20.00"),
                        expectedLastDate2),
                new BalanceEstimateDto( // third month estimate
                        new BigDecimal("-548.32"),
                        new BigDecimal("408.74"),
                        new BigDecimal("20.00"),
                        expectedLastDate3)
        );

        BalanceRecord balanceRecord = balanceRecordWithSetIncomes();
        LocalDate date = LocalDate.of(2023, 1, 5);

        when(dateManager.getMonthEndDate(any()))
                .thenReturn(expectedLastDate1)
                .thenReturn(expectedLastDate2)
                .thenReturn(expectedLastDate3);
        when(currencyRateService.getValueInCurrency(any(), any()))
                .thenReturn(new BigDecimal("10.00"));

        List<BalanceEstimateDto> actualBalanceEstimation = balanceEstimationService.getBalanceEstimation(
                balanceRecord, date, expectedPreviousTotal);

        assertEquals(expectedBalanceEstimation, actualBalanceEstimation);
    }

    @Test
    void testEstimateForMonth() {
        BigDecimal expectedPreviousTotal = new BigDecimal("200.00");
        LocalDate expectedLastDate = LocalDate.of(2023, 1, 31);
        BalanceEstimateDto expectedEstimatedDto = new BalanceEstimateDto(
                expectedPreviousTotal,
                new BigDecimal("34.00"),
                new BigDecimal("20.00"),
                expectedLastDate);

        BalanceRecord balanceRecord = balanceRecordWithSetIncomes();
        LocalDate date = LocalDate.of(2023, 1, 5);
        Map<MonthWeek, BigDecimal> estimatedExpensePerWeek = Map.of(
                MonthWeek.DAYS_1_TO_7, new BigDecimal("14.00"),
                MonthWeek.DAYS_8_TO_14, new BigDecimal("10.00"),
                MonthWeek.DAYS_15_TO_21, new BigDecimal("10.00"),
                MonthWeek.DAYS_22_TO_31, new BigDecimal("10.00")
        );

        when(dateManager.getMonthEndDate(any())).thenReturn(expectedLastDate);
        when(currencyRateService.getValueInCurrency(any(), any()))
                .thenReturn(new BigDecimal("10.00"));

        BalanceEstimateDto actualEstimateDto = balanceEstimationService.estimateForMonth(
                date, expectedPreviousTotal, balanceRecord, estimatedExpensePerWeek);

        assertEquals(expectedEstimatedDto, actualEstimateDto);
    }

    @Test
    void testGetExpensesTilEndOfMonth() {
        BigDecimal expectedResult = new BigDecimal("34.00");
        LocalDate date = LocalDate.of(2023, 1, 5);
        Map<MonthWeek, BigDecimal> estimatedExpensePerWeek = Map.of(
                MonthWeek.DAYS_1_TO_7, new BigDecimal("14.00"),
                MonthWeek.DAYS_8_TO_14, new BigDecimal("10.00"),
                MonthWeek.DAYS_15_TO_21, new BigDecimal("10.00"),
                MonthWeek.DAYS_22_TO_31, new BigDecimal("10.00")
        );

        BigDecimal actualResult = balanceEstimationService.getExpensesTilEndOfMonth(date, estimatedExpensePerWeek);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testGetIncomesExpectedTilEndOfMonthWithAccrualDayAfterProvidedDate() {
        LocalDate date = LocalDate.of(2023, 1, 23);
        BalanceRecord balanceRecord = balanceRecordWithSetIncomes();

        when(currencyRateService.getValueInCurrency(any(), any()))
                .thenReturn(new BigDecimal("10.00"));

        BigDecimal actualResult = balanceEstimationService.getIncomesTilEndOfMonth(date, balanceRecord);

        assertEquals(new BigDecimal("10.00"), actualResult);
        verify(currencyRateService, times(1)).getValueInCurrency(any(), any());
    }

    @Test
    void testGetSumOfThreeWeeksLeftInMonthAfterCurrentWeek() {
        BigDecimal expectedResult = new BigDecimal("30.00");
        MonthWeek currentMonthWeek = MonthWeek.DAYS_1_TO_7;
        Map<MonthWeek, BigDecimal> estimatedExpensePerWeek = Map.of(
                MonthWeek.DAYS_8_TO_14, new BigDecimal("10.00"),
                MonthWeek.DAYS_15_TO_21, new BigDecimal("10.00"),
                MonthWeek.DAYS_22_TO_31, new BigDecimal("10.00")
        );

        BigDecimal actualResult = balanceEstimationService.getExpensesForFullWeeksLeftTilEndOfMonth(
                currentMonthWeek, estimatedExpensePerWeek);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testGetZeroWhenNoFullWeeksLeftInMonthAfterCurrentWeek() {
        BigDecimal expectedResult = BigDecimal.ZERO;
        MonthWeek currentMonthWeek = MonthWeek.DAYS_22_TO_31;
        Map<MonthWeek, BigDecimal> estimatedExpensePerWeek = Map.of(
                MonthWeek.DAYS_22_TO_31, new BigDecimal("10.00")
        );

        BigDecimal actualResult = balanceEstimationService.getExpensesForFullWeeksLeftTilEndOfMonth(
                currentMonthWeek, estimatedExpensePerWeek);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testGetZeroWhenThereAreNoMoreDaysLeftInWeek() {
        BigDecimal expectedResult = new BigDecimal("00.00");
        MonthWeek currentMonthWeek = MonthWeek.DAYS_22_TO_31;
        LocalDate currentDate = LocalDate.of(2022, 12, 31);
        Map<MonthWeek, BigDecimal> estimatedExpensePerWeek = Map.of(
                MonthWeek.DAYS_22_TO_31, new BigDecimal("10.00")
        );

        when(dateManager.getLastDayOfMonth(currentDate)).thenReturn(31);

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

        when(dateManager.getLastDayOfMonth(currentDate)).thenReturn(28);

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
