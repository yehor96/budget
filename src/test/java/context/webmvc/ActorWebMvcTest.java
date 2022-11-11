package context.webmvc;

import com.fasterxml.jackson.databind.ObjectReader;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import yehor.budget.common.exception.ObjectAlreadyExistsException;
import yehor.budget.common.exception.ObjectNotFoundException;
import yehor.budget.service.ActorService;
import yehor.budget.web.dto.full.ActorFullDto;
import yehor.budget.web.dto.limited.ActorLimitedDto;

import java.util.List;

import static common.factory.ActorFactory.DEFAULT_ACTOR_ID;
import static common.factory.ActorFactory.defaultActorFullDto;
import static common.factory.ActorFactory.defaultActorLimitedDto;
import static common.factory.ActorFactory.secondActorFullDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ActorWebMvcTest extends BaseWebMvcTest {

    @MockBean
    private ActorService actorService;

    // Get all actors

    @Test
    void testGetAllActors() throws Exception {
        List<ActorFullDto> expectedActors = List.of(defaultActorFullDto(), secondActorFullDto());

        when(actorService.getAll()).thenReturn(expectedActors);

        String response = mockMvc.perform(get(ACTORS_URL)
                        .header("Authorization", BASIC_AUTH_STRING))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ObjectReader listReader = objectMapper.readerForListOf(ActorFullDto.class);
        List<ActorFullDto> actualActors = listReader.readValue(response);

        assertEquals(expectedActors, actualActors);
    }

    // Save actor

    @Test
    void testSaveActor() throws Exception {
        ActorLimitedDto actorLimitedDto = defaultActorLimitedDto();

        mockMvc.perform(post(ACTORS_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actorLimitedDto)))
                .andExpect(status().isOk());

        verify(actorService, times(1)).save(actorLimitedDto);
    }

    @Test
    void testTrySavingActorWhenSuchAlreadyExists() throws Exception {
        ActorLimitedDto actorLimitedDto = defaultActorLimitedDto();
        String expectedErrorMessage = "expectedErrorMessage";

        doThrow(new ObjectAlreadyExistsException(expectedErrorMessage))
                .when(actorService).save(actorLimitedDto);

        String response = mockMvc.perform(post(ACTORS_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actorLimitedDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, BAD_REQUEST, expectedErrorMessage);
    }

    // Delete actor

    @Test
    void testDeleteActor() throws Exception {
        mockMvc.perform(delete(ACTORS_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .param("id", String.valueOf(DEFAULT_ACTOR_ID)))
                .andExpect(status().isOk());

        verify(actorService, times(1)).delete(DEFAULT_ACTOR_ID);
    }

    @Test
    void testTryDeletingActorWhenSuchDoesNotExists() throws Exception {
        String expectedErrorMessage = "expectedErrorMessage";

        doThrow(new ObjectNotFoundException(expectedErrorMessage))
                .when(actorService).delete(DEFAULT_ACTOR_ID);

        String response = mockMvc.perform(delete(ACTORS_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .param("id", String.valueOf(DEFAULT_ACTOR_ID)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, NOT_FOUND, expectedErrorMessage);
    }

    // Update actor

    @Test
    void testUpdateActor() throws Exception {
        ActorFullDto actorDto = defaultActorFullDto();

        mockMvc.perform(put(ACTORS_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actorDto)))
                .andExpect(status().isOk());

        verify(actorService, times(1)).update(actorDto);
    }

    @Test
    void testTryUpdatingActorWhenSuchDoesNotExists() throws Exception {
        ActorFullDto actorDto = defaultActorFullDto();
        String expectedErrorMessage = "expectedErrorMessage";

        doThrow(new ObjectNotFoundException(expectedErrorMessage))
                .when(actorService).update(actorDto);

        String response = mockMvc.perform(put(ACTORS_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actorDto)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, NOT_FOUND, expectedErrorMessage);
    }
}
