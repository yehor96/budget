package yehor.budget.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yehor.budget.service.ExpenseService;
import yehor.budget.util.IntervalUtil;
import yehor.budget.web.dto.DailyExpenseDto;

import java.time.LocalDate;
import java.util.List;

import static yehor.budget.util.Constants.END_DATE;
import static yehor.budget.util.Constants.START_DATE;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private static final IntervalUtil INTERVAL_UTIL = new IntervalUtil();

    private final ExpenseService expenseService;

    @GetMapping
    public DailyExpenseDto getDailyExpense(@RequestParam("date") String dateParam) {
        LocalDate date = LocalDate.parse(dateParam);
        if (!INTERVAL_UTIL.isWithinBudget(date)) {
            throw new IllegalArgumentException(
                    "Date is out of budget period. Start date is " + START_DATE + ", end date is " + END_DATE);
        }

        return expenseService.findByDate(date);
    }

    @GetMapping("/interval")
    public List<DailyExpenseDto> getExpensesInInterval(@RequestParam("dateFrom") String dateFromParam,
                                                       @RequestParam("dateTo") String dateToParam) {
        LocalDate dateFrom = LocalDate.parse(dateFromParam);
        LocalDate dateTo = LocalDate.parse(dateToParam);
        if (!INTERVAL_UTIL.areWithinBudget(dateFrom, dateTo)) {
            throw new IllegalArgumentException(
                    "Date is out of budget period. Start date is " + START_DATE + ", end date is " + END_DATE);
        }

        return expenseService.findAllInInterval(dateFrom, dateTo);
    }

    @GetMapping("/sum")
    public int getExpensesSumInInterval(@RequestParam("dateFrom") String dateFromParam,
                                                     @RequestParam("dateTo") String dateToParam) {
        LocalDate dateFrom = LocalDate.parse(dateFromParam);
        LocalDate dateTo = LocalDate.parse(dateToParam);
        if (!INTERVAL_UTIL.areWithinBudget(dateFrom, dateTo)) {
            throw new IllegalArgumentException(
                    "Date is out of budget period. Start date is " + START_DATE + ", end date is " + END_DATE);
        }

        return expenseService.findSumInInterval(dateFrom, dateTo);
    }

}
