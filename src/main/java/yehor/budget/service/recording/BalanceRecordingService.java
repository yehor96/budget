package yehor.budget.service.recording;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import yehor.budget.common.util.PageableHelper;
import yehor.budget.entity.recording.BalanceRecord;
import yehor.budget.repository.ActorRepository;
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
import yehor.budget.web.dto.limited.BalanceItemLimitedDto;
import yehor.budget.web.dto.limited.BalanceRecordLimitedDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceRecordingService {

    private final BalanceItemRepository balanceItemRepository;
    private final BalanceRecordRepository balanceRecordRepository;
    private final BalanceConverter balanceConverter;
    private final ActorRepository actorRepository;
    private final IncomeSourceService incomeSourceService;
    private final EstimatedExpenseService estimatedExpenseService;
    private final PageableHelper pageableHelper;
    private final IncomeSourceRecordRepository incomeSourceRecordRepository;
    private final IncomeSourceConverter incomeSourceConverter;
    private final BalanceEstimationService balanceEstimationService;

    @Transactional(readOnly = true)
    public Optional<BalanceRecordFullDto> getLatest() {
        Optional<BalanceRecord> latestOpt = pageableHelper.getLatestByDate(balanceRecordRepository);
        if (latestOpt.isEmpty()) {
            return Optional.empty();
        }
        BalanceRecord balanceRecord = latestOpt.get();
        BalanceRecordFullDto balanceRecordDto = balanceConverter.convert(balanceRecord);
        setTotalBalance(balanceRecordDto);
        List<BalanceEstimateDto> estimates = balanceEstimationService.getBalanceEstimation(
                balanceRecord, balanceRecordDto.getDate(), balanceRecordDto.getTotalBalance());
        balanceRecordDto.setBalanceEstimates(estimates);
        return Optional.of(balanceRecordDto);
    }

    @Transactional
    public void save(BalanceRecordLimitedDto balanceRecordDto) {
        validateActorsExist(balanceRecordDto);
        BalanceRecord balanceRecord = balanceConverter.convert(balanceRecordDto);

        //todo these values should be stored in separate repository with expense records
        saveEstimatedExpenses(balanceRecord);

        balanceRecordRepository.save(balanceRecord);
        saveIncomeSourceRecords(balanceRecord);
        balanceRecord.getBalanceItems().forEach(balanceItemRepository::save);
    }

    private void saveIncomeSourceRecords(BalanceRecord balanceRecord) {
        incomeSourceService.getTotalIncome().getIncomeSources().stream()
                .map(incomeSource -> incomeSourceConverter.convert(incomeSource, balanceRecord))
                .forEach(incomeSourceRecordRepository::save);
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

    private void validateActorsExist(BalanceRecordLimitedDto balanceRecordDto) {
        List<Long> notExistingIds = balanceRecordDto.getBalanceItems().stream()
                .map(BalanceItemLimitedDto::getActorId)
                .filter(id -> !actorRepository.existsById(id))
                .toList();
        if (!CollectionUtils.isEmpty(notExistingIds)) {
            throw new IllegalArgumentException("Provided actor ids do not exist: " + notExistingIds);
        }
    }
}
