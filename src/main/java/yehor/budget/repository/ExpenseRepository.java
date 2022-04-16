package yehor.budget.repository;

import org.springframework.stereotype.Repository;
import yehor.budget.entity.DailyExpense;
import yehor.budget.util.model.Interval;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static yehor.budget.util.Constants.END_DATE;
import static yehor.budget.util.Constants.START_DATE;

@Repository
public class ExpenseRepository {

    private static final List<DailyExpense> DUMMY_EXPENSES = List.of(
            DailyExpense.builder().value(10).date(START_DATE).build(),
            DailyExpense.builder().value(0).date(LocalDate.of(2022, 3, 23)).build(),
            DailyExpense.builder().value(100).date(LocalDate.of(2022, 3, 24)).build(),
            DailyExpense.builder().value(5).date(LocalDate.of(2022, 3, 25)).build(),
            DailyExpense.builder().value(1).date(LocalDate.of(2022, 3, 26)).build(),
            DailyExpense.builder().value(14).date(LocalDate.of(2022, 3, 27)).build(),
            DailyExpense.builder().value(0).date(LocalDate.of(2022, 3, 28)).build(),
            DailyExpense.builder().value(25).date(LocalDate.of(2022, 3, 29)).build(),
            DailyExpense.builder().value(0).date(LocalDate.of(2022, 3, 30)).build(),
            DailyExpense.builder().value(5).date(END_DATE).build()
    );

    public Optional<DailyExpense> findOne(LocalDate date) {
        return DUMMY_EXPENSES.stream()
                .filter(e -> e.getDate().equals(date))
                .findFirst();
    }

    public int findSumInInterval(LocalDate dateFrom, LocalDate dateTo) {
        Interval interval = Interval.of(dateFrom, dateTo);
        return DUMMY_EXPENSES.stream()
                .filter(e -> interval.isWithin(e.getDate()))
                .mapToInt(DailyExpense::getValue)
                .sum();
    }

    public List<DailyExpense> findAllInInterval(LocalDate dateFrom, LocalDate dateTo) {
        Interval interval = Interval.of(dateFrom, dateTo);
        return DUMMY_EXPENSES.stream()
                .filter(entry -> interval.isWithin(entry.getDate()))
                .toList();
    }
}
