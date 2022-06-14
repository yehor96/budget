package yehor.budget.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yehor.budget.manager.date.DateManager;
import yehor.budget.service.ExpenseService;
import yehor.budget.web.dto.ExpenseDto;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final DateManager dateManager;
    private final ExpenseService expenseService;

    @GetMapping
    public ResponseEntity<ExpenseDto> getExpense(@RequestParam("date") Long id) {
        ExpenseDto expenseDto = expenseService.findById(id);
        return new ResponseEntity<>(expenseDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ExpenseDto> saveExpense(@RequestBody ExpenseDto expenseDto) {
        dateManager.validateDateAfterStart(expenseDto.getDate());

        expenseService.save(expenseDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseDto> updateExpense(@RequestParam("id") Long id,
                                                    @RequestBody ExpenseDto expenseDto) {
        dateManager.validateDateWithinBudget(expenseDto.getDate());

        expenseService.updateById(id, expenseDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/interval")
    public ResponseEntity<List<ExpenseDto>> getExpensesInInterval(@RequestParam("dateFrom") String dateFromParam,
                                                                  @RequestParam("dateTo") String dateToParam) {
        LocalDate dateFrom = dateManager.parse(dateFromParam);
        LocalDate dateTo = dateManager.parse(dateToParam);

        dateManager.validateDatesInSequentialOrder(dateFrom, dateTo);
        dateManager.validateDatesWithinBudget(dateFrom, dateTo);

        List<ExpenseDto> expenseDtoList = expenseService.findAllInInterval(dateFrom, dateTo);
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

    @DeleteMapping("/{id}")
    public ResponseEntity<ExpenseDto> deleteExpense(@RequestParam("id") Long id) {
        expenseService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
