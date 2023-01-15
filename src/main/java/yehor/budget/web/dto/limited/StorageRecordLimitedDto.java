package yehor.budget.web.dto.limited;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class StorageRecordLimitedDto {
    private List<StorageItemLimitedDto> storageItems;
    private LocalDate date;
}
