package yehor.budget.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yehor.budget.date.FullMonth;
import yehor.budget.entity.Expense;
import yehor.budget.repository.ExpenseRepository;
import yehor.budget.web.dto.MonthlyStatistics;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final ExpenseRepository expenseRepository;

    public MonthlyStatistics getMonthlyStatistics(FullMonth fullMonth) {
        LocalDate firstDay = LocalDate.of(fullMonth.getYear(), fullMonth.getMonth(), 1);
        LocalDate lastDay = LocalDate.of(fullMonth.getYear(), fullMonth.getMonth(), fullMonth.getMonth().length(Year.isLeap(fullMonth.getYear())));
        List<Expense> expenses = expenseRepository.findAllInInterval(firstDay, lastDay);

        MonthlyStatistics statistics = new MonthlyStatistics();
        for (var expense : expenses) {
            calculateTotalExpense(statistics, expense);
            calculateRegulars(statistics, expense);
        }
        return statistics;
    }

    private void calculateTotalExpense(MonthlyStatistics statistics, Expense expense) {
        statistics.setTotalExpense(statistics.getTotalExpense().add(expense.getValue()));
    }

    private void calculateRegulars(MonthlyStatistics statistics, Expense expense) {
        if (Boolean.TRUE.equals(expense.getIsRegular())) {
            statistics.setTotalRegular(statistics.getTotalRegular().add(expense.getValue()));
        } else {
            statistics.setTotalNonRegular(statistics.getTotalNonRegular().add(expense.getValue()));
        }
    }
}
