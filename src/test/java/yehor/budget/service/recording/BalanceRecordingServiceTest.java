package yehor.budget.service.recording;

import org.junit.jupiter.api.Test;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.util.CollectionUtils;
import yehor.budget.common.exception.ObjectNotFoundException;
import yehor.budget.common.util.PageableHelper;
import yehor.budget.entity.recording.BalanceItem;
import yehor.budget.entity.recording.BalanceRecord;
import yehor.budget.entity.recording.IncomeSourceRecord;
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
import java.util.Optional;

import static common.factory.BalanceFactory.*;
import static common.factory.EstimatedExpenseFactory.defaultEstimatedExpenseFullDto;
import static common.factory.IncomeSourceFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BalanceRecordingServiceTest {

    private final BalanceItemRepository balanceItemRepository = mock(BalanceItemRepository.class);
    private final BalanceRecordRepository balanceRecordRepository = mock(BalanceRecordRepository.class);
    private final BalanceConverter balanceConverter = mock(BalanceConverter.class);
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

    @Test
    void testGetLatestReturnsOptionalWithValueWhenThereAreRecords() {
        BalanceRecordFullDto balanceRecordFullDto = defaultBalanceRecordFullDto();
        BalanceRecord balanceRecord = defaultBalanceRecord();
        LocalDate expectedDateEOM = LocalDate.of(2023, 1, 31);

        when(pageableHelper.getLatestByDate(any())).thenReturn(Optional.of(balanceRecord));
        when(balanceConverter.convert(balanceRecord)).thenReturn(balanceRecordFullDto);
        when(balanceEstimationService.getBalanceEstimation(any(), any(), any()))
                .thenReturn(List.of(defaultBalanceEstimationDto()));

        Optional<BalanceRecordFullDto> optActualBalanceRecordDto = balanceRecordingService.getLatest();

        assertTrue(optActualBalanceRecordDto.isPresent());
        BalanceRecordFullDto actualRecordDto = optActualBalanceRecordDto.get();
        assertNotNull(actualRecordDto.getTotalBalance());
        assertEquals(DEFAULT_BALANCE_RECORD_TOTAL, actualRecordDto.getTotalBalance());

        assertFalse(CollectionUtils.isEmpty(actualRecordDto.getBalanceEstimates()));
        BalanceEstimateDto balanceEstimateDto = actualRecordDto.getBalanceEstimates().get(0);
        assertNotNull(balanceEstimateDto);
        assertNotNull(balanceEstimateDto.getExpenseByEndOfMonth());
        BigDecimal profit = balanceEstimateDto.getIncomeByEndOfMonth()
                .add(balanceEstimateDto.getPreviousTotal())
                .subtract(balanceEstimateDto.getExpenseByEndOfMonth());
        assertEquals(profit, balanceEstimateDto.getProfitByEndOfMonth());
        assertEquals(expectedDateEOM, balanceEstimateDto.getEndOfMonthDate());
        assertFalse(CollectionUtils.isEmpty(actualRecordDto.getBalanceItems()));
    }

    @Test
    void testFindAllInInterval() {
        BalanceRecordFullDto balanceRecordFullDto = defaultBalanceRecordFullDto();
        BalanceRecordFullDto balanceRecordFullDto2 = secondBalanceRecordFullDto();
        BalanceRecord balanceRecord = secondBalanceRecord();
        BalanceRecord balanceRecord2 = balanceRecordWithSetIncomes();
        LocalDate expectedDateEOM = LocalDate.of(2023, 1, 31);

        when(balanceRecordRepository.findAllInInterval(any(), any())).thenReturn(List.of(balanceRecord, balanceRecord2));
        when(balanceConverter.convert(balanceRecord)).thenReturn(balanceRecordFullDto);
        when(balanceConverter.convert(balanceRecord2)).thenReturn(balanceRecordFullDto2);
        when(balanceEstimationService.getBalanceEstimation(any(), any(), any()))
                .thenReturn(List.of(defaultBalanceEstimationDto()));

        List<BalanceRecordFullDto> recordsInInterval = balanceRecordingService.findAllInInterval(
                LocalDate.of(2023, 1, 10), LocalDate.of(2023, 1, 20));

        assertFalse(recordsInInterval.isEmpty());
        recordsInInterval.forEach(actualRecordDto -> {
            assertNotNull(actualRecordDto.getTotalBalance());
            assertEquals(DEFAULT_BALANCE_RECORD_TOTAL, actualRecordDto.getTotalBalance());

            assertFalse(CollectionUtils.isEmpty(actualRecordDto.getBalanceEstimates()));
            BalanceEstimateDto balanceEstimateDto = actualRecordDto.getBalanceEstimates().get(0);
            assertNotNull(balanceEstimateDto);
            assertNotNull(balanceEstimateDto.getExpenseByEndOfMonth());
            BigDecimal profit = balanceEstimateDto.getIncomeByEndOfMonth()
                    .add(balanceEstimateDto.getPreviousTotal())
                    .subtract(balanceEstimateDto.getExpenseByEndOfMonth());
            assertEquals(profit, balanceEstimateDto.getProfitByEndOfMonth());
            assertEquals(expectedDateEOM, balanceEstimateDto.getEndOfMonthDate());
            assertFalse(CollectionUtils.isEmpty(actualRecordDto.getBalanceItems()));
        });
    }

    @Test
    void testSaveSuccessfullyWhileSettingExpensesAndSavingIncomes() {
        BalanceRecordLimitedDto recordLimitedDto = defaultBalanceRecordLimitedDto();
        BalanceRecord balanceRecord = balanceRecordWithNotSetExpensesAndIncome();
        balanceRecord.setDate(LocalDate.of(2022, 10, 10));

        when(balanceRecordRepository.existsByDate(any())).thenReturn(false);
        when(balanceConverter.convert(any(BalanceRecordLimitedDto.class))).thenReturn(balanceRecord);
        when(estimatedExpenseService.getOne()).thenReturn(defaultEstimatedExpenseFullDto());
        when(incomeSourceService.getTotalIncome()).thenReturn(defaultTotalIncomeDto());
        when(incomeSourceConverter.convert(any(), any()))
                .thenReturn(defaultIncomeSourceRecord())
                .thenReturn(secondIncomeSourceRecord());
        when(balanceConverter.convertToDtoWithNoEstimates(any()))
                .thenReturn(balanceRecordFullDtoWithoutEstimates());
        when(balanceRecordRepository.save(balanceRecord)).thenReturn(balanceRecord);

        balanceRecordingService.save(recordLimitedDto);

        assertNotNull(balanceRecord.getTotal1to7());
        assertNotNull(balanceRecord.getTotal8to14());
        assertNotNull(balanceRecord.getTotal15to21());
        assertNotNull(balanceRecord.getTotal22to31());
        verify(balanceRecordRepository, times(1)).save(balanceRecord);
        verify(balanceItemRepository, times(2)).save(any(BalanceItem.class));
        verify(incomeSourceRecordRepository, times(2)).save(any(IncomeSourceRecord.class));
    }

    @Test
    void testTrySavingBalanceRecordWithExistingDate() {
        BalanceRecordLimitedDto recordLimitedDto = defaultBalanceRecordLimitedDto();

        when(balanceRecordRepository.existsByDate(any())).thenReturn(true);

        try {
            balanceRecordingService.save(recordLimitedDto);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
            assertEquals("Record with provided date " + recordLimitedDto.getDate() + " already exists", e.getMessage());
        }
        verify(balanceRecordRepository, never()).save(any(BalanceRecord.class));
        verify(balanceItemRepository, never()).save(any(BalanceItem.class));
    }

    @Test
    void testDeleteBalanceRecord() {
        Long id = 1L;
        balanceRecordingService.delete(id);
        verify(balanceRecordRepository, times(1)).deleteById(id);
    }

    @Test
    void testTryDeleteBalanceRecordWithNotExistingId() {
        Long id = 1L;
        doThrow(EmptyResultDataAccessException.class).when(balanceRecordRepository).deleteById(id);
        try {
            balanceRecordingService.delete(id);
        } catch (Exception e) {
            assertEquals(ObjectNotFoundException.class, e.getClass());
            assertEquals("Balance with id " + id + " not found", e.getMessage());
        }
    }

}
