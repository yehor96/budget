package yehor.budget.web.dto.full;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
@Data
public class StorageRecordFullDto {
    private final Long id;
    private final List<StorageItemFullDto> storageItems;
    private final LocalDate date;
    private final BigDecimal totalStorage;
}
