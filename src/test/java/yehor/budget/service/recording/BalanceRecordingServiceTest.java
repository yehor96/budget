package yehor.budget.service.recording;

import org.junit.jupiter.api.Test;
import yehor.budget.common.date.MonthWeek;
import yehor.budget.common.util.PageableHelper;
import yehor.budget.entity.Actor;
import yehor.budget.entity.recording.BalanceItem;
import yehor.budget.entity.recording.BalanceRecord;
import yehor.budget.repository.ActorRepository;
import yehor.budget.repository.recording.BalanceItemRepository;
import yehor.budget.repository.recording.BalanceRecordRepository;
import yehor.budget.repository.recording.IncomeSourceRecordRepository;
import yehor.budget.service.EstimatedExpenseService;
import yehor.budget.service.IncomeSourceService;
import yehor.budget.web.converter.BalanceConverter;
import yehor.budget.web.converter.IncomeSourceConverter;
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

class BalanceRecordingServiceTest {

    private final BalanceItemRepository balanceItemRepository = mock(BalanceItemRepository.class);
    private final BalanceRecordRepository balanceRecordRepository = mock(BalanceRecordRepository.class);
    private final BalanceConverter balanceConverter = mock(BalanceConverter.class);
    private final ActorRepository actorRepository = mock(ActorRepository.class);
    private final IncomeSourceService incomeSourceService = mock(IncomeSourceService.class);
    private final EstimatedExpenseService estimatedExpenseService = mock(EstimatedExpenseService.class);
    private final PageableHelper pageableHelper = mock(PageableHelper.class);
    private final IncomeSourceRecordRepository incomeSourceRecordRepository = mock(IncomeSourceRecordRepository.class);
    private final IncomeSourceConverter incomeSourceConverter = mock(IncomeSourceConverter.class);
    private final BalanceEstimationService balanceEstimationService = mock(BalanceEstimationService.class);

    private final BalanceRecordingService balanceRecordingService = new BalanceRecordingService(
            balanceItemRepository,
            balanceRecordRepository,
            balanceConverter,
            actorRepository,
            incomeSourceService,
            estimatedExpenseService,
            pageableHelper,
            incomeSourceRecordRepository,
            incomeSourceConverter,
            balanceEstimationService
    );

    @Test
    void testGetLatestReturnsEmptyOptionalWhenThereAreNoRecords() {
        when(pageableHelper.getLatestByDate(any())).thenReturn(Optional.empty());

        Optional<BalanceRecordFullDto> result = balanceRecordingService.getLatest();

        assertTrue(result.isEmpty());
    }

//    @Test
//    void testGetLatestReturnsOptionalWithValueWhenThereAreRecords() {
//        BalanceRecordFullDto balanceRecordFullDto = defaultBalanceRecordFullDto();
//        BalanceRecord balanceRecord = defaultBalanceRecord();
//        LocalDate expectedDateEOM = LocalDate.of(2010, 1, 31);
//
//        when(pageableHelper.getLatestByDate(any())).thenReturn(Optional.of(balanceRecord));
//        when(balanceConverter.convert(balanceRecord)).thenReturn(balanceRecordFullDto);
//        when(dateManager.getMonthEndDate(LocalDate.now())).thenReturn(expectedDateEOM);
//
//        Optional<BalanceRecordFullDto> optActualBalanceRecordDto = balanceRecordingService.getLatest();
//
//        assertTrue(optActualBalanceRecordDto.isPresent());
//        BalanceRecordFullDto actualRecordDto = optActualBalanceRecordDto.get();
//        assertNotNull(actualRecordDto.getTotalBalance());
//        assertEquals(DEFAULT_BALANCE_RECORD_TOTAL, actualRecordDto.getTotalBalance());
//
//        BalanceEstimateDto balanceEstimateDto = actualRecordDto.getBalanceEstimate();
//        assertNotNull(balanceEstimateDto);
//        assertNotNull(balanceEstimateDto.getExpenseByEndOfMonth());
//        assertEquals(balanceRecord.getTotalIncome(), balanceEstimateDto.getIncomeByEndOfMonth());
//        BigDecimal profit = balanceEstimateDto.getIncomeByEndOfMonth()
//                .add(balanceEstimateDto.getPreviousTotal())
//                .subtract(balanceEstimateDto.getExpenseByEndOfMonth());
//        assertEquals(profit, balanceEstimateDto.getProfitByEndOfMonth());
//        assertEquals(expectedDateEOM, balanceEstimateDto.getEndOfMonthDate());
//        assertFalse(actualRecordDto.getBalanceItems().isEmpty());
//    }
//
//    @Test
//    void testSaveSuccessfullyWhileSettingExpensesAndIncomeWithAllSourcesIncludedIntoTotalIncome() {
//        BalanceRecordLimitedDto recordLimitedDto = defaultBalanceRecordLimitedDto();
//        BalanceRecord balanceRecord = balanceRecordWithNotSetExpensesAndIncome();
//        balanceRecord.setDate(LocalDate.of(2022, 10, 10));
//
//        when(actorRepository.existsById(any())).thenReturn(true);
//        when(balanceConverter.convert(any(BalanceRecordLimitedDto.class))).thenReturn(balanceRecord);
//        when(estimatedExpenseService.getOne()).thenReturn(defaultEstimatedExpenseFullDto());
//        when(incomeSourceService.getTotalIncome()).thenReturn(defaultTotalIncomeDto());
//        when(incomeSourceService.getTotalIncome()).thenReturn(defaultTotalIncomeDto());
//        when(currencyRateService.getValueInCurrency(any(), any()))
//                .thenReturn(new BigDecimal("50.00"));
//
//        balanceRecordingService.save(recordLimitedDto);
//
//        assertNotNull(balanceRecord.getTotalIncome());
//        assertNotNull(balanceRecord.getTotal1to7());
//        assertNotNull(balanceRecord.getTotal8to14());
//        assertNotNull(balanceRecord.getTotal15to21());
//        assertNotNull(balanceRecord.getTotal22to31());
//        assertEquals(new BigDecimal("100.00"), balanceRecord.getTotalIncome());
//        verify(balanceRecordRepository, times(1)).save(balanceRecord);
//        verify(balanceItemRepository, times(2)).save(any(BalanceItem.class));
//    }
//
//    @Test
//    void testSaveSuccessfullyWhileSettingExpensesAndIncomeWithOnlySourcesAfterBalanceRecordDateIncludedIntoTotalIncome() {
//        BalanceRecordLimitedDto recordLimitedDto = defaultBalanceRecordLimitedDto();
//        BalanceRecord balanceRecord = balanceRecordWithNotSetExpensesAndIncome();
//        balanceRecord.setDate(LocalDate.of(2022, 10, 23));
//
//        when(actorRepository.existsById(any())).thenReturn(true);
//        when(balanceConverter.convert(any(BalanceRecordLimitedDto.class))).thenReturn(balanceRecord);
//        when(estimatedExpenseService.getOne()).thenReturn(defaultEstimatedExpenseFullDto());
//        when(incomeSourceService.getTotalIncome()).thenReturn(defaultTotalIncomeDto());
//        when(incomeSourceService.getTotalIncome()).thenReturn(defaultTotalIncomeDto());
//        when(currencyRateService.getValueInCurrency(any(), any()))
//                .thenReturn(new BigDecimal("50.00"));
//
//        balanceRecordingService.save(recordLimitedDto);
//
//        assertNotNull(balanceRecord.getTotalIncome());
//        assertNotNull(balanceRecord.getTotal1to7());
//        assertNotNull(balanceRecord.getTotal8to14());
//        assertNotNull(balanceRecord.getTotal15to21());
//        assertNotNull(balanceRecord.getTotal22to31());
//        assertEquals(new BigDecimal("50.00"), balanceRecord.getTotalIncome());
//        verify(balanceRecordRepository, times(1)).save(balanceRecord);
//        verify(balanceItemRepository, times(2)).save(any(BalanceItem.class));
//    }
//
//    @Test
//    void testTrySavingWithNotExistingActors() {
//        BalanceRecordLimitedDto recordLimitedDto = defaultBalanceRecordLimitedDto();
//        BalanceRecord balanceRecord = defaultBalanceRecord();
//        List<Long> invalidIds = balanceRecord.getBalanceItems().stream().map(BalanceItem::getActor).map(Actor::getId).toList();
//
//        when(actorRepository.existsById(any())).thenReturn(false);
//
//        try {
//            balanceRecordingService.save(recordLimitedDto);
//            fail("Exception was not thrown");
//        } catch (Exception exception) {
//            assertEquals(IllegalArgumentException.class, exception.getClass());
//            assertEquals("Provided actor ids do not exist: " + invalidIds, exception.getMessage());
//        }
//        verify(balanceRecordRepository, never()).save(balanceRecord);
//        verify(balanceItemRepository, never()).save(any(BalanceItem.class));
//    }

}
