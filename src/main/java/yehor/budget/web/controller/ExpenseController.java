package yehor.budget.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yehor.budget.common.date.DateManager;
import yehor.budget.service.ExpenseService;
import yehor.budget.web.dto.full.ExpenseFullDto;
import yehor.budget.web.dto.limited.ExpenseLimitedDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static yehor.budget.web.exception.CategoryExceptionProvider.invalidCategoryIdException;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
@Tag(name = "Expense Controller")
public class ExpenseController {

    private final DateManager dateManager;
    private final ExpenseService expenseService;

    @GetMapping
    @Operation(summary = "Get expense by id")
    public ResponseEntity<ExpenseFullDto> getExpense(@RequestParam("id") Long id) {
        ExpenseFullDto expenseDto = expenseService.findById(id);
        return new ResponseEntity<>(expenseDto, HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Save expense")
    public ResponseEntity<ExpenseLimitedDto> saveExpense(@RequestBody ExpenseLimitedDto expenseDto) {
        dateManager.validateDateAfterStart(expenseDto.getDate());
        validateCategoryId(expenseDto.getCategoryId());

        expenseService.save(expenseDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping
    @Operation(summary = "Update expense by id")
    public ResponseEntity<ExpenseFullDto> updateExpense(@RequestBody ExpenseFullDto expenseDto) {
        dateManager.validateDateAfterStart(expenseDto.getDate());
        validateCategoryId(expenseDto.getCategoryId());

        expenseService.updateById(expenseDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/interval")
    @Operation(summary = "Get list of expenses within dates interval")
    public ResponseEntity<List<ExpenseFullDto>> getExpensesInInterval(@RequestParam("dateFrom") String dateFromParam,
                                                                  @RequestParam("dateTo") String dateToParam) {
        LocalDate dateFrom = dateManager.parse(dateFromParam);
        LocalDate dateTo = dateManager.parse(dateToParam);

        dateManager.validateDatesInSequentialOrder(dateFrom, dateTo);
        dateManager.validateDatesWithinBudget(dateFrom, dateTo);

        List<ExpenseFullDto> expenseDtoList = expenseService.findAllInInterval(dateFrom, dateTo);
        return new ResponseEntity<>(expenseDtoList, HttpStatus.OK);
    }

    @GetMapping("/sum")
    @Operation(summary = "Get sum of expenses within dates interval")
    public ResponseEntity<BigDecimal> getExpensesSumInInterval(@RequestParam("dateFrom") String dateFromParam,
                                                               @RequestParam("dateTo") String dateToParam) {
        LocalDate dateFrom = dateManager.parse(dateFromParam);
        LocalDate dateTo = dateManager.parse(dateToParam);

        dateManager.validateDatesInSequentialOrder(dateFrom, dateTo);
        dateManager.validateDatesWithinBudget(dateFrom, dateTo);

        BigDecimal sum = expenseService.findSumInInterval(dateFrom, dateTo);
        return new ResponseEntity<>(sum, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete expense by id")
    public ResponseEntity<ExpenseFullDto> deleteExpense(@PathVariable("id") Long id) {
        expenseService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void validateCategoryId(Long categoryId) {
        if (Objects.isNull(categoryId) || categoryId < 1) {
            throw invalidCategoryIdException(categoryId);
        }
    }

}
