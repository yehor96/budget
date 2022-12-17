package yehor.budget.service;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import yehor.budget.entity.Actor;
import yehor.budget.entity.BalanceItem;
import yehor.budget.entity.BalanceRecord;
import yehor.budget.repository.ActorRepository;
import yehor.budget.repository.BalanceItemRepository;
import yehor.budget.repository.BalanceRecordRepository;
import yehor.budget.web.converter.BalanceConverter;
import yehor.budget.web.dto.full.BalanceRecordFullDto;
import yehor.budget.web.dto.limited.BalanceRecordLimitedDto;

import java.util.List;
import java.util.Optional;

import static common.factory.BalanceFactory.DEFAULT_BALANCE_RECORD_TOTAL;
import static common.factory.BalanceFactory.defaultBalanceRecord;
import static common.factory.BalanceFactory.defaultBalanceRecordFullDto;
import static common.factory.BalanceFactory.defaultBalanceRecordLimitedDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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

    private final BalanceService balanceService = new BalanceService(
            balanceItemRepository, balanceRecordRepository, balanceConverter, actorRepository);

    @Test
    void testGetLatestReturnsEmptyOptionalWhenThereAreNoRecords() {
        @SuppressWarnings("unchecked")
        Page<BalanceRecord> page = mock(Page.class);

        when(balanceRecordRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(page.isEmpty()).thenReturn(true);

        Optional<BalanceRecordFullDto> result = balanceService.getLatest();

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetLatestReturnsOptionalWithValueWhenThereAreRecords() {
        @SuppressWarnings("unchecked")
        Page<BalanceRecord> page = mock(Page.class);
        BalanceRecordFullDto balanceRecordFullDto = defaultBalanceRecordFullDto();
        BalanceRecord balanceRecord = defaultBalanceRecord();
        List<BalanceRecord> balanceRecordList = List.of(balanceRecord);

        when(balanceRecordRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(page.isEmpty()).thenReturn(false);
        when(page.toList()).thenReturn(balanceRecordList);
        when(balanceConverter.convert(balanceRecord)).thenReturn(balanceRecordFullDto);

        Optional<BalanceRecordFullDto> optActualBalanceRecordDto = balanceService.getLatest();

        assertTrue(optActualBalanceRecordDto.isPresent());
        BalanceRecordFullDto actualRecordDto = optActualBalanceRecordDto.get();
        assertNotNull(actualRecordDto.getTotal());
        assertEquals(DEFAULT_BALANCE_RECORD_TOTAL, actualRecordDto.getTotal());
    }

    @Test
    void testSaveSuccessfully() {
        BalanceRecordLimitedDto recordLimitedDto = defaultBalanceRecordLimitedDto();
        BalanceRecord balanceRecord = defaultBalanceRecord();

        when(actorRepository.existsById(any())).thenReturn(true);
        when(balanceConverter.convert(any(BalanceRecordLimitedDto.class))).thenReturn(balanceRecord);
        when(balanceRecordRepository.save(balanceRecord)).thenReturn(balanceRecord);
        when(balanceConverter.convert(anyList(), any(BalanceRecord.class))).thenReturn(balanceRecord.getBalanceItems());

        balanceService.save(recordLimitedDto);

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
}
