package yehor.budget.web.converter;

import org.springframework.stereotype.Component;
import yehor.budget.entity.StorageItem;
import yehor.budget.entity.StorageRecord;
import yehor.budget.web.dto.full.StorageItemFullDto;
import yehor.budget.web.dto.full.StorageRecordFullDto;
import yehor.budget.web.dto.limited.StorageItemLimitedDto;
import yehor.budget.web.dto.limited.StorageRecordLimitedDto;

import java.util.List;

@Component
public class StorageConverter {

    public StorageRecordFullDto convert(StorageRecord storageRecord) {
        return StorageRecordFullDto.builder()
                .id(storageRecord.getId())
                .date(storageRecord.getDate())
                .storedInTotal(storageRecord.getStoredInTotal())
                .storageItems(convert(storageRecord.getStorageItems()))
                .build();
    }

    public StorageItemFullDto convert(StorageItem storageItem) {
        return StorageItemFullDto.builder()
                .id(storageItem.getId())
                .name(storageItem.getName())
                .value(storageItem.getValue())
                .currency(storageItem.getCurrency())
                .build();
    }

    public List<StorageItemFullDto> convert(List<StorageItem> storageItems) {
        return storageItems.stream().map(this::convert).toList();
    }

    public StorageRecord convert(StorageRecordLimitedDto storageRecordDto) {
        StorageRecord storageRecord = StorageRecord.builder()
                .date(storageRecordDto.getDate())
                .build();
        storageRecord.setStorageItems(convert(storageRecordDto.getStorageItems(), storageRecord));
        return storageRecord;
    }

    public StorageItem convert(StorageItemLimitedDto storageItemLimitedDto, StorageRecord storageRecord) {
        return StorageItem.builder()
                .name(storageItemLimitedDto.getName())
                .value(storageItemLimitedDto.getValue())
                .currency(storageItemLimitedDto.getCurrency())
                .storageRecord(storageRecord)
                .build();
    }

    public List<StorageItem> convert(List<StorageItemLimitedDto> storageItemLimitedDtos, StorageRecord storageRecord) {
        return storageItemLimitedDtos.stream().map(item -> convert(item, storageRecord)).toList();
    }
}
