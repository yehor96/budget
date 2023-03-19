package common.factory;

import lombok.experimental.UtilityClass;
import yehor.budget.common.Currency;
import yehor.budget.entity.StorageItem;
import yehor.budget.entity.StorageRecord;
import yehor.budget.web.dto.full.StorageItemFullDto;
import yehor.budget.web.dto.full.StorageRecordFullDto;
import yehor.budget.web.dto.limited.StorageItemLimitedDto;
import yehor.budget.web.dto.limited.StorageRecordLimitedDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@UtilityClass
public class StorageFactory {

    public static final Long DEFAULT_STORAGE_RECORD_ID = 1L;
    public static final Long DEFAULT_STORAGE_ITEM_ID = 1L;
    public static final BigDecimal DEFAULT_STORED_IN_TOTAL = new BigDecimal("110.00");

    public static StorageRecord defaultStorageRecord() {
        return StorageRecord.builder()
                .id(DEFAULT_STORAGE_RECORD_ID)
                .date(LocalDate.now())
                .storedInTotal(DEFAULT_STORED_IN_TOTAL)
                .storageItems(defaultStorageItemList())
                .build();
    }

    public static StorageRecord secondStorageRecord() {
        return StorageRecord.builder()
                .id(2L)
                .date(LocalDate.now().plusDays(5))
                .storedInTotal(DEFAULT_STORED_IN_TOTAL)
                .storageItems(secondStorageItemList())
                .build();
    }

    public static List<StorageItem> defaultStorageItemList() {
        return List.of(defaultStorageItem(), secondStorageItem());
    }

    public static StorageItem defaultStorageItem() {
        return StorageItem.builder()
                .id(DEFAULT_STORAGE_ITEM_ID)
                .name("item1")
                .currency(Currency.UAH)
                .value(new BigDecimal("10.00"))
                .build();
    }

    public static StorageItem secondStorageItem() {
        return StorageItem.builder()
                .id(2L)
                .name("item2")
                .currency(Currency.UAH)
                .value(new BigDecimal("100.00"))
                .build();
    }

    public static List<StorageItem> secondStorageItemList() {
        return List.of(thirdStorageItem(), fourthStorageItem());
    }

    public static StorageItem thirdStorageItem() {
        return StorageItem.builder()
                .id(3L)
                .name("item3")
                .currency(Currency.UAH)
                .value(new BigDecimal("60.00"))
                .build();
    }

    public static StorageItem fourthStorageItem() {
        return StorageItem.builder()
                .id(4L)
                .name("item4")
                .currency(Currency.UAH)
                .value(new BigDecimal("50.00"))
                .build();
    }

    public static StorageRecordFullDto defaultStorageRecordFullDto() {
        return StorageRecordFullDto.builder()
                .id(DEFAULT_STORAGE_RECORD_ID)
                .date(LocalDate.now())
                .storedInTotal(DEFAULT_STORED_IN_TOTAL)
                .storageItems(defaultStorageItemFullDtoList())
                .build();
    }

    public static StorageRecordFullDto secondStorageRecordFullDto() {
        return StorageRecordFullDto.builder()
                .id(2L)
                .date(LocalDate.now().plusDays(5))
                .storedInTotal(DEFAULT_STORED_IN_TOTAL)
                .storageItems(secondStorageItemFullDtoList())
                .build();
    }

    public static StorageRecordLimitedDto defaultStorageRecordLimitedDto() {
        return StorageRecordLimitedDto.builder()
                .date(LocalDate.now())
                .storageItems(defaultStorageItemLimitedDtoList())
                .build();
    }

    public static List<StorageItemFullDto> defaultStorageItemFullDtoList() {
        return List.of(defaultStorageItemFullDto(), secondStorageItemFullDto());
    }

    public static StorageItemFullDto defaultStorageItemFullDto() {
        return StorageItemFullDto.builder()
                .id(DEFAULT_STORAGE_ITEM_ID)
                .name("item1")
                .currency(Currency.UAH)
                .value(new BigDecimal("10.00"))
                .build();
    }

    public static StorageItemFullDto secondStorageItemFullDto() {
        return StorageItemFullDto.builder()
                .id(2L)
                .name("item2")
                .currency(Currency.UAH)
                .value(new BigDecimal("100.00"))
                .build();
    }

    public static List<StorageItemFullDto> secondStorageItemFullDtoList() {
        return List.of(thirdStorageItemFullDto(), fourthStorageItemFullDto());
    }

    public static StorageItemFullDto thirdStorageItemFullDto() {
        return StorageItemFullDto.builder()
                .id(3L)
                .name("item3")
                .currency(Currency.UAH)
                .value(new BigDecimal("60.00"))
                .build();
    }

    public static StorageItemFullDto fourthStorageItemFullDto() {
        return StorageItemFullDto.builder()
                .id(4L)
                .name("item4")
                .currency(Currency.UAH)
                .value(new BigDecimal("50.00"))
                .build();
    }


    public static List<StorageItemLimitedDto> defaultStorageItemLimitedDtoList() {
        return List.of(defaultStorageItemLimitedDto(), secondStorageItemLimitedDto());
    }

    public static StorageItemLimitedDto defaultStorageItemLimitedDto() {
        return StorageItemLimitedDto.builder()
                .name("item1")
                .currency(Currency.UAH)
                .value(new BigDecimal("10.00"))
                .build();
    }

    public static StorageItemLimitedDto secondStorageItemLimitedDto() {
        return StorageItemLimitedDto.builder()
                .name("item2")
                .currency(Currency.UAH)
                .value(new BigDecimal("100.00"))
                .build();
    }
}
