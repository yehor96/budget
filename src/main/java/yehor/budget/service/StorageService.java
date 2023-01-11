package yehor.budget.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yehor.budget.common.util.PageableHelper;
import yehor.budget.entity.StorageRecord;
import yehor.budget.repository.StorageItemRepository;
import yehor.budget.repository.StorageRecordRepository;
import yehor.budget.web.converter.StorageConverter;
import yehor.budget.web.dto.full.StorageRecordFullDto;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final StorageItemRepository storageItemRepository;
    private final StorageRecordRepository storageRecordRepository;
    private final PageableHelper pageableHelper;
    private final StorageConverter storageConverter;

    @Transactional(readOnly = true)
    public Optional<StorageRecordFullDto> getLatest() {
        Optional<StorageRecord> latestOpt = pageableHelper.getLatestByDate(storageRecordRepository);
        return latestOpt.isEmpty() ? Optional.empty() : Optional.of(storageConverter.convert(latestOpt.get()));
    }
}
