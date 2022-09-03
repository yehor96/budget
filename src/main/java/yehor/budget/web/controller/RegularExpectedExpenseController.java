package yehor.budget.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yehor.budget.service.RegularExpectedExpenseService;
import yehor.budget.web.dto.full.RegularExpectedExpenseFullDto;

@RestController
@RequestMapping("/api/v1/regular-expected-expenses")
@RequiredArgsConstructor
@Tag(name = "Regular Expense Controller")
public class RegularExpectedExpenseController {

    private final RegularExpectedExpenseService regularExpectedExpenseService;

    @GetMapping
    @Operation(summary = "Get regular expected expenses [Dummy values]")
    public ResponseEntity<RegularExpectedExpenseFullDto> getOne() {
        RegularExpectedExpenseFullDto regularExpectedExpenseFullDto = regularExpectedExpenseService.getOne();
        return new ResponseEntity<>(regularExpectedExpenseFullDto, HttpStatus.OK);
    }
}
