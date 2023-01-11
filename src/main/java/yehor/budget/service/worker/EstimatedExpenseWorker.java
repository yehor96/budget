package yehor.budget.service.worker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import yehor.budget.common.SettingsListener;
import yehor.budget.common.date.DateManager;
import yehor.budget.common.date.MonthWeek;
import yehor.budget.common.date.FullMonth;
import yehor.budget.common.util.CalculatorHelper;
import yehor.budget.common.util.WaiterUtil;
import yehor.budget.entity.Expense;
import yehor.budget.entity.RowEstimatedExpense;
import yehor.budget.entity.Settings;
import yehor.budget.repository.CategoryRepository;
import yehor.budget.repository.ExpenseRepository;
import yehor.budget.repository.RowEstimatedExpenseRepository;
import yehor.budget.service.SettingsService;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static java.math.BigDecimal.ZERO;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.reducing;

@RequiredArgsConstructor
@Component
@Slf4j
public class EstimatedExpenseWorker implements SettingsListener {

    public static final Pattern EXPECTED_EXPENSE_END_DATE_SCOPE_PATTERN = Pattern.compile("\\d+[dMy]$");

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final RowEstimatedExpenseRepository rowEstimatedExpenseRepository;
    private final CalculatorHelper calculatorHelper;
    private final SettingsService settingsService;
    private final DateManager dateManager;

    ScheduledThreadPoolExecutor executor;
    int currentInitDelay;
    int currentPeriod;
    String currentEstimationScopePattern;

    @PostConstruct
    private void init() {
        Settings settings = settingsService.getSettingsEntity();
        startTask(
                settings.getEstimatedExpenseWorkerInitDelay(),
                settings.getEstimatedExpenseWorkerPeriod(),
                settings.getEstimatedExpenseWorkerEndDateScopePattern()
        );
    }

    @Override
    public void onUpdate(Settings settings) {
        int initialDelay = settings.getEstimatedExpenseWorkerInitDelay();
        int period = settings.getEstimatedExpenseWorkerPeriod();
        String estimationScopePattern = settings.getEstimatedExpenseWorkerEndDateScopePattern();
        if (currentInitDelay != initialDelay
                || currentPeriod != period
                || estimationScopePatternNeedsUpdating(estimationScopePattern)) {
            executor.shutdown();
            WaiterUtil.waitFor(() -> executor.isShutdown(), Duration.ofMillis(500));
            startTask(initialDelay, period, estimationScopePattern);
        }
    }

    private void startTask(int initialDelay, int period, String estimationScopePattern) {
        currentInitDelay = initialDelay;
        currentPeriod = period;
        if (isEstimationScopePatternValid(estimationScopePattern)) {
            currentEstimationScopePattern = estimationScopePattern;
        }
        log.info("Starting estimated expense worker with initialDelay: " + initialDelay + "m and period: " + period + "m");
        executor = new ScheduledThreadPoolExecutor(1);
        executor.scheduleAtFixedRate(
                new EstimatedExpenseTask(),
                initialDelay,
                period,
                TimeUnit.MINUTES);
    }

    private boolean estimationScopePatternNeedsUpdating(String estimationScopePattern) {
        return isEstimationScopePatternValid(estimationScopePattern)
                && !currentEstimationScopePattern.equals(estimationScopePattern);
    }

    private boolean isEstimationScopePatternValid(String estimationScopePattern) {
        return EXPECTED_EXPENSE_END_DATE_SCOPE_PATTERN.matcher(estimationScopePattern).matches() ||
                dateManager.isValidLocalDatePattern(estimationScopePattern);
    }

    class EstimatedExpenseTask implements Runnable {
        @Override
        @Transactional
        public void run() {
            log.info("Calculation of estimated expenses started");
            try {
                List<Expense> expenses = expenseRepository.findAllRegularInInterval(
                        getEndDateForEstimation(), LocalDate.now());
                long numOfMonthsUnderCalculation = expenses.stream()
                        .map(e -> FullMonth.of(e.getDate()))
                        .distinct().count();
                Map<Long, List<Expense>> categoryIdToExpenseMap = expenses.stream().collect(
                        groupingBy(expense -> expense.getCategory().getId()));

                for (var categoryExpenses : categoryIdToExpenseMap.entrySet()) {
                    Long categoryId = categoryExpenses.getKey();

                    Map<MonthWeek, BigDecimal> weeksListMap = categoryExpenses.getValue().stream()
                            .collect(groupingBy(
                                    expense -> MonthWeek.of(expense.getDate()),
                                    mapping(Expense::getValue, reducing(ZERO, BigDecimal::add))));

                    setAvgValues(weeksListMap, numOfMonthsUnderCalculation);
                    RowEstimatedExpense rowEstimatedExpense = getRowOfEstimatedExpenses(categoryId, weeksListMap);
                    saveRowToDatabase(rowEstimatedExpense);
                }
            } catch (Exception e) {
                log.error("Exception is thrown during calculation of estimated expenses.", e);
            }
            log.info("Calculation of estimated expenses finished");
        }
    }

    private LocalDate getEndDateForEstimation() {
        try {
            if (currentEstimationScopePattern.endsWith("d")) {
                return LocalDate.now().minusDays(getNumberFromPattern());
            } else if (currentEstimationScopePattern.endsWith("M")) {
                return LocalDate.now().minusMonths(getNumberFromPattern());
            } else if (currentEstimationScopePattern.endsWith("y")) {
                return LocalDate.now().minusYears(getNumberFromPattern());
            } else {
                // if all previous do not match, pattern value should be a valid LocalDate represented as String
                return LocalDate.parse(currentEstimationScopePattern);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Illegal estimation end date scope pattern provided: "
            + currentEstimationScopePattern);
        }
    }

    private Long getNumberFromPattern() {
        return Long.parseLong(currentEstimationScopePattern.substring(0, currentEstimationScopePattern.length() - 1));
    }

    private void setAvgValues(Map<MonthWeek, BigDecimal> weeksListMap, long divider) {
        BigDecimal dividerDecimal = new BigDecimal(divider);
        for (MonthWeek period : MonthWeek.values()) {
            BigDecimal existingValue = weeksListMap.getOrDefault(period, ZERO);
            weeksListMap.replace(period, calculatorHelper.divide(existingValue, dividerDecimal));
        }
    }

    private RowEstimatedExpense getRowOfEstimatedExpenses(Long categoryId, Map<MonthWeek, BigDecimal> daysToSum) {
        BigDecimal days1to7 = daysToSum.get(MonthWeek.DAYS_1_TO_7);
        BigDecimal days8to14 = daysToSum.get(MonthWeek.DAYS_8_TO_14);
        BigDecimal days15to21 = daysToSum.get(MonthWeek.DAYS_15_TO_21);
        BigDecimal days22to31 = daysToSum.get(MonthWeek.DAYS_22_TO_31);
        return RowEstimatedExpense.builder()
                .days1to7(Optional.ofNullable(days1to7).orElse(ZERO))
                .days8to14(Optional.ofNullable(days8to14).orElse(ZERO))
                .days15to21(Optional.ofNullable(days15to21).orElse(ZERO))
                .days22to31(Optional.ofNullable(days22to31).orElse(ZERO))
                .category(categoryRepository.getById(categoryId))
                .build();
    }

    private void saveRowToDatabase(RowEstimatedExpense row) {
        log.info("Saving row for estimated expenses: {}", row);
        if (rowEstimatedExpenseRepository.existsByCategoryId(row.getCategory().getId())) {
            rowEstimatedExpenseRepository.updateByCategoryId(row);
        } else {
            rowEstimatedExpenseRepository.save(row);
        }
    }

}
