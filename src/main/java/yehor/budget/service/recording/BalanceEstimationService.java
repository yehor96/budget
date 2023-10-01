package yehor.budget.service.recording;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yehor.budget.common.Currency;
import yehor.budget.common.date.DateManager;
import yehor.budget.common.date.MonthWeek;
import yehor.budget.entity.FutureExpense;
import yehor.budget.entity.recording.BalanceRecord;
import yehor.budget.entity.recording.ExpectedExpenseRecord;
import yehor.budget.repository.FutureExpenseRepository;
import yehor.budget.service.client.currency.CurrencyRateService;
import yehor.budget.web.dto.full.BalanceEstimateDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BalanceEstimationService {

    private static final Currency CURRENCY_FOR_ESTIMATION = Currency.UAH;
    private static final int NUMBER_OF_MONTH_TO_ESTIMATE_FOR = 3;

    private final DateManager dateManager;
    private final CurrencyRateService currencyRateService;
    private final FutureExpenseRepository futureExpenseRepository;

    public List<BalanceEstimateDto> getBalanceEstimation(BalanceRecord balanceRecord,
                                                         LocalDate currentDate,
                                                         BigDecimal currentTotal) {
        ExpectedExpenseRecord expectedExpenseRecord = balanceRecord.getExpectedExpenseRecord();
        Map<MonthWeek, BigDecimal> estimatedExpensePerWeek = Map.of(
                MonthWeek.DAYS_1_TO_7, expectedExpenseRecord.getTotal1to7(),
                MonthWeek.DAYS_8_TO_14, expectedExpenseRecord.getTotal8to14(),
                MonthWeek.DAYS_15_TO_21, expectedExpenseRecord.getTotal15to21(),
                MonthWeek.DAYS_22_TO_31, expectedExpenseRecord.getTotal22to31()
        );

        List<BalanceEstimateDto> estimates = new ArrayList<>();
        BalanceEstimateDto balanceEstimateDto = estimateForMonth(
                currentDate, currentTotal, balanceRecord, estimatedExpensePerWeek);
        estimates.add(balanceEstimateDto);
        for (int i = 0; i < NUMBER_OF_MONTH_TO_ESTIMATE_FOR - 1; i++) {
            BalanceEstimateDto estimate = estimateForMonth(
                    estimates.get(estimates.size() - 1).getEndOfMonthDate().plusDays(1),
                    estimates.get(estimates.size() - 1).getProfitByEndOfMonth(),
                    balanceRecord,
                    estimatedExpensePerWeek);
            estimates.add(estimate);
        }
        return estimates;
    }

    BalanceEstimateDto estimateForMonth(LocalDate currentDate,
                                        BigDecimal previousTotal,
                                        BalanceRecord balanceRecord,
                                        Map<MonthWeek, BigDecimal> estimatedExpensePerWeek) {
        BigDecimal totalExpensesLeftInMonth = getExpensesTilEndOfMonth(currentDate, estimatedExpensePerWeek);
        BigDecimal totalIncomesLeftInMonth = getIncomesTilEndOfMonth(currentDate, balanceRecord);
        return new BalanceEstimateDto(
                previousTotal,
                totalExpensesLeftInMonth,
                totalIncomesLeftInMonth,
                dateManager.getLastDateOfMonth(currentDate));
    }

    BigDecimal getIncomesTilEndOfMonth(LocalDate currentDate, BalanceRecord balanceRecord) {
        return balanceRecord.getIncomeSourceRecords().stream()
                .filter(income -> currentDate.getDayOfMonth() < income.getAccrualDay())
                .map(income -> currencyRateService.getValueInCurrency(income, CURRENCY_FOR_ESTIMATION))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    BigDecimal getExpensesTilEndOfMonth(LocalDate currentDate, Map<MonthWeek, BigDecimal> estimatedExpensePerWeek) {
        MonthWeek currentMonthWeek = MonthWeek.of(currentDate);
        BigDecimal expensesForFullWeeksLeftInMonth = getExpensesForFullWeeksLeftTilEndOfMonth(
                currentMonthWeek, estimatedExpensePerWeek);
        BigDecimal expensesForDaysLeftInCurrentWeek = getExpensesForDaysLeftInCurrentWeek(
                currentMonthWeek, currentDate, estimatedExpensePerWeek);
        BigDecimal plannedFutureExpenses = getFutureExpensesTilEndOfMonth(currentDate);
        return expensesForFullWeeksLeftInMonth
                .add(expensesForDaysLeftInCurrentWeek)
                .add(plannedFutureExpenses);
    }

    BigDecimal getFutureExpensesTilEndOfMonth(LocalDate dateFrom) {
        LocalDate dateTo = dateManager.getLastDateOfMonth(dateFrom);
        return futureExpenseRepository.getFutureExpensesInInterval(dateFrom, dateTo).stream()
                .map(FutureExpense::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    BigDecimal getExpensesForFullWeeksLeftTilEndOfMonth(MonthWeek currentMonthWeek,
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
            lastDayOfCurrentWeek = dateManager.getLastDayOfMonth(currentDate);
        } else {
            lastDayOfCurrentWeek = currentMonthWeek.getRange().get(currentMonthWeek.getRange().size() - 1);
        }
        int daysLeftInWeek = lastDayOfCurrentWeek - currentDate.getDayOfMonth();
        BigDecimal expensesForCurrentWeek = estimatedExpensePerWeek.get(currentMonthWeek);
        return expensesForCurrentWeek
                .divide(BigDecimal.valueOf(7), RoundingMode.CEILING)
                .multiply(BigDecimal.valueOf(daysLeftInWeek));
    }
}
