package yehor.budget.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import yehor.budget.common.date.DateManager;
import yehor.budget.common.exception.ObjectAlreadyExistsException;
import yehor.budget.common.exception.ObjectNotFoundException;
import yehor.budget.service.IncomeSourceService;
import yehor.budget.web.dto.TotalIncomeDto;
import yehor.budget.web.dto.full.IncomeSourceFullDto;
import yehor.budget.web.dto.limited.IncomeSourceLimitedDto;

import static java.util.Objects.isNull;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/v1/income-sources")
@RequiredArgsConstructor
@Tag(name = "Income Source Controller")
public class IncomeSourceController {

    private final IncomeSourceService incomeSourceService;
    private final DateManager dateManager;

    @GetMapping
    @Operation(summary = "Get total income source")
    public ResponseEntity<TotalIncomeDto> getTotalIncome() {
        TotalIncomeDto totalIncome = incomeSourceService.getTotalIncome();
        return new ResponseEntity<>(totalIncome, HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Save income source")
    public ResponseEntity<IncomeSourceFullDto> saveIncomeSource(@RequestBody IncomeSourceLimitedDto incomeSourceDto) {
        try {
            if (isNull(incomeSourceDto.getAccrualDayOfMonth())) {
                incomeSourceDto.setAccrualDayOfMonth(1);
            }
            dateManager.validateDayOfMonth(incomeSourceDto.getAccrualDayOfMonth());
            IncomeSourceFullDto saved = incomeSourceService.save(incomeSourceDto);
            return new ResponseEntity<>(saved, HttpStatus.OK);
        } catch (ObjectAlreadyExistsException | IllegalArgumentException exception) {
            throw new ResponseStatusException(BAD_REQUEST, exception.getMessage());
        }
    }

    @DeleteMapping
    @Operation(summary = "Delete income source by id")
    public ResponseEntity<IncomeSourceLimitedDto> deleteIncomeSource(@RequestParam("id") Long id) {
        try {
            incomeSourceService.delete(id);
        } catch (ObjectNotFoundException exception) {
            throw new ResponseStatusException(NOT_FOUND, exception.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping
    @Operation(summary = "Update income source by id")
    public ResponseEntity<IncomeSourceFullDto> updateIncomeSource(@RequestBody IncomeSourceFullDto incomeSourceDto) {
        try {
            IncomeSourceFullDto updated = incomeSourceService.update(incomeSourceDto);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (ObjectNotFoundException exception) {
            throw new ResponseStatusException(NOT_FOUND, exception.getMessage());
        }
    }
}
