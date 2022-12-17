package yehor.budget.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import yehor.budget.service.BalanceService;
import yehor.budget.web.dto.full.BalanceRecordFullDto;
import yehor.budget.web.dto.limited.BalanceRecordLimitedDto;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/v1/balance")
@RequiredArgsConstructor
@Tag(name = "Balance Controller")
public class BalanceController {

    private final BalanceService balanceService;

    @GetMapping
    @Operation(summary = "Get latest balance")
    public BalanceRecordFullDto getLatest() {
        return balanceService.getLatest().orElseThrow(
                () -> new ResponseStatusException(NOT_FOUND, "There are no balance records"));
    }

    @PostMapping
    @Operation(summary = "Save balance")
    public void save(@RequestBody BalanceRecordLimitedDto balanceRecordDto) {
        balanceService.save(balanceRecordDto);
    }
}
