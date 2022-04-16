package yehor.budget.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yehor.budget.service.ExpenseService;
import yehor.budget.util.DatesManager;
import yehor.budget.web.dto.DailyExpenseDto;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final DatesManager datesManager;
    private final ExpenseService expenseService;

    @GetMapping
    public DailyExpenseDto getDailyExpense(@RequestParam("date") String dateParam) {
        LocalDate date = LocalDate.parse(dateParam);
        datesManager.validateDate(date);
        return expenseService.findByDate(date);
    }

    @GetMapping("/interval")
    public List<DailyExpenseDto> getExpensesInInterval(@RequestParam("dateFrom") String dateFromParam,
                                                       @RequestParam("dateTo") String dateToParam) {
        LocalDate dateFrom = LocalDate.parse(dateFromParam);
        LocalDate dateTo = LocalDate.parse(dateToParam);
        datesManager.validateDates(dateFrom, dateTo);
        return expenseService.findAllInInterval(dateFrom, dateTo);
    }

    @GetMapping("/sum")
    public int getExpensesSumInInterval(@RequestParam("dateFrom") String dateFromParam,
                                        @RequestParam("dateTo") String dateToParam) {
        LocalDate dateFrom = LocalDate.parse(dateFromParam);
        LocalDate dateTo = LocalDate.parse(dateToParam);
        datesManager.validateDates(dateFrom, dateTo);
        return expenseService.findSumInInterval(dateFrom, dateTo);
    }

}
