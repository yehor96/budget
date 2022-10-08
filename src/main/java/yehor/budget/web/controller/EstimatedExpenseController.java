package yehor.budget.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yehor.budget.service.EstimatedExpenseService;
import yehor.budget.web.dto.full.EstimatedExpenseFullDto;

@RestController
@RequestMapping("/api/v1/estimated-expenses")
@RequiredArgsConstructor
@Tag(name = "Estimated Expense Controller")
public class EstimatedExpenseController {

    private final EstimatedExpenseService estimatedExpenseService;

    @GetMapping
    @Operation(summary = "Get estimated expenses")
    public ResponseEntity<EstimatedExpenseFullDto> getOne() {
        EstimatedExpenseFullDto estimatedExpenseFullDto = estimatedExpenseService.getOne();
        return new ResponseEntity<>(estimatedExpenseFullDto, HttpStatus.OK);
    }
}
