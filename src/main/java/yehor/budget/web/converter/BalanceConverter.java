package yehor.budget.web.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import yehor.budget.entity.recording.BalanceItem;
import yehor.budget.entity.recording.BalanceRecord;
import yehor.budget.web.dto.full.BalanceItemFullDto;
import yehor.budget.web.dto.full.BalanceRecordFullDto;
import yehor.budget.web.dto.limited.BalanceItemLimitedDto;
import yehor.budget.web.dto.limited.BalanceRecordLimitedDto;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BalanceConverter {

    public BalanceRecord convert(BalanceRecordLimitedDto dto) {
        BalanceRecord balanceRecord = BalanceRecord.builder()
                .date(dto.getDate())
                .build();
        List<BalanceItem> balanceItems = convert(dto.getBalanceItems(), balanceRecord);
        balanceRecord.setBalanceItems(balanceItems);
        return balanceRecord;
    }

    public List<BalanceItem> convert(List<BalanceItemLimitedDto> itemDtos, BalanceRecord balanceRecord) {
        return itemDtos.stream()
                .map(item -> convert(item, balanceRecord))
                .toList();
    }

    public BalanceItem convert(BalanceItemLimitedDto dto, BalanceRecord balanceRecord) {
        return BalanceItem.builder()
                .balanceRecord(balanceRecord)
                .card(dto.getCard())
                .cash(dto.getCash())
                .itemName(dto.getItemName())
                .build();
    }

    public BalanceRecordFullDto convert(BalanceRecord balanceRecord) {
        return BalanceRecordFullDto.builder()
                .id(balanceRecord.getId())
                .date(balanceRecord.getDate())
                .balanceItems(convert(balanceRecord.getBalanceItems()))
                .build();
    }

    public List<BalanceItemFullDto> convert(List<BalanceItem> items) {
        return items.stream()
                .map(this::convert)
                .toList();
    }

    public BalanceItemFullDto convert(BalanceItem item) {
        return BalanceItemFullDto.builder()
                .id(item.getId())
                .card(item.getCard())
                .cash(item.getCash())
                .itemName(item.getItemName())
                .build();
    }
}
