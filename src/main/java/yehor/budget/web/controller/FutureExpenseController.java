package yehor.budget.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import yehor.budget.common.exception.ObjectAlreadyExistsException;
import yehor.budget.common.exception.ObjectNotFoundException;
import yehor.budget.service.FutureExpenseService;
import yehor.budget.web.dto.full.FutureExpenseFullDto;
import yehor.budget.web.dto.limited.FutureExpenseLimitedDto;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/v1/future-expenses")
@RequiredArgsConstructor
@Tag(name = "Future Expense Controller")
/*
    Class not covered with unit tests
 */
public class FutureExpenseController {

    private final FutureExpenseService futureExpenseService;

    @GetMapping
    @Operation(summary = "Get all future expenses")
    public List<FutureExpenseFullDto> getAllFutureExpenses() {
        return futureExpenseService.getAll();
    }

    @PostMapping
    @Operation(summary = "Save future expense")
    public ResponseEntity<FutureExpenseFullDto> saveFutureExpense(@RequestBody FutureExpenseLimitedDto futureExpenseDto) {
        try {
            FutureExpenseFullDto saved = futureExpenseService.save(futureExpenseDto);
            return new ResponseEntity<>(saved, HttpStatus.OK);
        } catch (ObjectAlreadyExistsException exception) {
            throw new ResponseStatusException(BAD_REQUEST, exception.getMessage());
        }
    }

    @DeleteMapping
    @Operation(summary = "Delete future expense by id")
    public ResponseEntity<FutureExpenseLimitedDto> deleteFutureExpense(@RequestParam("id") Long id) {
        try {
            futureExpenseService.delete(id);
        } catch (ObjectNotFoundException exception) {
            throw new ResponseStatusException(NOT_FOUND, exception.getMessage());
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(BAD_REQUEST, exception.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
