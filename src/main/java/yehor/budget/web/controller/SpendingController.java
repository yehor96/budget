package yehor.budget.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yehor.budget.service.SpendingService;
import yehor.budget.util.IntervalUtil;
import yehor.budget.web.dto.SpendingValueDto;

import java.time.LocalDate;

import static yehor.budget.util.Constants.START_DATE;

@RestController
@RequestMapping("/api/v1/spending")
@RequiredArgsConstructor
public class SpendingController {

    private static final IntervalUtil INTERVAL_UTIL = new IntervalUtil();

    private final SpendingService spendingService;

    @GetMapping
    public SpendingValueDto getSpending(@RequestParam("date") String dateParam) {
        LocalDate date = LocalDate.parse(dateParam);
        if (!INTERVAL_UTIL.isWithinBudget(date)) {
            throw new IllegalArgumentException(
                    "Date is out of budget period. Start date is " + START_DATE +
                            ", and current date is " + LocalDate.now());
        }

        return spendingService.findByDate(date);
    }

    @GetMapping("/sum")
    public SpendingValueDto getSpendingSum(@RequestParam("dateFrom") String dateFromParam,
                                           @RequestParam("dateTo") String dateToParam) {
        LocalDate dateFrom = LocalDate.parse(dateFromParam);
        LocalDate dateTo = LocalDate.parse(dateToParam);
        if (!INTERVAL_UTIL.areWithinBudget(dateFrom, dateTo)) {
            throw new IllegalArgumentException(
                    "Date is out of budget period. Start date is " + START_DATE +
                            ", and current date is " + LocalDate.now());
        }

        return spendingService.findSumInInterval(dateFrom, dateTo);
    }

}
