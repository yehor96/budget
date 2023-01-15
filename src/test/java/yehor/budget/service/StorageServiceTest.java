package yehor.budget.service;

import org.junit.jupiter.api.Test;
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
import java.util.Optional;

import static common.factory.StorageFactory.DEFAULT_STORED_IN_TOTAL;
import static common.factory.StorageFactory.defaultStorageRecord;
import static common.factory.StorageFactory.defaultStorageRecordFullDto;
import static common.factory.StorageFactory.defaultStorageRecordLimitedDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StorageServiceTest {

    private final StorageItemRepository storageItemRepository = mock(StorageItemRepository.class);
    private final StorageRecordRepository storageRecordRepository = mock(StorageRecordRepository.class);
    private final PageableHelper pageableHelper = mock(PageableHelper.class);
    private final StorageConverter storageConverter = mock(StorageConverter.class);
    private final CurrencyRateService currencyRateService = mock(CurrencyRateService.class);

    private final StorageService storageService = new StorageService(
            storageItemRepository,
            storageRecordRepository,
            pageableHelper,
            storageConverter,
            currencyRateService
    );

    @Test
    void testGetLatestReturnsEmptyOptionalWhenThereAreNoRecords() {
        when(pageableHelper.getLatestByDate(any())).thenReturn(Optional.empty());

        Optional<StorageRecordFullDto> result = storageService.getLatest();

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetLatestReturnsOptionalWithValueWhenThereAreRecords() {
        StorageRecordFullDto storageRecordFullDto = defaultStorageRecordFullDto();
        StorageRecord storageRecord = defaultStorageRecord();

        when(pageableHelper.getLatestByDate(any())).thenReturn(Optional.of(storageRecord));
        when(storageConverter.convert(storageRecord)).thenReturn(storageRecordFullDto);

        Optional<StorageRecordFullDto> optActualStorageRecordDto = storageService.getLatest();

        assertTrue(optActualStorageRecordDto.isPresent());
        StorageRecordFullDto actualRecordDto = optActualStorageRecordDto.get();
        assertNotNull(actualRecordDto.getStoredInTotal());
        assertEquals(DEFAULT_STORED_IN_TOTAL, actualRecordDto.getStoredInTotal());
        assertFalse(actualRecordDto.getStorageItems().isEmpty());
    }

    @Test
    void testSaveSuccessfullySetsStoredInTotal() {
        StorageRecordLimitedDto recordLimitedDto = defaultStorageRecordLimitedDto();
        StorageRecord storageRecord = defaultStorageRecord();
        storageRecord.setStoredInTotal(null);

        when(storageConverter.convert(any(StorageRecordLimitedDto.class))).thenReturn(storageRecord);
        when(currencyRateService.getValueInCurrency(any(), any()))
                .thenReturn(new BigDecimal("50.00"))
                .thenReturn(new BigDecimal("50.00"));

        storageService.save(recordLimitedDto);

        assertNotNull(storageRecord.getStoredInTotal());
        assertEquals(new BigDecimal("100.00"), storageRecord.getStoredInTotal());
        verify(storageRecordRepository, times(1)).save(storageRecord);
        verify(storageItemRepository, times(2)).save(any(StorageItem.class));
    }
}
