package yehor.budget.service;

import org.junit.jupiter.api.Test;
import org.springframework.dao.EmptyResultDataAccessException;
import yehor.budget.common.exception.ObjectAlreadyExistsException;
import yehor.budget.common.exception.ObjectNotFoundException;
import yehor.budget.entity.Actor;
import yehor.budget.repository.ActorRepository;
import yehor.budget.web.converter.ActorConverter;
import yehor.budget.web.dto.full.ActorFullDto;
import yehor.budget.web.dto.limited.ActorLimitedDto;

import java.util.List;

import static common.factory.ActorFactory.defaultActor;
import static common.factory.ActorFactory.defaultActorFullDto;
import static common.factory.ActorFactory.defaultActorLimitedDto;
import static common.factory.ActorFactory.secondActor;
import static common.factory.ActorFactory.secondActorFullDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ActorServiceTest {

    private final ActorRepository actorRepositoryMock = mock(ActorRepository.class);
    private final ActorConverter actorConverterMock = mock(ActorConverter.class);

    private final ActorService actorService = new ActorService(actorRepositoryMock, actorConverterMock);

    @Test
    void testGetAll() {
        ActorFullDto expectedDto1 = defaultActorFullDto();
        ActorFullDto expectedDto2 = secondActorFullDto();
        Actor actor1 = defaultActor();
        Actor actor2 = secondActor();

        when(actorRepositoryMock.findAll()).thenReturn(List.of(actor1, actor2));
        when(actorConverterMock.convert(actor1)).thenReturn(expectedDto1);
        when(actorConverterMock.convert(actor2)).thenReturn(expectedDto2);

        List<ActorFullDto> actors = actorService.getAll();

        assertTrue(actors.contains(expectedDto1));
        assertTrue(actors.contains(expectedDto2));
    }

    @Test
    void testSave() {
        ActorLimitedDto actorLimitedDto = defaultActorLimitedDto();
        Actor actor = defaultActor();

        when(actorConverterMock.convert(actorLimitedDto)).thenReturn(actor);

        actorService.save(actorLimitedDto);

        verify(actorRepositoryMock, times(1))
                .save(actor);
    }

    @Test
    void testTrySavingExistingActor() {
        ActorLimitedDto actorLimitedDto = defaultActorLimitedDto();
        Actor actor = defaultActor();

        when(actorConverterMock.convert(actorLimitedDto)).thenReturn(actor);
        when(actorRepositoryMock.existsByName(actor.getName())).thenReturn(true);

        try {
            actorService.save(actorLimitedDto);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(ObjectAlreadyExistsException.class, e.getClass());
            ObjectAlreadyExistsException exception = (ObjectAlreadyExistsException) e;
            assertEquals("Actor " + actorLimitedDto.getName() + " already exists", exception.getMessage());
            verify(actorRepositoryMock, never())
                    .save(actor);
        }
    }

    @Test
    void testDeleteActor() {
        actorService.delete(1L);
        verify(actorRepositoryMock, times(1))
                .deleteById(1L);
    }

    @Test
    void testTryDeletingNotExistingActor() {
        doThrow(new EmptyResultDataAccessException(1)).when(actorRepositoryMock).deleteById(1L);

        try {
            actorService.delete(1L);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(ObjectNotFoundException.class, e.getClass());
            ObjectNotFoundException exception = (ObjectNotFoundException) e;
            assertEquals("Actor with id " + 1L + " not found", exception.getMessage());
            verify(actorRepositoryMock, times(1))
                    .deleteById(1L);
        }
    }

    @Test
    void testUpdateActor() {
        ActorFullDto expectedDto = defaultActorFullDto();
        Actor actor = defaultActor();

        when(actorConverterMock.convert(expectedDto)).thenReturn(actor);
        when(actorRepositoryMock.existsById(expectedDto.getId())).thenReturn(true);

        actorService.update(expectedDto);

        verify(actorRepositoryMock, times(1))
                .save(actor);
    }

    @Test
    void testTryUpdatingNotExistingActor() {
        ActorFullDto expectedDto = defaultActorFullDto();
        Actor actor = defaultActor();

        when(actorConverterMock.convert(expectedDto)).thenReturn(actor);
        when(actorRepositoryMock.existsById(expectedDto.getId())).thenReturn(false);

        try {
            actorService.update(expectedDto);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(ObjectNotFoundException.class, e.getClass());
            ObjectNotFoundException exception = (ObjectNotFoundException) e;
            assertEquals("Actor with id " + 1L + " does not exist", exception.getMessage());
            verify(actorRepositoryMock, never())
                    .save(actor);
        }
    }
}
