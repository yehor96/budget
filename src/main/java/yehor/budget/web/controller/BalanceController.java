package yehor.budget.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import yehor.budget.common.date.DateManager;
import yehor.budget.common.exception.ObjectNotFoundException;
import yehor.budget.service.recording.BalanceRecordingService;
import yehor.budget.web.dto.full.BalanceRecordFullDto;
import yehor.budget.web.dto.limited.BalanceRecordLimitedDto;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/v1/balance")
@RequiredArgsConstructor
@Tag(name = "Balance Controller")
public class BalanceController {

    private final BalanceRecordingService balanceRecordingService;
    private final DateManager dateManager;

    @GetMapping
    @Operation(summary = "Get latest balance record")
    public BalanceRecordFullDto getLatest() {
        return balanceRecordingService.getLatest().orElseThrow(
                () -> new ResponseStatusException(NOT_FOUND, "There are no balance records"));
    }

    @PostMapping
    @Operation(summary = "Save balance")
    public ResponseEntity<BalanceRecordLimitedDto> save(@RequestBody BalanceRecordLimitedDto balanceRecordDto) {
        try {
            dateManager.validateDateAfterStart(balanceRecordDto.getDate());
            validateBalanceItems(balanceRecordDto);
            balanceRecordingService.save(balanceRecordDto);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(BAD_REQUEST, exception.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    @Operation(summary = "Delete balance record")
    public ResponseEntity<BalanceRecordLimitedDto> delete(@RequestParam("id") Long id) {
        try {
            balanceRecordingService.delete(id);
        } catch (ObjectNotFoundException exception) {
            throw new ResponseStatusException(NOT_FOUND, exception.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/interval")
    @Operation(summary = "Get list of balance records within dates interval")
    public ResponseEntity<List<BalanceRecordFullDto>> getBalanceRecordsInInterval(@RequestParam("dateFrom") String dateFromParam,
                                                                                  @RequestParam("dateTo") String dateToParam) {
        try {
            LocalDate dateFrom = dateManager.parse(dateFromParam);
            LocalDate dateTo = dateManager.parse(dateToParam);

            dateManager.validateDatesInSequentialOrder(dateFrom, dateTo);
            dateManager.validateDatesWithinBudget(dateFrom, dateTo);

            List<BalanceRecordFullDto> balanceRecords = balanceRecordingService.findAllInInterval(dateFrom, dateTo);
            return new ResponseEntity<>(balanceRecords, HttpStatus.OK);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(BAD_REQUEST, exception.getMessage());
        }
    }

    private void validateBalanceItems(BalanceRecordLimitedDto balanceRecordDto) {
        if (CollectionUtils.isEmpty(balanceRecordDto.getBalanceItems())) {
            throw new IllegalArgumentException("Balance items are not provided");
        }
    }
}
