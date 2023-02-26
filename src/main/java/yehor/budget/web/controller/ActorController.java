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
import yehor.budget.service.ActorService;
import yehor.budget.web.dto.full.ActorFullDto;
import yehor.budget.web.dto.limited.ActorLimitedDto;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/v1/actors")
@RequiredArgsConstructor
@Tag(name = "Actor Controller")
public class ActorController {

    private final ActorService actorService;

    @GetMapping
    @Operation(summary = "Get all actors")
    public List<ActorFullDto> getAllActors() {
        return actorService.getAll();
    }

    @PostMapping
    @Operation(summary = "Save actor")
    public ResponseEntity<ActorLimitedDto> saveActor(@RequestBody ActorLimitedDto actorDto) {
        try {
            actorService.save(actorDto);
        } catch (ObjectAlreadyExistsException exception) {
            throw new ResponseStatusException(BAD_REQUEST, exception.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    @Operation(summary = "Delete actor by id")
    public ResponseEntity<ActorLimitedDto> deleteActor(@RequestParam("id") Long id) {
        try {
            actorService.delete(id);
        } catch (ObjectNotFoundException exception) {
            throw new ResponseStatusException(NOT_FOUND, exception.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping
    @Operation(summary = "Update actor by id")
    public ResponseEntity<ActorFullDto> updateActor(@RequestBody ActorFullDto actorDto) {
        try {
            actorService.update(actorDto);
        } catch (ObjectNotFoundException exception) {
            throw new ResponseStatusException(NOT_FOUND, exception.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
