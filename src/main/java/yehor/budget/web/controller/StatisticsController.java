package yehor.budget.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yehor.budget.common.date.DateManager;
import yehor.budget.common.date.FullMonth;
import yehor.budget.service.StatisticsService;
import yehor.budget.web.dto.MonthlyStatistics;
import yehor.budget.web.dto.PeriodicStatistics;

import java.time.Month;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
@Tag(name = "Statistics Controller")
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final DateManager dateManager;

    @GetMapping("/monthly")
    @Operation(summary = "Get statistics for one month")
    public MonthlyStatistics getMonthlyStatistics(@RequestParam("month") Month month,
                                                  @RequestParam("year") Integer year) {
        FullMonth fullMonth = FullMonth.of(month, year);

        dateManager.validateMonthWithinBudget(fullMonth);

        return statisticsService.getMonthlyStatistics(fullMonth);
    }

    @GetMapping("/periodic")
    @Operation(summary = "Get statistics for a period of a few months")
    public PeriodicStatistics getPeriodicStatistics(@RequestParam("startMonth") Month startMonth,
                                                    @RequestParam("startYear") Integer startYear,
                                                    @RequestParam("endMonth") Month endMonth,
                                                    @RequestParam("endYear") Integer endYear) {
        FullMonth startFullMonth = FullMonth.of(startMonth, startYear);
        FullMonth endFullMonth = FullMonth.of(endMonth, endYear);

        dateManager.validateMonthsInSequentialOrder(startFullMonth, endFullMonth);
        dateManager.validateMonthWithinBudget(startFullMonth);
        dateManager.validateMonthWithinBudget(endFullMonth);

        return statisticsService.getPeriodicStatistics(startFullMonth, endFullMonth);
    }
}
