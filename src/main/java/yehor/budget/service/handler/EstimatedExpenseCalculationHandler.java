package yehor.budget.service.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import yehor.budget.common.helper.CalculatorHelper;
import yehor.budget.entity.Expense;
import yehor.budget.entity.RowEstimatedExpense;
import yehor.budget.repository.CategoryRepository;
import yehor.budget.repository.ExpenseRepository;
import yehor.budget.repository.RowEstimatedExpenseRepository;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.math.BigDecimal.ZERO;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.reducing;

@RequiredArgsConstructor
@Component
public class EstimatedExpenseCalculationHandler {

    private static final Logger LOG = LogManager.getLogger(EstimatedExpenseCalculationHandler.class);

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final RowEstimatedExpenseRepository rowEstimatedExpenseRepository;
    private final CalculatorHelper calculatorHelper;

    @PostConstruct
    public void init() {
        new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(
                new EstimatedExpenseCalculationTask(),
                1,
                1,
                TimeUnit.MINUTES);
    }

    @RequiredArgsConstructor
    private class EstimatedExpenseCalculationTask implements Runnable {

        @Override
        @Transactional
        public void run() {
            LOG.info("Calculation of estimated expenses started");
            try {
                List<Expense> expenses = expenseRepository.findAllInInterval(LocalDate.now().minusYears(1), LocalDate.now());
                long numOfMonthsUnderCalculation = expenses.stream().map(e -> e.getDate().getMonth()).distinct().count();
                Map<Long, List<Expense>> categoryIdToExpenseMap = expenses.stream().collect(
                        groupingBy(expense -> expense.getCategory().getId()));

                for (var categoryExpenses : categoryIdToExpenseMap.entrySet()) {
                    Long categoryId = categoryExpenses.getKey();

                    Map<DaysBucket, BigDecimal> daysBucketListMap = categoryExpenses.getValue().stream()
                            .collect(groupingBy(
                                    expense -> DaysBucket.of(expense.getDate()),
                                    mapping(Expense::getValue, reducing(ZERO, BigDecimal::add))));

                    setAvgValues(daysBucketListMap, numOfMonthsUnderCalculation);
                    RowEstimatedExpense rowEstimatedExpense = getRowOfEstimatedExpenses(categoryId, daysBucketListMap);
                    saveRowToDatabase(categoryId, rowEstimatedExpense);
                }
            } catch (Exception e) {
                LOG.error("Exception is thrown during calculation of estimated expenses.", e);
            }
            LOG.info("Calculation of estimated expenses finished");
        }
    }

    private void setAvgValues(Map<DaysBucket, BigDecimal> daysBucketListMap, long divider) {
        BigDecimal dividerDecimal = new BigDecimal(divider);
        for (DaysBucket period : DaysBucket.values()) {
            BigDecimal existingValue = daysBucketListMap.getOrDefault(period, ZERO);
            daysBucketListMap.replace(period, calculatorHelper.average(existingValue, dividerDecimal));
        }
    }

    private RowEstimatedExpense getRowOfEstimatedExpenses(Long categoryId, Map<DaysBucket, BigDecimal> daysToSum) {
        return RowEstimatedExpense.builder()
                .days1to7(Optional.ofNullable(daysToSum.get(DaysBucket.DAYS_1_TO_7)).orElse(ZERO))
                .days8to14(Optional.ofNullable(daysToSum.get(DaysBucket.DAYS_8_TO_14)).orElse(ZERO))
                .days15to21(Optional.ofNullable(daysToSum.get(DaysBucket.DAYS_15_TO_21)).orElse(ZERO))
                .days22to31(Optional.ofNullable(daysToSum.get(DaysBucket.DAYS_22_TO_31)).orElse(ZERO))
                .category(categoryRepository.getById(categoryId))
                .build();
    }

    private void saveRowToDatabase(Long categoryId, RowEstimatedExpense row) {
        LOG.info("Saving row for estimated expenses: {}", row);
        if (rowEstimatedExpenseRepository.existsByCategoryId(categoryId)) {
            rowEstimatedExpenseRepository.updateByCategoryId(row);
        } else {
            rowEstimatedExpenseRepository.save(row);
        }
    }

    private enum DaysBucket {
        DAYS_1_TO_7(List.of(1, 2, 3, 4, 5, 6, 7)),
        DAYS_8_TO_14(List.of(8, 9, 10, 11, 12, 13, 14)),
        DAYS_15_TO_21(List.of(15, 16, 17, 18, 19, 20, 21)),
        DAYS_22_TO_31(List.of(22, 23, 24, 25, 26, 27, 28, 29, 30, 31));

        @Getter
        private final List<Integer> range;

        DaysBucket(List<Integer> range) {
            this.range = range;
        }

        public static DaysBucket of(LocalDate date) {
            Integer day = date.getDayOfMonth();
            if (DAYS_1_TO_7.getRange().contains(day)) {
                return DAYS_1_TO_7;
            } else if (DAYS_8_TO_14.getRange().contains(day)) {
                return DAYS_8_TO_14;
            } else if (DAYS_15_TO_21.getRange().contains(day)) {
                return DAYS_15_TO_21;
            } else if (DAYS_22_TO_31.getRange().contains(day)) {
                return DAYS_22_TO_31;
            } else {
                throw new IllegalArgumentException("Illegal value provided " + date);
            }
        }
    }
}
