package yehor.budget.web.dto.full;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
@Data
public class StorageRecordFullDto {
    private Long id;
    private List<StorageItemFullDto> storageItems;
    private LocalDate date;
    private BigDecimal storedInTotal;
}
