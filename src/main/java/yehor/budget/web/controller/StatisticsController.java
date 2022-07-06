package yehor.budget.web.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yehor.budget.date.DateManager;
import yehor.budget.date.FullMonth;
import yehor.budget.service.StatisticsService;
import yehor.budget.web.dto.MonthlyStatistics;

import java.time.Month;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
@Tag(name = "Statistics Controller")
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final DateManager dateManager;

    @GetMapping("/monthly")
    public MonthlyStatistics getMonthlyStatistics(@RequestParam("month") Month month,
                                                  @RequestParam("year") Integer year) {
        FullMonth fullMonth = FullMonth.of(month, year);
        dateManager.validateMonthWithinBudget(fullMonth);
        return statisticsService.getMonthlyStatistics(fullMonth);
    }
}
