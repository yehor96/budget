package yehor.budget.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.server.ResponseStatusException;
import yehor.budget.common.exception.ObjectAlreadyExistsException;
import yehor.budget.common.exception.ObjectNotFoundException;
import yehor.budget.service.IncomeSourceService;
import yehor.budget.web.dto.TotalIncomeDto;
import yehor.budget.web.dto.full.IncomeSourceFullDto;
import yehor.budget.web.dto.limited.IncomeSourceLimitedDto;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/v1/income-sources")
@RequiredArgsConstructor
@Tag(name = "Income Source Controller")
public class IncomeSourceController {

    private final IncomeSourceService incomeSourceService;

    @GetMapping
    @Operation(summary = "Get total income source")
    public ResponseEntity<TotalIncomeDto> getTotalIncome() {
        TotalIncomeDto totalIncome = incomeSourceService.getTotalIncome();
        return new ResponseEntity<>(totalIncome, HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Save income source")
    public ResponseEntity<IncomeSourceLimitedDto> saveIncomeSource(@RequestBody IncomeSourceLimitedDto incomeSourceDto) {
        try {
            incomeSourceService.save(incomeSourceDto);
        } catch (ObjectAlreadyExistsException exception) {
            throw new ResponseStatusException(BAD_REQUEST, exception.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.OK);
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
    public ResponseEntity<IncomeSourceFullDto> updateTag(@RequestBody IncomeSourceFullDto incomeSourceDto) {
        try {
            incomeSourceService.update(incomeSourceDto);
        } catch (ObjectNotFoundException exception) {
            throw new ResponseStatusException(NOT_FOUND, exception.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
