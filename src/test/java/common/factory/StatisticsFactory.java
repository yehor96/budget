package common.factory;

import lombok.experimental.UtilityClass;
import yehor.budget.common.date.FullMonth;
import yehor.budget.web.dto.MonthlyStatistics;
import yehor.budget.web.dto.PeriodicStatistics;

import java.math.BigDecimal;
import java.util.Map;

import static java.time.Month.JULY;
import static java.time.Month.JUNE;

@UtilityClass
public class StatisticsFactory {

    public static MonthlyStatistics defaultMonthlyStatistics() {
        return MonthlyStatistics.builder()
                .totalExpense(new BigDecimal("100.00"))
                .totalRegular(new BigDecimal("60.00"))
                .totalNonRegular(new BigDecimal("40.00"))
                .categoryToValueMap(Map.of(
                        "Food", new BigDecimal("30.00"),
                        "Medication", new BigDecimal("60.00"),
                        "Transport", new BigDecimal("10.00"))
                )
                .build();
    }

    public static MonthlyStatistics secondMonthlyStatistics() {
        return MonthlyStatistics.builder()
                .totalExpense(new BigDecimal("50.00"))
                .totalRegular(new BigDecimal("40.00"))
                .totalNonRegular(new BigDecimal("10.00"))
                .categoryToValueMap(Map.of(
                        "Food", new BigDecimal("40.00"),
                        "Medication", new BigDecimal("10.00"))
                )
                .build();
    }

    public static PeriodicStatistics defaultPeriodicStatistics() {
        return PeriodicStatistics.builder()
                .avgMonthlyTotalRegular(new BigDecimal("50.00"))
                .avgMonthlyTotalNonRegular(new BigDecimal("25.00"))
                .avgMonthlyTotalExpense(new BigDecimal("75.00"))
                .totalExpense(new BigDecimal("150.00"))
                .monthToMonthlyStatisticsMap(Map.of(
                        FullMonth.of(JUNE, 2022).toString(), defaultMonthlyStatistics(),
                        FullMonth.of(JULY, 2022).toString(), secondMonthlyStatistics())
                )
                .build();
    }
}
