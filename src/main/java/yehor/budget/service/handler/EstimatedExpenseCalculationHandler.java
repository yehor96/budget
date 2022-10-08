package yehor.budget.service.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import yehor.budget.entity.Expense;
import yehor.budget.entity.RowEstimatedExpense;
import yehor.budget.repository.CategoryRepository;
import yehor.budget.repository.ExpenseRepository;
import yehor.budget.repository.RowEstimatedExpenseRepository;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.groupingBy;

@RequiredArgsConstructor
@Component
public class EstimatedExpenseCalculationHandler {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final RowEstimatedExpenseRepository rowEstimatedExpenseRepository;

    @PostConstruct
    public void init() {
        new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(
                getTask(),
                1,
                1, //Duration.ofHours(24).toMinutes(),
                TimeUnit.MINUTES);
    }

    private Runnable getTask() {
        return new EstimatedExpenseCalculationTask(expenseRepository, categoryRepository, rowEstimatedExpenseRepository);
    }

    @RequiredArgsConstructor
    private static class EstimatedExpenseCalculationTask implements Runnable {

        private static final Logger LOG = LogManager.getLogger(EstimatedExpenseCalculationTask.class);

        private final ExpenseRepository expenseRepository;
        private final CategoryRepository categoryRepository;
        private final RowEstimatedExpenseRepository rowEstimatedExpenseRepository;

        @Override
        @Transactional
        public void run() {
            LOG.info("Calculation of estimated expenses started");
            Map<Long, List<Expense>> categoryIdToExpenseMap = expenseRepository
                    .findAllInInterval(LocalDate.now().minusYears(1), LocalDate.now()) //todo make this range modifiable (task)
                    .stream().collect(groupingBy(expense -> expense.getCategory().getId()));

            try {
                for (var entry : categoryIdToExpenseMap.entrySet()) {
                    Long id = entry.getKey();

                    Map<Days, BigDecimal> daysToSum = new EnumMap<>(Days.class);
                    for (var expense : entry.getValue()) {
                        Days bucket = Days.getBucket(expense.getDate().getDayOfMonth());
                        BigDecimal existingValue = daysToSum.get(bucket);
                        if (Objects.isNull(existingValue)) {
                            daysToSum.put(bucket, expense.getValue());
                        } else {
                            daysToSum.put(bucket, existingValue.add(expense.getValue()));
                        }
                    }

                    RowEstimatedExpense rowEstimatedExpense = RowEstimatedExpense.builder()
                            .days1to7(Optional.ofNullable(daysToSum.get(Days.DAYS_1_TO_7)).orElse(BigDecimal.ZERO))
                            .days8to14(Optional.ofNullable(daysToSum.get(Days.DAYS_8_TO_14)).orElse(BigDecimal.ZERO))
                            .days15to21(Optional.ofNullable(daysToSum.get(Days.DAYS_15_TO_21)).orElse(BigDecimal.ZERO))
                            .days22to31(Optional.ofNullable(daysToSum.get(Days.DAYS_22_TO_31)).orElse(BigDecimal.ZERO))
                            .category(categoryRepository.getById(id))
                            .build();
                    LOG.info("Saving row for estimated expenses: {}", rowEstimatedExpense);
                    if (rowEstimatedExpenseRepository.existsByCategoryId(id)) {
                        rowEstimatedExpenseRepository.updateByCategoryId(rowEstimatedExpense);
                    } else {
                        rowEstimatedExpenseRepository.save(rowEstimatedExpense);
                    }
                }
            } catch (Exception e) {
                LOG.info("Exception: ", e);
            }
            LOG.info("Calculation of estimated expenses finished");
        }
    }

    private enum Days {
        DAYS_1_TO_7(List.of(1, 2, 3, 4, 5, 6, 7)),
        DAYS_8_TO_14(List.of(8, 9, 10, 11, 12, 13, 14)),
        DAYS_15_TO_21(List.of(15, 16, 17, 18, 19, 20, 21)),
        DAYS_22_TO_31(List.of(22, 23, 24, 25, 26, 27, 28, 29, 30, 31));

        @Getter
        private final List<Integer> range;

        Days(List<Integer> range) {
            this.range = range;
        }

        public static Days getBucket(Integer day) {
            if (DAYS_1_TO_7.getRange().contains(day)) {
                return DAYS_1_TO_7;
            } else if (DAYS_8_TO_14.getRange().contains(day)) {
                return DAYS_8_TO_14;
            } else if (DAYS_15_TO_21.getRange().contains(day)) {
                return DAYS_15_TO_21;
            } else if (DAYS_22_TO_31.getRange().contains(day)) {
                return DAYS_22_TO_31;
            } else {
                throw new RuntimeException("Illegal day value");
            }
        }
    }
}
