package yehor.budget.service.recording;

import org.junit.jupiter.api.Test;
import org.springframework.dao.EmptyResultDataAccessException;
import yehor.budget.common.exception.ObjectNotFoundException;
import yehor.budget.common.util.PageableHelper;
import yehor.budget.entity.StorageItem;
import yehor.budget.entity.StorageRecord;
import yehor.budget.repository.StorageItemRepository;
import yehor.budget.repository.StorageRecordRepository;
import yehor.budget.service.client.currency.CurrencyRateService;
import yehor.budget.web.converter.StorageConverter;
import yehor.budget.web.dto.full.StorageRecordFullDto;
import yehor.budget.web.dto.limited.StorageRecordLimitedDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static common.factory.StorageFactory.DEFAULT_STORED_IN_TOTAL;
import static common.factory.StorageFactory.defaultStorageRecord;
import static common.factory.StorageFactory.defaultStorageRecordFullDto;
import static common.factory.StorageFactory.defaultStorageRecordLimitedDto;
import static common.factory.StorageFactory.secondStorageRecord;
import static common.factory.StorageFactory.secondStorageRecordFullDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StorageRecordingServiceTest {

    private final StorageItemRepository storageItemRepository = mock(StorageItemRepository.class);
    private final StorageRecordRepository storageRecordRepository = mock(StorageRecordRepository.class);
    private final PageableHelper pageableHelper = mock(PageableHelper.class);
    private final StorageConverter storageConverter = mock(StorageConverter.class);
    private final CurrencyRateService currencyRateService = mock(CurrencyRateService.class);

    private final StorageRecordingService storageRecordingService = new StorageRecordingService(
            storageItemRepository,
            storageRecordRepository,
            pageableHelper,
            storageConverter,
            currencyRateService
    );

    @Test
    void testGetLatestReturnsEmptyOptionalWhenThereAreNoRecords() {
        when(pageableHelper.getLatestByDate(any())).thenReturn(Optional.empty());

        Optional<StorageRecordFullDto> result = storageRecordingService.getLatest();

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetLatestReturnsOptionalWithValueWhenThereAreRecords() {
        StorageRecordFullDto storageRecordFullDto = defaultStorageRecordFullDto();
        StorageRecord storageRecord = defaultStorageRecord();

        when(pageableHelper.getLatestByDate(any())).thenReturn(Optional.of(storageRecord));
        when(storageConverter.convert(storageRecord)).thenReturn(storageRecordFullDto);

        Optional<StorageRecordFullDto> optActualStorageRecordDto = storageRecordingService.getLatest();

        assertTrue(optActualStorageRecordDto.isPresent());
        StorageRecordFullDto actualRecordDto = optActualStorageRecordDto.get();
        assertNotNull(actualRecordDto.getStoredInTotal());
        assertEquals(DEFAULT_STORED_IN_TOTAL, actualRecordDto.getStoredInTotal());
        assertFalse(actualRecordDto.getStorageItems().isEmpty());
    }

    @Test
    void testFindAllInInterval() {
        StorageRecordFullDto storageRecordFullDto = defaultStorageRecordFullDto();
        StorageRecordFullDto storageRecordFullDto2 = secondStorageRecordFullDto();
        StorageRecord storageRecord = defaultStorageRecord();
        StorageRecord storageRecord2 = secondStorageRecord();

        when(storageRecordRepository.findAllInInterval(any(), any())).thenReturn(List.of(storageRecord, storageRecord2));
        when(storageConverter.convert(storageRecord)).thenReturn(storageRecordFullDto);
        when(storageConverter.convert(storageRecord2)).thenReturn(storageRecordFullDto2);

        List<StorageRecordFullDto> recordsInInterval = storageRecordingService.findAllInInterval(
                LocalDate.of(2023, 3, 15), LocalDate.of(2023, 3, 25));

        assertFalse(recordsInInterval.isEmpty());
        recordsInInterval.forEach(actualRecordDto -> {
            assertNotNull(actualRecordDto.getStoredInTotal());
            assertEquals(DEFAULT_STORED_IN_TOTAL, actualRecordDto.getStoredInTotal());
            assertFalse(actualRecordDto.getStorageItems().isEmpty());
        });
    }

    @Test
    void testSaveSuccessfullySetsStoredInTotal() {
        StorageRecordLimitedDto recordLimitedDto = defaultStorageRecordLimitedDto();
        StorageRecord storageRecord = defaultStorageRecord();
        storageRecord.setStoredInTotal(null);

        when(storageRecordRepository.existsByDate(any())).thenReturn(false);
        when(storageConverter.convert(any(StorageRecordLimitedDto.class))).thenReturn(storageRecord);
        when(currencyRateService.getValueInCurrency(any(), any()))
                .thenReturn(new BigDecimal("50.00"))
                .thenReturn(new BigDecimal("50.00"));

        storageRecordingService.save(recordLimitedDto);

        assertNotNull(storageRecord.getStoredInTotal());
        assertEquals(new BigDecimal("100.00"), storageRecord.getStoredInTotal());
        verify(storageRecordRepository, times(1)).save(storageRecord);
        verify(storageItemRepository, times(2)).save(any(StorageItem.class));
    }

    @Test
    void testTrySavingStorageRecordWithExistingDate() {
        StorageRecordLimitedDto recordLimitedDto = defaultStorageRecordLimitedDto();

        when(storageRecordRepository.existsByDate(any())).thenReturn(true);

        try {
            storageRecordingService.save(recordLimitedDto);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(IllegalArgumentException.class, e.getClass());
            assertEquals("Record with provided date " + recordLimitedDto.getDate() + " already exists", e.getMessage());
        }
        verify(storageRecordRepository, never()).save(any(StorageRecord.class));
        verify(storageItemRepository, never()).save(any(StorageItem.class));
    }

    @Test
    void testDeleteStorageRecord() {
        Long id = 1L;
        storageRecordingService.delete(id);
        verify(storageRecordRepository, times(1)).deleteById(id);
    }

    @Test
    void testTryDeleteStorageRecordWithNotExistingId() {
        Long id = 1L;
        doThrow(EmptyResultDataAccessException.class).when(storageRecordRepository).deleteById(id);
        try {
            storageRecordingService.delete(id);
        } catch (Exception e) {
            assertEquals(ObjectNotFoundException.class, e.getClass());
            assertEquals("Storage with id " + id + " not found", e.getMessage());
        }
    }
}
