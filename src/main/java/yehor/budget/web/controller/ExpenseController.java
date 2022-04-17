package yehor.budget.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yehor.budget.service.ExpenseService;
import yehor.budget.manager.date.DateManager;
import yehor.budget.web.dto.DailyExpenseDto;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final DateManager dateManager;
    private final ExpenseService expenseService;

    @GetMapping
    public DailyExpenseDto getDailyExpense(@RequestParam("date") String dateParam) {
        LocalDate date = LocalDate.parse(dateParam);

        dateManager.validateDateWithinBudget(date);

        return expenseService.findByDate(date);
    }

    @PostMapping
    public void addDailyExpense(@RequestBody DailyExpenseDto dailyExpenseDto) {
        dateManager.validateDateAfterStart(dailyExpenseDto.getDate());

        expenseService.addOne(dailyExpenseDto);
    }

    @GetMapping("/interval")
    public List<DailyExpenseDto> getExpensesInInterval(@RequestParam("dateFrom") String dateFromParam,
                                                       @RequestParam("dateTo") String dateToParam) {
        LocalDate dateFrom = LocalDate.parse(dateFromParam);
        LocalDate dateTo = LocalDate.parse(dateToParam);

        dateManager.validateDatesInSequentialOrder(dateFrom, dateTo);
        dateManager.validateDatesWithinBudget(dateFrom, dateTo);

        return expenseService.findAllInInterval(dateFrom, dateTo);
    }

    @GetMapping("/sum")
    public int getExpensesSumInInterval(@RequestParam("dateFrom") String dateFromParam,
                                        @RequestParam("dateTo") String dateToParam) {
        LocalDate dateFrom = LocalDate.parse(dateFromParam);
        LocalDate dateTo = LocalDate.parse(dateToParam);

        dateManager.validateDatesWithinBudget(dateFrom, dateTo);

        return expenseService.findSumInInterval(dateFrom, dateTo);
    }

}
