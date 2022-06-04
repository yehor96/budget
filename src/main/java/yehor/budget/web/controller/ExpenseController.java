package yehor.budget.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yehor.budget.manager.date.DateManager;
import yehor.budget.service.ExpenseService;
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
    public ResponseEntity<DailyExpenseDto> getDailyExpense(@RequestParam("date") String dateParam) {
        LocalDate date = dateManager.parse(dateParam);

        dateManager.validateDateWithinBudget(date);

        DailyExpenseDto dailyExpenseDto = expenseService.findByDate(date);
        return new ResponseEntity<>(dailyExpenseDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<DailyExpenseDto> saveDailyExpense(@RequestBody DailyExpenseDto dailyExpenseDto) {
        dateManager.validateDateAfterStart(dailyExpenseDto.getDate());

        expenseService.save(dailyExpenseDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<DailyExpenseDto> updateDailyExpense(@RequestBody DailyExpenseDto dailyExpenseDto) {
        dateManager.validateDateWithinBudget(dailyExpenseDto.getDate());

        expenseService.updateByDate(dailyExpenseDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/interval")
    public ResponseEntity<List<DailyExpenseDto>> getExpensesInInterval(@RequestParam("dateFrom") String dateFromParam,
                                                                       @RequestParam("dateTo") String dateToParam) {
        LocalDate dateFrom = dateManager.parse(dateFromParam);
        LocalDate dateTo = dateManager.parse(dateToParam);

        dateManager.validateDatesInSequentialOrder(dateFrom, dateTo);
        dateManager.validateDatesWithinBudget(dateFrom, dateTo);

        List<DailyExpenseDto> expenseDtoList = expenseService.findAllInInterval(dateFrom, dateTo);
        return new ResponseEntity<>(expenseDtoList, HttpStatus.OK);
    }

    @GetMapping("/sum")
    public ResponseEntity<Integer> getExpensesSumInInterval(@RequestParam("dateFrom") String dateFromParam,
                                                            @RequestParam("dateTo") String dateToParam) {
        LocalDate dateFrom = dateManager.parse(dateFromParam);
        LocalDate dateTo = dateManager.parse(dateToParam);

        dateManager.validateDatesWithinBudget(dateFrom, dateTo);

        int sum = expenseService.findSumInInterval(dateFrom, dateTo);
        return new ResponseEntity<>(sum, HttpStatus.OK);
    }

}
