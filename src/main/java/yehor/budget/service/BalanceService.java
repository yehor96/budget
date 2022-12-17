package yehor.budget.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yehor.budget.entity.BalanceItem;
import yehor.budget.entity.BalanceRecord;
import yehor.budget.repository.BalanceItemRepository;
import yehor.budget.repository.BalanceRecordRepository;
import yehor.budget.web.converter.BalanceConverter;
import yehor.budget.web.dto.full.BalanceRecordFullDto;
import yehor.budget.web.dto.limited.BalanceRecordLimitedDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceService {

    private final BalanceItemRepository balanceItemRepository;
    private final BalanceRecordRepository balanceRecordRepository;
    private final BalanceConverter balanceConverter;

    public Optional<BalanceRecordFullDto> getLatest() {
        Pageable pageable = PageRequest.of(0, 1, Sort.Direction.DESC, "date");
        Slice<BalanceRecord> balanceRecords = balanceRecordRepository.findAll(pageable);
        if (balanceRecords.isEmpty()) {
            return Optional.empty();
        }
        BalanceRecordFullDto balanceRecordDto = balanceConverter.convert(balanceRecords.toList().get(0));
        setTotalBalance(balanceRecordDto);
        return Optional.of(balanceRecordDto);
    }

    @Transactional
    public void save(BalanceRecordLimitedDto balanceRecordDto) {
        BalanceRecord balanceRecord = balanceConverter.convert(balanceRecordDto);
        BalanceRecord savedBalanceRecord = balanceRecordRepository.save(balanceRecord);

        List<BalanceItem> balanceItems = balanceConverter.convert(
                balanceRecordDto.getBalanceItems(), savedBalanceRecord);
        balanceItems.forEach(balanceItemRepository::save);
    }

    private void setTotalBalance(BalanceRecordFullDto balanceRecordDto) {
        BigDecimal total = balanceRecordDto.getBalanceItems().stream()
                .map(item -> item.getCard().add(item.getCash()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        balanceRecordDto.setTotal(total);
    }
}
