package yehor.budget.web.converter;

import org.springframework.stereotype.Component;
import yehor.budget.entity.StorageItem;
import yehor.budget.entity.StorageRecord;
import yehor.budget.web.dto.full.StorageItemFullDto;
import yehor.budget.web.dto.full.StorageRecordFullDto;

import java.util.List;

@Component
public class StorageConverter {

    public StorageRecordFullDto convert(StorageRecord storageRecord) {
        return StorageRecordFullDto.builder()
                .id(storageRecord.getId())
                .date(storageRecord.getDate())
                .totalStorage(storageRecord.getTotalStorage())
                .storageItems(convert(storageRecord.getStorageItems()))
                .build();
    }

    private StorageItemFullDto convert(StorageItem storageItem) {
        return StorageItemFullDto.builder()
                .id(storageItem.getId())
                .name(storageItem.getName())
                .value(storageItem.getValue())
                .currency(storageItem.getCurrency())
                .build();
    }

    private List<StorageItemFullDto> convert(List<StorageItem> storageItems) {
        return storageItems.stream().map(this::convert).toList();
    }

}
