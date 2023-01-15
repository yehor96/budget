package yehor.budget.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yehor.budget.common.date.DateManager;
import yehor.budget.common.date.FullMonth;
import yehor.budget.entity.Expense;
import yehor.budget.common.util.CalculatorHelper;
import yehor.budget.repository.ExpenseRepository;
import yehor.budget.web.dto.MonthlyStatistics;
import yehor.budget.web.dto.PeriodicStatistics;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final ExpenseRepository expenseRepository;
    private final DateManager dateManager;
    private final CalculatorHelper calculatorHelper;

    public MonthlyStatistics getMonthlyStatistics(FullMonth fullMonth) {
        LocalDate firstDay = LocalDate.of(fullMonth.getYear(), fullMonth.getMonth(), 1);
        LocalDate lastDay = LocalDate.of(fullMonth.getYear(), fullMonth.getMonth(), fullMonth.getMonth().length(Year.isLeap(fullMonth.getYear())));
        List<Expense> expenses = expenseRepository.findAllInInterval(firstDay, lastDay);

        MonthlyStatistics statistics = new MonthlyStatistics();
        for (var expense : expenses) {
            calculateTotalExpense(statistics, expense);
            calculateRegulars(statistics, expense);
        }
        calculateCategoryTotals(statistics, expenses);
        return statistics;
    }

    public PeriodicStatistics getPeriodicStatistics(FullMonth startFullMonth, FullMonth endFullMonth) {
        PeriodicStatistics periodicStatistics = new PeriodicStatistics();
        Map<String, MonthlyStatistics> monthToMonthlyStatisticsMap =
                getMonthToMonthlyStatisticsMap(startFullMonth, endFullMonth);

        List<BigDecimal> totalRegulars = new ArrayList<>();
        List<BigDecimal> totalNonRegulars = new ArrayList<>();
        List<BigDecimal> totalExpenses = new ArrayList<>();

        for (MonthlyStatistics statistics : monthToMonthlyStatisticsMap.values()) {
            totalRegulars.add(statistics.getTotalRegular());
            totalNonRegulars.add(statistics.getTotalNonRegular());
            totalExpenses.add(statistics.getTotalExpense());
        }

        periodicStatistics.setAvgMonthlyTotalRegular(calculatorHelper.average(totalRegulars));
        periodicStatistics.setAvgMonthlyTotalNonRegular(calculatorHelper.average(totalNonRegulars));
        periodicStatistics.setAvgMonthlyTotalExpense(calculatorHelper.average(totalExpenses));
        periodicStatistics.setMonthToMonthlyStatisticsMap(monthToMonthlyStatisticsMap);
        periodicStatistics.setTotalExpense(calculatorHelper.sum(totalExpenses));

        return periodicStatistics;
    }

    private Map<String, MonthlyStatistics> getMonthToMonthlyStatisticsMap(FullMonth startFullMonth, FullMonth endFullMonth) {
        List<FullMonth> monthsList = dateManager.getMonthsListIn(startFullMonth, endFullMonth);
        Map<String, MonthlyStatistics> monthToMonthlyStatisticsMap = new LinkedHashMap<>();
        monthsList.forEach(fullMonth -> monthToMonthlyStatisticsMap.put(fullMonth.toString(), getMonthlyStatistics(fullMonth)));
        return monthToMonthlyStatisticsMap;
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

    private void calculateCategoryTotals(MonthlyStatistics statistics, List<Expense> expenses) {
        Map<String, BigDecimal> categoryToValueMap = expenses.stream().collect(
                toMap(e -> e.getCategory().getName(), Expense::getValue, BigDecimal::add));
        statistics.setCategoryToValueMap(categoryToValueMap);
    }

}
