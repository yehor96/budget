package yehor.budget.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yehor.budget.common.Currency;
import yehor.budget.common.util.PageableHelper;
import yehor.budget.entity.StorageRecord;
import yehor.budget.repository.StorageItemRepository;
import yehor.budget.repository.StorageRecordRepository;
import yehor.budget.service.client.currency.CurrencyRateService;
import yehor.budget.web.converter.StorageConverter;
import yehor.budget.web.dto.full.StorageRecordFullDto;
import yehor.budget.web.dto.limited.StorageRecordLimitedDto;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StorageService {

    private static final Currency TOTAL_STORED_CURRENCY = Currency.USD;

    private final StorageItemRepository storageItemRepository;
    private final StorageRecordRepository storageRecordRepository;
    private final PageableHelper pageableHelper;
    private final StorageConverter storageConverter;
    private final CurrencyRateService currencyRateService;

    @Transactional(readOnly = true)
    public Optional<StorageRecordFullDto> getLatest() {
        Optional<StorageRecord> latestOpt = pageableHelper.getLatestByDate(storageRecordRepository);
        return latestOpt.isEmpty() ? Optional.empty() : Optional.of(storageConverter.convert(latestOpt.get()));
    }

    @Transactional
    public void save(StorageRecordLimitedDto storageRecordDto) {
        StorageRecord storageRecord = storageConverter.convert(storageRecordDto);
        setStoredInTotal(storageRecord);

        storageRecordRepository.save(storageRecord);
        storageRecord.getStorageItems().forEach(storageItemRepository::save);
    }

    private void setStoredInTotal(StorageRecord storageRecord) {
        BigDecimal total = storageRecord.getStorageItems().stream()
                .map(item -> currencyRateService.getValueInCurrency(item, TOTAL_STORED_CURRENCY))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        storageRecord.setStoredInTotal(total);
    }
}
