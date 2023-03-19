package yehor.budget.service.recording;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yehor.budget.common.Currency;
import yehor.budget.common.exception.ObjectNotFoundException;
import yehor.budget.common.util.PageableHelper;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageRecordingService {

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
        validateRecordWithDateNotExists(storageRecordDto.getDate());
        StorageRecord storageRecord = storageConverter.convert(storageRecordDto);
        setStoredInTotal(storageRecord);
        storageRecordRepository.save(storageRecord);
        log.info("Saved: {}", storageRecord);
        storageRecord.getStorageItems().forEach(storageItemRepository::save);
        log.info("List of saved storage items: {}", storageRecord.getStorageItems());
    }

    @Transactional(readOnly = true)
    public List<StorageRecordFullDto> findAllInInterval(LocalDate dateFrom, LocalDate dateTo) {
        List<StorageRecord> storageRecords = storageRecordRepository.findAllInInterval(dateFrom, dateTo);
        return storageRecords.stream()
                .map(storageConverter::convert)
                .toList();
    }

    @Transactional
    public void delete(Long id) {
        try {
            storageRecordRepository.deleteById(id);
            log.info("Storage record with id {} is deleted", id);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Storage with id " + id + " not found");
        }
    }

    private void validateRecordWithDateNotExists(LocalDate date) {
        if (storageRecordRepository.existsByDate(date)) {
            throw new IllegalArgumentException("Record with provided date " + date + " already exists");
        }
    }

    private void setStoredInTotal(StorageRecord storageRecord) {
        BigDecimal total = storageRecord.getStorageItems().stream()
                .map(item -> currencyRateService.getValueInCurrency(item, TOTAL_STORED_CURRENCY))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        storageRecord.setStoredInTotal(total);
    }
}
