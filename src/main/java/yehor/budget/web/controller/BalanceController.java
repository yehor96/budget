package yehor.budget.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import yehor.budget.common.date.DateManager;
import yehor.budget.service.BalanceService;
import yehor.budget.web.dto.full.BalanceRecordFullDto;
import yehor.budget.web.dto.limited.BalanceItemLimitedDto;
import yehor.budget.web.dto.limited.BalanceRecordLimitedDto;

import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/v1/balance")
@RequiredArgsConstructor
@Tag(name = "Balance Controller")
public class BalanceController {

    private final BalanceService balanceService;
    private final DateManager dateManager;

    @GetMapping
    @Operation(summary = "Get latest balance record")
    public BalanceRecordFullDto getLatest() {
        return balanceService.getLatest().orElseThrow(
                () -> new ResponseStatusException(NOT_FOUND, "There are no balance records"));
    }

    @PostMapping
    @Operation(summary = "Save balance")
    public void save(@RequestBody BalanceRecordLimitedDto balanceRecordDto) {
        try {
            dateManager.validateDateAfterStart(balanceRecordDto.getDate());
            validateBalanceItems(balanceRecordDto);
            validateActors(balanceRecordDto);

            balanceService.save(balanceRecordDto);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(BAD_REQUEST, exception.getMessage());
        }
    }

    private void validateBalanceItems(BalanceRecordLimitedDto balanceRecordDto) {
        if (CollectionUtils.isEmpty(balanceRecordDto.getBalanceItems())) {
            throw new IllegalArgumentException("Balance items are not provided");
        }
    }

    private void validateActors(BalanceRecordLimitedDto balanceRecordDto) {
        List<Long> notValidIds = balanceRecordDto.getBalanceItems().stream()
                .map(BalanceItemLimitedDto::getActorId)
                .filter(id -> Objects.isNull(id) || id < 1)
                .toList();
        if (!CollectionUtils.isEmpty(notValidIds)) {
            throw new IllegalArgumentException("Provided actor ids are not valid: " + notValidIds);
        }
    }
}
