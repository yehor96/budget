package yehor.budget.service.recording;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yehor.budget.common.exception.ObjectNotFoundException;
import yehor.budget.common.util.PageableHelper;
import yehor.budget.entity.recording.BalanceItem;
import yehor.budget.entity.recording.BalanceRecord;
import yehor.budget.entity.recording.ExpectedExpenseRecord;
import yehor.budget.entity.recording.IncomeSourceRecord;
import yehor.budget.repository.recording.BalanceItemRepository;
import yehor.budget.repository.recording.BalanceRecordRepository;
import yehor.budget.repository.recording.ExpectedExpenseRecordRepository;
import yehor.budget.repository.recording.IncomeSourceRecordRepository;
import yehor.budget.service.EstimatedExpenseService;
import yehor.budget.service.IncomeSourceService;
import yehor.budget.web.converter.BalanceConverter;
import yehor.budget.web.converter.EstimatedExpenseConverter;
import yehor.budget.web.converter.IncomeSourceConverter;
import yehor.budget.web.dto.full.BalanceEstimateDto;
import yehor.budget.web.dto.full.BalanceRecordFullDto;
import yehor.budget.web.dto.full.BalanceRecordFullDtoWithoutEstimates;
import yehor.budget.web.dto.full.EstimatedExpenseFullDto;
import yehor.budget.web.dto.limited.BalanceRecordLimitedDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
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
    private final EstimatedExpenseConverter estimatedExpenseConverter;
    private final ExpectedExpenseRecordRepository expectedExpenseRecordRepository;

    @Transactional(readOnly = true)
    public Optional<BalanceRecordFullDto> getLatest() {
        Optional<BalanceRecord> latestOpt = pageableHelper.getLatestByDate(balanceRecordRepository);
        return latestOpt.map(this::calculateFullBalanceRecord);
    }

    @Transactional
    public BalanceRecordFullDtoWithoutEstimates save(BalanceRecordLimitedDto balanceRecordDto) {
        validateRecordWithDateNotExists(balanceRecordDto.getDate());
        BalanceRecord balanceRecord = balanceConverter.convert(balanceRecordDto);

        BalanceRecord savedRecord = balanceRecordRepository.save(balanceRecord);
        saveIncomeSourceRecords(balanceRecord);
        saveEstimatedExpenses(balanceRecord);

        List<BalanceItem> savedItems = new ArrayList<>();
        savedRecord.getBalanceItems().forEach(item -> savedItems.add(balanceItemRepository.save(item)));
        savedRecord.setBalanceItems(savedItems);

        BalanceRecordFullDtoWithoutEstimates saved = balanceConverter.convertToDtoWithNoEstimates(savedRecord);
        log.info("Saved: {}", savedRecord);
        return saved;
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
        List<IncomeSourceRecord> incomeSourceRecords = new ArrayList<>();
        incomeSourceService.getTotalIncome().getIncomeSources().stream()
                .map(existingIncomeSource -> {
                    existingIncomeSource.setId(null); // in order to create new entity with the same fields but new id
                    return existingIncomeSource;
                })
                .map(incomeSource -> incomeSourceConverter.convert(incomeSource, balanceRecord))
                .forEach(incSourceRec -> {
                    incomeSourceRecordRepository.save(incSourceRec);
                    incomeSourceRecords.add(incSourceRec);
                    log.info("Saved: {}", incSourceRec);
                });
        balanceRecord.setIncomeSourceRecords(incomeSourceRecords);
    }

    private void saveEstimatedExpenses(BalanceRecord balanceRecord) {
        EstimatedExpenseFullDto estimatedExpenses = estimatedExpenseService.getOne();
        ExpectedExpenseRecord expectedExpenseRecord = estimatedExpenseConverter.convert(estimatedExpenses, balanceRecord);
        balanceRecord.setExpectedExpenseRecord(expectedExpenseRecord);
        expectedExpenseRecordRepository.save(expectedExpenseRecord);
    }

    private void setTotalBalance(BalanceRecordFullDto balanceRecordDto) {
        BigDecimal total = balanceRecordDto.getBalanceItems().stream()
                .map(item -> item.getCard().add(item.getCash()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        balanceRecordDto.setTotalBalance(total);
    }
}
