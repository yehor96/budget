package yehor.budget.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yehor.budget.common.exception.ObjectAlreadyExistsException;
import yehor.budget.common.exception.ObjectNotFoundException;
import yehor.budget.entity.Actor;
import yehor.budget.repository.ActorRepository;
import yehor.budget.web.converter.ActorConverter;
import yehor.budget.web.dto.full.ActorFullDto;
import yehor.budget.web.dto.limited.ActorLimitedDto;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActorService {

    private final ActorRepository actorRepository;
    private final ActorConverter actorConverter;

    public List<ActorFullDto> getAll() {
        List<Actor> actors = actorRepository.findAll();
        return actors.stream()
                .map(actorConverter::convert)
                .toList();
    }

    @Transactional
    public void save(ActorLimitedDto actorDto) {
        Actor actor = actorConverter.convert(actorDto);
        validateNotExists(actor);
        actorRepository.save(actor);
        log.info("Saved: {}", actor);
    }

    public void delete(Long id) {
        try {
            actorRepository.deleteById(id);
            log.info("Actor with id {} is deleted", id);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Actor with id " + id + " not found");
        }
    }

    @Transactional
    public void update(ActorFullDto actorDto) {
        validateExists(actorDto.getId());
        Actor actor = actorConverter.convert(actorDto);
        actorRepository.save(actor);
        log.info("Updated: {}", actor);
    }

    private void validateNotExists(Actor actor) {
        if (actorRepository.existsByName(actor.getName())) {
            throw new ObjectAlreadyExistsException("Actor " + actor.getName() + " already exists");
        }
    }

    private void validateExists(Long id) {
        if (!actorRepository.existsById(id)) {
            throw new ObjectNotFoundException("Actor with id " + id + " does not exist");
        }
    }
}
