package yehor.budget.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yehor.budget.date.DateManager;
import yehor.budget.date.FullMonth;
import yehor.budget.entity.Expense;
import yehor.budget.helper.CalculatorHelper;
import yehor.budget.repository.ExpenseRepository;
import yehor.budget.web.dto.MonthlyStatistics;
import yehor.budget.web.dto.PeriodStatistics;

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

    public PeriodStatistics getPeriodStatistics(FullMonth startFullMonth, FullMonth endFullMonth) {
        PeriodStatistics periodStatistics = new PeriodStatistics();
        Map<FullMonth, MonthlyStatistics> monthToMonthlyStatisticsMap =
                getMonthToMonthlyStatisticsMap(startFullMonth, endFullMonth);

        List<BigDecimal> totalRegulars = new ArrayList<>();
        List<BigDecimal> totalNonRegulars = new ArrayList<>();
        List<BigDecimal> totalExpenses = new ArrayList<>();

        for (MonthlyStatistics statistics : monthToMonthlyStatisticsMap.values()) {
            totalRegulars.add(statistics.getTotalRegular());
            totalNonRegulars.add(statistics.getTotalNonRegular());
            totalExpenses.add(statistics.getTotalExpense());
        }

        periodStatistics.setAvgMonthlyTotalRegular(calculatorHelper.average(totalRegulars));
        periodStatistics.setAvgMonthlyTotalNonRegular(calculatorHelper.average(totalNonRegulars));
        periodStatistics.setAvgMonthlyTotalExpense(calculatorHelper.average(totalExpenses));
        periodStatistics.setMonthToMonthlyStatisticsMap(monthToMonthlyStatisticsMap);
        periodStatistics.setTotalExpense(calculatorHelper.sum(totalExpenses));

        return periodStatistics;
    }

    private Map<FullMonth, MonthlyStatistics> getMonthToMonthlyStatisticsMap(FullMonth startFullMonth, FullMonth endFullMonth) {
        List<FullMonth> monthsList = dateManager.getMonthsListIn(startFullMonth, endFullMonth);
        Map<FullMonth, MonthlyStatistics> monthToMonthlyStatisticsMap = new LinkedHashMap<>();
        //TODO add new getMonthlyStatistics to make sure there is only one request to db:
        monthsList.forEach(fullMonth -> monthToMonthlyStatisticsMap.put(fullMonth, getMonthlyStatistics(fullMonth)));
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
