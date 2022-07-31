package yehor.budget.service;

import org.junit.jupiter.api.Test;
import yehor.budget.common.date.DateManager;
import yehor.budget.common.date.FullMonth;
import yehor.budget.entity.Category;
import yehor.budget.entity.Expense;
import yehor.budget.common.helper.CalculatorHelper;
import yehor.budget.repository.ExpenseRepository;
import yehor.budget.web.dto.MonthlyStatistics;
import yehor.budget.web.dto.PeriodicStatistics;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StatisticsServiceTest {

    private final ExpenseRepository expenseRepositoryMock = mock(ExpenseRepository.class);
    private final DateManager dateManagerMock = mock(DateManager.class);
    private final CalculatorHelper calculatorHelperMock = mock(CalculatorHelper.class);

    private final StatisticsService statisticsService = new StatisticsService(
            expenseRepositoryMock, dateManagerMock, calculatorHelperMock);

    @Test
    void testGetMonthlyStatistics() {
        FullMonth fullMonth = FullMonth.of(Month.JULY, 2022);
        LocalDate date1 = LocalDate.of(2022, 7, 1);
        LocalDate date2 = LocalDate.of(2022, 7, 15);
        LocalDate date3 = LocalDate.of(2022, 7, 31);

        Category category1 = Category.builder().id(1L).name("Food").build();
        Category category2 = Category.builder().id(2L).name("Meds").build();

        Expense expense1 = Expense.builder().id(1L).date(date1).value(BigDecimal.TEN).isRegular(true).category(category1).build();
        Expense expense2 = Expense.builder().id(1L).date(date2).value(BigDecimal.TEN).isRegular(true).category(category1).build();
        Expense expense3 = Expense.builder().id(1L).date(date3).value(BigDecimal.TEN).isRegular(false).category(category2).build();

        when(expenseRepositoryMock.findAllInInterval(any(), any())).thenReturn(List.of(expense1, expense2, expense3));

        MonthlyStatistics expectedMonthlyStatistics = MonthlyStatistics.builder()
                .totalExpense(BigDecimal.valueOf(30))
                .totalRegular(BigDecimal.valueOf(20))
                .totalNonRegular(BigDecimal.TEN)
                .categoryToValueMap(Map.of("Food", BigDecimal.valueOf(20), "Meds", BigDecimal.TEN))
                .build();

        MonthlyStatistics actualMonthlyStatistics = statisticsService.getMonthlyStatistics(fullMonth);

        assertEquals(actualMonthlyStatistics, expectedMonthlyStatistics);
    }

    @Test
    void testGetMonthlyStatisticsEmptyMonth() {
        FullMonth fullMonth = FullMonth.of(Month.JULY, 2022);

        when(expenseRepositoryMock.findAllInInterval(any(), any())).thenReturn(Collections.emptyList());

        MonthlyStatistics expectedMonthlyStatistics = MonthlyStatistics.builder()
                .totalExpense(BigDecimal.ZERO)
                .totalRegular(BigDecimal.ZERO)
                .totalNonRegular(BigDecimal.ZERO)
                .categoryToValueMap(Collections.emptyMap())
                .build();

        MonthlyStatistics actualMonthlyStatistics = statisticsService.getMonthlyStatistics(fullMonth);

        assertEquals(actualMonthlyStatistics, expectedMonthlyStatistics);
    }

    @Test
    void testGetPeriodicStatistics() {
        FullMonth july = FullMonth.of(Month.JULY, 2022);
        FullMonth august = FullMonth.of(Month.AUGUST, 2022);
        FullMonth september = FullMonth.of(Month.SEPTEMBER, 2022);
        LocalDate date1 = LocalDate.of(2022, 7, 1);
        LocalDate date2 = LocalDate.of(2022, 7, 15);
        LocalDate date3 = LocalDate.of(2022, 7, 31);
        LocalDate date4 = LocalDate.of(2022, 8, 1);
        LocalDate date5 = LocalDate.of(2022, 9, 15);

        Category category1 = Category.builder().id(1L).name("Food").build();
        Category category2 = Category.builder().id(2L).name("Meds").build();

        Expense expense1 = Expense.builder().id(1L).date(date1).value(BigDecimal.TEN).isRegular(true).category(category1).build();
        Expense expense2 = Expense.builder().id(1L).date(date2).value(BigDecimal.TEN).isRegular(true).category(category1).build();
        Expense expense3 = Expense.builder().id(1L).date(date3).value(BigDecimal.TEN).isRegular(false).category(category2).build();
        Expense expense4 = Expense.builder().id(1L).date(date4).value(BigDecimal.TEN).isRegular(true).category(category1).build();
        Expense expense5 = Expense.builder().id(1L).date(date5).value(BigDecimal.TEN).isRegular(false).category(category2).build();

        when(expenseRepositoryMock.findAllInInterval(any(), any()))
                .thenReturn(List.of(expense1, expense2, expense3))
                .thenReturn(Collections.singletonList(expense4))
                .thenReturn(Collections.singletonList(expense5));
        when(calculatorHelperMock.average(any()))
                .thenReturn(BigDecimal.TEN)
                .thenReturn(BigDecimal.valueOf(6.5))
                .thenReturn(BigDecimal.valueOf(16.5));
        when(calculatorHelperMock.sum(any()))
                .thenReturn(BigDecimal.valueOf(50));
        when(dateManagerMock.getMonthsListIn(any(), any()))
                .thenReturn(List.of(july, august, september));

        MonthlyStatistics monthlyStatistics1 = MonthlyStatistics.builder()
                .totalExpense(BigDecimal.valueOf(30))
                .totalRegular(BigDecimal.valueOf(20))
                .totalNonRegular(BigDecimal.TEN)
                .categoryToValueMap(Map.of("Food", BigDecimal.valueOf(20), "Meds", BigDecimal.TEN))
                .build();
        MonthlyStatistics monthlyStatistics2 = MonthlyStatistics.builder()
                .totalExpense(BigDecimal.TEN)
                .totalRegular(BigDecimal.TEN)
                .totalNonRegular(BigDecimal.ZERO)
                .categoryToValueMap(Map.of("Food", BigDecimal.TEN))
                .build();
        MonthlyStatistics monthlyStatistics3 = MonthlyStatistics.builder()
                .totalExpense(BigDecimal.TEN)
                .totalRegular(BigDecimal.ZERO)
                .totalNonRegular(BigDecimal.TEN)
                .categoryToValueMap(Map.of("Meds", BigDecimal.TEN))
                .build();

        PeriodicStatistics expectedPeriodicStatistics = PeriodicStatistics.builder()
                .monthToMonthlyStatisticsMap(Map.of(
                        july.toString(), monthlyStatistics1,
                        august.toString(), monthlyStatistics2,
                        september.toString(), monthlyStatistics3)
                )
                .totalExpense(BigDecimal.valueOf(50))
                .avgMonthlyTotalRegular(BigDecimal.TEN)
                .avgMonthlyTotalNonRegular(BigDecimal.valueOf(6.5))
                .avgMonthlyTotalExpense(BigDecimal.valueOf(16.5))
                .build();

        PeriodicStatistics actualPeriodicStatistics = statisticsService.getPeriodicStatistics(july, september);

        assertEquals(actualPeriodicStatistics, expectedPeriodicStatistics);
    }

    @Test
    void testGetPeriodicStatisticsEmptyMonths() {
        FullMonth july = FullMonth.of(Month.JULY, 2022);
        FullMonth august = FullMonth.of(Month.AUGUST, 2022);
        FullMonth september = FullMonth.of(Month.SEPTEMBER, 2022);

        when(expenseRepositoryMock.findAllInInterval(any(), any())).thenReturn(Collections.emptyList());
        when(calculatorHelperMock.average(any())).thenReturn(BigDecimal.ZERO);
        when(calculatorHelperMock.sum(any())).thenReturn(BigDecimal.ZERO);
        when(dateManagerMock.getMonthsListIn(any(), any())).thenReturn(List.of(july, august, september));

        MonthlyStatistics monthlyStatistics1 = MonthlyStatistics.builder()
                .totalExpense(BigDecimal.ZERO)
                .totalRegular(BigDecimal.ZERO)
                .totalNonRegular(BigDecimal.ZERO)
                .categoryToValueMap(Collections.emptyMap())
                .build();
        MonthlyStatistics monthlyStatistics2 = MonthlyStatistics.builder()
                .totalExpense(BigDecimal.ZERO)
                .totalRegular(BigDecimal.ZERO)
                .totalNonRegular(BigDecimal.ZERO)
                .categoryToValueMap(Collections.emptyMap())
                .build();
        MonthlyStatistics monthlyStatistics3 = MonthlyStatistics.builder()
                .totalExpense(BigDecimal.ZERO)
                .totalRegular(BigDecimal.ZERO)
                .totalNonRegular(BigDecimal.ZERO)
                .categoryToValueMap(Collections.emptyMap())
                .build();

        PeriodicStatistics expectedPeriodicStatistics = PeriodicStatistics.builder()
                .monthToMonthlyStatisticsMap(Map.of(
                        july.toString(), monthlyStatistics1,
                        august.toString(), monthlyStatistics2,
                        september.toString(), monthlyStatistics3)
                )
                .totalExpense(BigDecimal.ZERO)
                .avgMonthlyTotalRegular(BigDecimal.ZERO)
                .avgMonthlyTotalNonRegular(BigDecimal.ZERO)
                .avgMonthlyTotalExpense(BigDecimal.ZERO)
                .build();

        PeriodicStatistics actualPeriodicStatistics = statisticsService.getPeriodicStatistics(july, september);

        assertEquals(actualPeriodicStatistics, expectedPeriodicStatistics);
    }
}