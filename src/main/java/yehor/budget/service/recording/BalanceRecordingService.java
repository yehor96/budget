package yehor.budget.service.recording;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yehor.budget.common.exception.ObjectNotFoundException;
import yehor.budget.common.util.PageableHelper;
import yehor.budget.entity.recording.BalanceRecord;
import yehor.budget.repository.recording.BalanceItemRepository;
import yehor.budget.repository.recording.BalanceRecordRepository;
import yehor.budget.repository.recording.IncomeSourceRecordRepository;
import yehor.budget.service.EstimatedExpenseService;
import yehor.budget.service.IncomeSourceService;
import yehor.budget.web.converter.BalanceConverter;
import yehor.budget.web.converter.IncomeSourceConverter;
import yehor.budget.web.dto.full.BalanceEstimateDto;
import yehor.budget.web.dto.full.BalanceRecordFullDto;
import yehor.budget.web.dto.full.EstimatedExpenseFullDto;
import yehor.budget.web.dto.limited.BalanceRecordLimitedDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceRecordingService {

    private final BalanceItemRepository balanceItemRepository;
    private final BalanceRecordRepository balanceRecordRepository;
    private final BalanceConverter balanceConverter;
    private final IncomeSourceService incomeSourceService;
    private final EstimatedExpenseService estimatedExpenseService;
    private final PageableHelper pageableHelper;
    private final IncomeSourceRecordRepository incomeSourceRecordRepository;
    private final IncomeSourceConverter incomeSourceConverter;
    private final BalanceEstimationService balanceEstimationService;

    @Transactional(readOnly = true)
    public Optional<BalanceRecordFullDto> getLatest() {
        Optional<BalanceRecord> latestOpt = pageableHelper.getLatestByDate(balanceRecordRepository);
        return latestOpt.map(this::calculateFullBalanceRecord);
    }

    @Transactional
    public void save(BalanceRecordLimitedDto balanceRecordDto) {
        validateRecordWithDateNotExists(balanceRecordDto.getDate());
        BalanceRecord balanceRecord = balanceConverter.convert(balanceRecordDto);

        saveEstimatedExpenses(balanceRecord);
        balanceRecordRepository.save(balanceRecord);
        log.info("Saved: {}", balanceRecord);
        saveIncomeSourceRecords(balanceRecord);
        balanceRecord.getBalanceItems().forEach(balanceItemRepository::save);
        log.info("List of saved balance items: {}", balanceRecord.getBalanceItems());
    }

    @Transactional(readOnly = true)
    public List<BalanceRecordFullDto> findAllInInterval(LocalDate dateFrom, LocalDate dateTo) {
        List<BalanceRecord> balanceRecords = balanceRecordRepository.findAllInInterval(dateFrom, dateTo);
        return balanceRecords.stream().map(this::calculateFullBalanceRecord).toList();
    }

    @Transactional
    public void delete(Long id) {
        try {
            balanceRecordRepository.deleteById(id);
            log.info("Balance record with id {} is deleted", id);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Balance with id " + id + " not found");
        }
    }

    private BalanceRecordFullDto calculateFullBalanceRecord(BalanceRecord balanceRecord) {
        BalanceRecordFullDto balanceRecordDto = balanceConverter.convert(balanceRecord);
        setTotalBalance(balanceRecordDto);
        List<BalanceEstimateDto> estimates = balanceEstimationService.getBalanceEstimation(
                balanceRecord, balanceRecordDto.getDate(), balanceRecordDto.getTotalBalance());
        balanceRecordDto.setBalanceEstimates(estimates);
        return balanceRecordDto;
    }

    private void validateRecordWithDateNotExists(LocalDate date) {
        if (balanceRecordRepository.existsByDate(date)) {
            throw new IllegalArgumentException("Record with provided date " + date + " already exists");
        }
    }

    private void saveIncomeSourceRecords(BalanceRecord balanceRecord) {
        incomeSourceService.getTotalIncome().getIncomeSources().stream()
                .map(incomeSource -> incomeSourceConverter.convert(incomeSource, balanceRecord))
                .forEach(incSourceRec -> {
                    incomeSourceRecordRepository.save(incSourceRec);
                    log.info("Saved: {}", incSourceRec);
                });
    }

    private void saveEstimatedExpenses(BalanceRecord balanceRecord) {
        EstimatedExpenseFullDto estimatedExpenses = estimatedExpenseService.getOne();
        balanceRecord.setTotal1to7(estimatedExpenses.getTotal1to7());
        balanceRecord.setTotal8to14(estimatedExpenses.getTotal8to14());
        balanceRecord.setTotal15to21(estimatedExpenses.getTotal15to21());
        balanceRecord.setTotal22to31(estimatedExpenses.getTotal22to31());
    }

    private void setTotalBalance(BalanceRecordFullDto balanceRecordDto) {
        BigDecimal total = balanceRecordDto.getBalanceItems().stream()
                .map(item -> item.getCard().add(item.getCash()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        balanceRecordDto.setTotalBalance(total);
    }
}
