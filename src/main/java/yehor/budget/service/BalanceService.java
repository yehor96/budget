package yehor.budget.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import yehor.budget.common.Currency;
import yehor.budget.common.date.DateManager;
import yehor.budget.common.date.MonthWeek;
import yehor.budget.entity.BalanceItem;
import yehor.budget.entity.BalanceRecord;
import yehor.budget.repository.ActorRepository;
import yehor.budget.repository.BalanceItemRepository;
import yehor.budget.repository.BalanceRecordRepository;
import yehor.budget.web.converter.BalanceConverter;
import yehor.budget.web.dto.full.BalanceEstimateDto;
import yehor.budget.web.dto.full.BalanceRecordFullDto;
import yehor.budget.web.dto.full.EstimatedExpenseFullDto;
import yehor.budget.web.dto.limited.BalanceItemLimitedDto;
import yehor.budget.web.dto.limited.BalanceRecordLimitedDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceService {

    private final BalanceItemRepository balanceItemRepository;
    private final BalanceRecordRepository balanceRecordRepository;
    private final BalanceConverter balanceConverter;
    private final ActorRepository actorRepository;
    private final IncomeSourceService incomeSourceService;
    private final EstimatedExpenseService estimatedExpenseService;
    private final DateManager dateManager;

    @Transactional(readOnly = true)
    public Optional<BalanceRecordFullDto> getLatest() {
        Pageable pageable = PageRequest.of(0, 1, Sort.Direction.DESC, "date");
        Page<BalanceRecord> balanceRecords = balanceRecordRepository.findAll(pageable);
        if (balanceRecords.isEmpty()) {
            return Optional.empty();
        }
        BalanceRecord balanceRecord = balanceRecords.toList().get(0);
        BalanceRecordFullDto balanceRecordDto = balanceConverter.convert(balanceRecord);
        setTotalBalance(balanceRecordDto);
        setBalanceEstimates(balanceRecord, balanceRecordDto);
        return Optional.of(balanceRecordDto);
    }

    @Transactional
    public void save(BalanceRecordLimitedDto balanceRecordDto) {
        validateActorsExist(balanceRecordDto);
        BalanceRecord balanceRecord = balanceConverter.convert(balanceRecordDto);
        setCurrentIncomesAndExpenses(balanceRecord);
        BalanceRecord savedBalanceRecord = balanceRecordRepository.save(balanceRecord);

        List<BalanceItem> balanceItems = balanceConverter.convert(
                balanceRecordDto.getBalanceItems(), savedBalanceRecord);
        balanceItems.forEach(balanceItemRepository::save);
    }

    BigDecimal getExpensesForFullWeeksLeftInCurrentMonth(MonthWeek currentMonthWeek,
                                                         Map<MonthWeek, BigDecimal> estimatedExpensePerWeek) {
        List<MonthWeek> monthWeeksAfter = currentMonthWeek.getMonthWeeksAfter();
        return estimatedExpensePerWeek.entrySet().stream()
                .filter(weekExpense -> monthWeeksAfter.contains(weekExpense.getKey()))
                .map(Map.Entry::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    BigDecimal getExpensesForDaysLeftInCurrentWeek(MonthWeek currentMonthWeek, LocalDate currentDate,
                                                   Map<MonthWeek, BigDecimal> estimatedExpensePerWeek) {
        int lastDayOfCurrentWeek;
        if (currentMonthWeek == MonthWeek.DAYS_22_TO_31) {
            lastDayOfCurrentWeek = dateManager.getLastDayOfMonthByDate(currentDate);
        } else {
            lastDayOfCurrentWeek = currentMonthWeek.getRange().get(currentMonthWeek.getRange().size() - 1);
        }
        int daysLeftInWeek = lastDayOfCurrentWeek - currentDate.getDayOfMonth();
        BigDecimal expensesForCurrentWeek = estimatedExpensePerWeek.get(currentMonthWeek);
        return expensesForCurrentWeek
                .divide(BigDecimal.valueOf(7), RoundingMode.CEILING)
                .multiply(BigDecimal.valueOf(daysLeftInWeek));
    }

    private void setBalanceEstimates(BalanceRecord balanceRecord, BalanceRecordFullDto balanceRecordDto) {
        Map<MonthWeek, BigDecimal> estimatedExpensePerWeek = Map.of(
                MonthWeek.DAYS_1_TO_7, balanceRecord.getTotal1to7(),
                MonthWeek.DAYS_8_TO_14, balanceRecord.getTotal8to14(),
                MonthWeek.DAYS_15_TO_21, balanceRecord.getTotal15to21(),
                MonthWeek.DAYS_22_TO_31, balanceRecord.getTotal22to31()
        );

        LocalDate currentDate = balanceRecordDto.getDate();
        MonthWeek currentMonthWeek = MonthWeek.of(currentDate);
        BigDecimal expensesForFullWeekLeft = getExpensesForFullWeeksLeftInCurrentMonth(
                currentMonthWeek, estimatedExpensePerWeek);
        BigDecimal expensesForDaysLeftInWeek = getExpensesForDaysLeftInCurrentWeek(
                currentMonthWeek, currentDate, estimatedExpensePerWeek);
        BigDecimal totalExpensesLeftInMonth = expensesForFullWeekLeft.add(expensesForDaysLeftInWeek);

        BalanceEstimateDto balanceEstimateDto = new BalanceEstimateDto(
                balanceRecordDto.getTotal(),
                totalExpensesLeftInMonth,
                balanceRecord.getTotalIncome(),
                dateManager.getMonthEndDate(currentDate));
        balanceRecordDto.setBalanceEstimateDto(balanceEstimateDto);
    }

    private void setCurrentIncomesAndExpenses(BalanceRecord balanceRecord) {
        EstimatedExpenseFullDto estimatedExpenses = estimatedExpenseService.getOne();
        balanceRecord.setTotal1to7(estimatedExpenses.getTotal1to7());
        balanceRecord.setTotal8to14(estimatedExpenses.getTotal8to14());
        balanceRecord.setTotal15to21(estimatedExpenses.getTotal15to21());
        balanceRecord.setTotal22to31(estimatedExpenses.getTotal22to31());

        BigDecimal totalIncome = incomeSourceService.getTotalIncome().getIncomeSources().stream()
                .filter(income -> balanceRecord.getDate().getDayOfMonth() < income.getAccrualDayOfMonth())
                .map(income -> incomeSourceService.getIncomeInCurrency(income, Currency.UAH))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        balanceRecord.setTotalIncome(totalIncome);
    }

    private void setTotalBalance(BalanceRecordFullDto balanceRecordDto) {
        BigDecimal total = balanceRecordDto.getBalanceItems().stream()
                .map(item -> item.getCard().add(item.getCash()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        balanceRecordDto.setTotal(total);
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
