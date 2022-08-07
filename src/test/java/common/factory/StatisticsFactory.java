package common.factory;

import lombok.experimental.UtilityClass;
import yehor.budget.common.date.FullMonth;
import yehor.budget.web.dto.MonthlyStatistics;
import yehor.budget.web.dto.PeriodicStatistics;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

import static java.time.Month.JULY;
import static java.time.Month.JUNE;

@UtilityClass
public class StatisticsFactory {

    public static MonthlyStatistics defaultMonthlyStatistics() {
        return MonthlyStatistics.builder()
                .totalExpense(new BigDecimal("136.50"))
                .totalRegular(new BigDecimal("21.00"))
                .totalNonRegular(new BigDecimal("115.50"))
                .categoryToValueMap(Map.of(
                        "Food", new BigDecimal("125.50"),
                        "Meds", new BigDecimal("11.00"))
                )
                .build();
    }

    public static MonthlyStatistics secondMonthlyStatistics() {
        return MonthlyStatistics.builder()
                .totalExpense(new BigDecimal("273.00"))
                .totalRegular(new BigDecimal("42.00"))
                .totalNonRegular(new BigDecimal("230.00"))
                .categoryToValueMap(Map.of(
                        "Food", new BigDecimal("156.00"),
                        "Medication", new BigDecimal("22.00"))
                )
                .build();
    }

    public static MonthlyStatistics emptyMonthStatistics() {
        return MonthlyStatistics.builder()
                .totalExpense(BigDecimal.ZERO)
                .totalRegular(BigDecimal.ZERO)
                .totalNonRegular(BigDecimal.ZERO)
                .categoryToValueMap(Collections.emptyMap())
                .build();
    }

    public static PeriodicStatistics defaultPeriodicStatistics() {
        return PeriodicStatistics.builder()
                .avgMonthlyTotalRegular(new BigDecimal("204.75"))
                .avgMonthlyTotalNonRegular(new BigDecimal("42.00"))
                .avgMonthlyTotalExpense(new BigDecimal("172.75"))
                .totalExpense(new BigDecimal("409.50"))
                .monthToMonthlyStatisticsMap(Map.of(
                        FullMonth.of(JUNE, 2022).toString(), defaultMonthlyStatistics(),
                        FullMonth.of(JULY, 2022).toString(), secondMonthlyStatistics())
                )
                .build();
    }
}
