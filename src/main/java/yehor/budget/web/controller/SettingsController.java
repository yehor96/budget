package yehor.budget.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import yehor.budget.service.SettingsService;
import yehor.budget.web.dto.full.SettingsFullDto;
import yehor.budget.web.dto.limited.SettingsLimitedDto;

import javax.persistence.EntityNotFoundException;
import java.util.Objects;

import static yehor.budget.service.worker.EstimatedExpenseWorker.EXPECTED_EXPENSE_END_DATE_SCOPE_PATTERN;

@Slf4j
@RestController
@RequestMapping("/api/v1/settings")
@RequiredArgsConstructor
@Tag(name = "Settings Controller")
public class SettingsController {

    private final SettingsService settingsService;

    @GetMapping
    @Operation(summary = "Get settings")
    public ResponseEntity<SettingsFullDto> getSettings() {
        try {
            SettingsFullDto settingsDto = settingsService.getSettings();
            return new ResponseEntity<>(settingsDto, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            log.error("Error getting settings. {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PutMapping
    @Operation(summary = "Update settings")
    public ResponseEntity<SettingsLimitedDto> updateSettings(@RequestBody SettingsLimitedDto settingsLimitedDto) {
        try {
            validate(settingsLimitedDto);
            settingsService.updateSettings(settingsLimitedDto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error updating settings. {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private void validate(SettingsLimitedDto settingsLimitedDto) {
        String scopePattern = settingsLimitedDto.getEstimatedExpenseWorkerEndDateScopePattern();
        if (Objects.nonNull(scopePattern) && !EXPECTED_EXPENSE_END_DATE_SCOPE_PATTERN.matcher(scopePattern).matches()) {
            throw new IllegalArgumentException("Illegal estimated expense end date scope pattern provided: "
                    + scopePattern);
        }
    }
}
