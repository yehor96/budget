package yehor.budget.repository;

import org.springframework.stereotype.Repository;
import yehor.budget.entity.DailyExpense;
import yehor.budget.manager.date.DateManager;
import yehor.budget.manager.date.Interval;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ExpenseRepository {

    private static final List<DailyExpense> DUMMY_EXPENSES = new ArrayList<>(List.of(
            DailyExpense.builder().value(10).date(DateManager.START_DATE).build(),
            DailyExpense.builder().value(0).date(LocalDate.of(2022, 3, 23)).build(),
            DailyExpense.builder().value(100).date(LocalDate.of(2022, 3, 24)).build(),
            DailyExpense.builder().value(5).date(LocalDate.of(2022, 3, 25)).build(),
            DailyExpense.builder().value(1).date(LocalDate.of(2022, 3, 26)).build(),
            DailyExpense.builder().value(14).date(LocalDate.of(2022, 3, 27)).build(),
            DailyExpense.builder().value(0).date(LocalDate.of(2022, 3, 28)).build(),
            DailyExpense.builder().value(25).date(LocalDate.of(2022, 3, 29)).build(),
            DailyExpense.builder().value(0).date(LocalDate.of(2022, 3, 30)).build(),
            DailyExpense.builder().value(5).date(DateManager.getEndDate()).build()
    ));

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

    public void addOne(DailyExpense expense) {
        DUMMY_EXPENSES.stream()
                .filter(element -> element.getDate().equals(expense.getDate()))
                .findFirst()
                .ifPresentOrElse(element -> element.setValue(expense.getValue()),
                        () -> processNewDate(expense));
    }

    //TODO will be removed after adding actual database
    private void processNewDate(DailyExpense expense) {
        Interval newDatesInterval = Interval.of(DateManager.getEndDate(), expense.getDate().minusDays(1));

        LocalDate newDate = DateManager.getEndDate().plusDays(1);
        while(newDatesInterval.isWithin(newDate)) {
            DUMMY_EXPENSES.add(new DailyExpense(0, newDate));
            newDate = newDate.plusDays(1);
        }
        DUMMY_EXPENSES.add(expense);
        DateManager.setEndDate(expense.getDate());
    }
}
