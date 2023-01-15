package yehor.budget.service;

import org.junit.jupiter.api.Test;
import yehor.budget.common.date.DateManager;
import yehor.budget.common.date.MonthWeek;
import yehor.budget.common.util.PageableHelper;
import yehor.budget.entity.Actor;
import yehor.budget.entity.BalanceItem;
import yehor.budget.entity.BalanceRecord;
import yehor.budget.repository.ActorRepository;
import yehor.budget.repository.BalanceItemRepository;
import yehor.budget.repository.BalanceRecordRepository;
import yehor.budget.service.client.currency.CurrencyRateService;
import yehor.budget.web.converter.BalanceConverter;
import yehor.budget.web.dto.full.BalanceEstimateDto;
import yehor.budget.web.dto.full.BalanceRecordFullDto;
import yehor.budget.web.dto.limited.BalanceRecordLimitedDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static common.factory.BalanceFactory.DEFAULT_BALANCE_RECORD_TOTAL;
import static common.factory.BalanceFactory.balanceRecordWithNotSetExpensesAndIncome;
import static common.factory.BalanceFactory.defaultBalanceRecord;
import static common.factory.BalanceFactory.defaultBalanceRecordFullDto;
import static common.factory.BalanceFactory.defaultBalanceRecordLimitedDto;
import static common.factory.EstimatedExpenseFactory.defaultEstimatedExpenseFullDto;
import static common.factory.IncomeSourceFactory.defaultTotalIncomeDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BalanceServiceTest {

    private final BalanceItemRepository balanceItemRepository = mock(BalanceItemRepository.class);
    private final BalanceRecordRepository balanceRecordRepository = mock(BalanceRecordRepository.class);
    private final BalanceConverter balanceConverter = mock(BalanceConverter.class);
    private final ActorRepository actorRepository = mock(ActorRepository.class);
    private final IncomeSourceService incomeSourceService = mock(IncomeSourceService.class);
    private final EstimatedExpenseService estimatedExpenseService = mock(EstimatedExpenseService.class);
    private final DateManager dateManager = mock(DateManager.class);
    private final PageableHelper pageableHelper = mock(PageableHelper.class);
    private final CurrencyRateService currencyRateService = mock(CurrencyRateService.class);

    private final BalanceService balanceService = new BalanceService(
            balanceItemRepository,
            balanceRecordRepository,
            balanceConverter,
            actorRepository,
            incomeSourceService,
            estimatedExpenseService,
            dateManager,
            pageableHelper,
            currencyRateService
    );

    @Test
    void testGetLatestReturnsEmptyOptionalWhenThereAreNoRecords() {
        when(pageableHelper.getLatestByDate(any())).thenReturn(Optional.empty());

        Optional<BalanceRecordFullDto> result = balanceService.getLatest();

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetLatestReturnsOptionalWithValueWhenThereAreRecords() {
        BalanceRecordFullDto balanceRecordFullDto = defaultBalanceRecordFullDto();
        BalanceRecord balanceRecord = defaultBalanceRecord();
        LocalDate expectedDateEOM = LocalDate.of(2010, 1, 31);

        when(pageableHelper.getLatestByDate(any())).thenReturn(Optional.of(balanceRecord));
        when(balanceConverter.convert(balanceRecord)).thenReturn(balanceRecordFullDto);
        when(dateManager.getMonthEndDate(LocalDate.now())).thenReturn(expectedDateEOM);

        Optional<BalanceRecordFullDto> optActualBalanceRecordDto = balanceService.getLatest();

        assertTrue(optActualBalanceRecordDto.isPresent());
        BalanceRecordFullDto actualRecordDto = optActualBalanceRecordDto.get();
        assertNotNull(actualRecordDto.getTotalBalance());
        assertEquals(DEFAULT_BALANCE_RECORD_TOTAL, actualRecordDto.getTotalBalance());

        BalanceEstimateDto balanceEstimateDto = actualRecordDto.getBalanceEstimateDto();
        assertNotNull(balanceEstimateDto);
        assertNotNull(balanceEstimateDto.getExpenseByEOM());
        assertEquals(balanceRecord.getTotalIncome(), balanceEstimateDto.getIncomeByEOM());
        BigDecimal profit = balanceEstimateDto.getIncomeByEOM()
                .add(balanceEstimateDto.getPreviousMonthTotal())
                .subtract(balanceEstimateDto.getExpenseByEOM());
        assertEquals(profit, balanceEstimateDto.getProfitByEOM());
        assertEquals(expectedDateEOM, balanceEstimateDto.getEndOfMonthDate());
        assertFalse(actualRecordDto.getBalanceItems().isEmpty());
    }

    @Test
    void testSaveSuccessfullyWhileSettingExpensesAndIncomeWithAllSourcesIncludedIntoTotalIncome() {
        BalanceRecordLimitedDto recordLimitedDto = defaultBalanceRecordLimitedDto();
        BalanceRecord balanceRecord = balanceRecordWithNotSetExpensesAndIncome();
        balanceRecord.setDate(LocalDate.of(2022, 10, 10));

        when(actorRepository.existsById(any())).thenReturn(true);
        when(balanceConverter.convert(any(BalanceRecordLimitedDto.class))).thenReturn(balanceRecord);
        when(estimatedExpenseService.getOne()).thenReturn(defaultEstimatedExpenseFullDto());
        when(incomeSourceService.getTotalIncome()).thenReturn(defaultTotalIncomeDto());
        when(incomeSourceService.getTotalIncome()).thenReturn(defaultTotalIncomeDto());
        when(currencyRateService.getValueInCurrency(any(), any()))
                .thenReturn(new BigDecimal("50.00"));

        balanceService.save(recordLimitedDto);

        assertNotNull(balanceRecord.getTotalIncome());
        assertNotNull(balanceRecord.getTotal1to7());
        assertNotNull(balanceRecord.getTotal8to14());
        assertNotNull(balanceRecord.getTotal15to21());
        assertNotNull(balanceRecord.getTotal22to31());
        assertEquals(new BigDecimal("100.00"), balanceRecord.getTotalIncome());
        verify(balanceRecordRepository, times(1)).save(balanceRecord);
        verify(balanceItemRepository, times(2)).save(any(BalanceItem.class));
    }

    @Test
    void testSaveSuccessfullyWhileSettingExpensesAndIncomeWithOnlySourcesAfterBalanceRecordDateIncludedIntoTotalIncome() {
        BalanceRecordLimitedDto recordLimitedDto = defaultBalanceRecordLimitedDto();
        BalanceRecord balanceRecord = balanceRecordWithNotSetExpensesAndIncome();
        balanceRecord.setDate(LocalDate.of(2022, 10, 23));

        when(actorRepository.existsById(any())).thenReturn(true);
        when(balanceConverter.convert(any(BalanceRecordLimitedDto.class))).thenReturn(balanceRecord);
        when(estimatedExpenseService.getOne()).thenReturn(defaultEstimatedExpenseFullDto());
        when(incomeSourceService.getTotalIncome()).thenReturn(defaultTotalIncomeDto());
        when(incomeSourceService.getTotalIncome()).thenReturn(defaultTotalIncomeDto());
        when(currencyRateService.getValueInCurrency(any(), any()))
                .thenReturn(new BigDecimal("50.00"));

        balanceService.save(recordLimitedDto);

        assertNotNull(balanceRecord.getTotalIncome());
        assertNotNull(balanceRecord.getTotal1to7());
        assertNotNull(balanceRecord.getTotal8to14());
        assertNotNull(balanceRecord.getTotal15to21());
        assertNotNull(balanceRecord.getTotal22to31());
        assertEquals(new BigDecimal("50.00"), balanceRecord.getTotalIncome());
        verify(balanceRecordRepository, times(1)).save(balanceRecord);
        verify(balanceItemRepository, times(2)).save(any(BalanceItem.class));
    }

    @Test
    void testTrySavingWithNotExistingActors() {
        BalanceRecordLimitedDto recordLimitedDto = defaultBalanceRecordLimitedDto();
        BalanceRecord balanceRecord = defaultBalanceRecord();
        List<Long> invalidIds = balanceRecord.getBalanceItems().stream().map(BalanceItem::getActor).map(Actor::getId).toList();

        when(actorRepository.existsById(any())).thenReturn(false);

        try {
            balanceService.save(recordLimitedDto);
            fail("Exception was not thrown");
        } catch (Exception exception) {
            assertEquals(IllegalArgumentException.class, exception.getClass());
            assertEquals("Provided actor ids do not exist: " + invalidIds, exception.getMessage());
        }
        verify(balanceRecordRepository, never()).save(balanceRecord);
        verify(balanceItemRepository, never()).save(any(BalanceItem.class));
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

        BigDecimal actualResult = balanceService.getExpensesForFullWeeksLeftInCurrentMonth(
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

        BigDecimal actualResult = balanceService.getExpensesForFullWeeksLeftInCurrentMonth(
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

        when(dateManager.getLastDayOfMonthByDate(currentDate)).thenReturn(31);

        BigDecimal actualResult = balanceService.getExpensesForDaysLeftInCurrentWeek(
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

        BigDecimal actualResult = balanceService.getExpensesForDaysLeftInCurrentWeek(
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

        BigDecimal actualResult = balanceService.getExpensesForDaysLeftInCurrentWeek(
                currentMonthWeek, currentDate, estimatedExpensePerWeek);

        assertEquals(expectedResult, actualResult);
    }
}
