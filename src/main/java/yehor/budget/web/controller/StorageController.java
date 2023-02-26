package yehor.budget.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import yehor.budget.common.date.DateManager;
import yehor.budget.common.exception.ObjectNotFoundException;
import yehor.budget.service.recording.StorageRecordingService;
import yehor.budget.web.dto.full.StorageRecordFullDto;
import yehor.budget.web.dto.limited.StorageRecordLimitedDto;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
@Tag(name = "Storage Controller")
public class StorageController {

    private final StorageRecordingService storageRecordingService;
    private final DateManager dateManager;

    @GetMapping
    @Operation(summary = "Get latest storage record")
    public StorageRecordFullDto getLatest() {
        return storageRecordingService.getLatest().orElseThrow(
                () -> new ResponseStatusException(NOT_FOUND, "There are no storage records"));
    }

    @PostMapping
    @Operation(summary = "Save storage record")
    public ResponseEntity<StorageRecordLimitedDto> save(@RequestBody StorageRecordLimitedDto storageRecord) {
        try {
            dateManager.validateDateAfterStart(storageRecord.getDate());
            validateStorageItems(storageRecord);
            storageRecordingService.save(storageRecord);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(BAD_REQUEST, exception.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    @Operation(summary = "Delete storage record")
    public ResponseEntity<StorageRecordLimitedDto> delete(@RequestParam("id") Long id) {
        try {
            storageRecordingService.delete(id);
        } catch (ObjectNotFoundException exception) {
            throw new ResponseStatusException(NOT_FOUND, exception.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void validateStorageItems(StorageRecordLimitedDto storageRecord) {
        if (CollectionUtils.isEmpty(storageRecord.getStorageItems())) {
            throw new IllegalArgumentException("Storage items are not provided");
        }
    }
}
