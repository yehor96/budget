package webmvc;

import com.fasterxml.jackson.databind.ObjectReader;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import yehor.budget.common.exception.ObjectAlreadyExistsException;
import yehor.budget.common.exception.ObjectNotFoundException;
import yehor.budget.service.TagService;
import yehor.budget.web.dto.full.TagFullDto;
import yehor.budget.web.dto.limited.TagLimitedDto;

import java.util.List;

import static common.factory.TagFactory.DEFAULT_TAG_ID;
import static common.factory.TagFactory.defaultTagFullDto;
import static common.factory.TagFactory.defaultTagLimitedDto;
import static common.factory.TagFactory.secondTagFullDto;
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

class TagWebMvcTest extends BaseWebMvcTest {

    @MockBean
    private TagService tagService;

    // Get all tags

    @Test
    void testGetAllTags() throws Exception {
        List<TagFullDto> expectedTags = List.of(defaultTagFullDto(), secondTagFullDto());

        when(tagService.getAll()).thenReturn(expectedTags);

        String response = mockMvc.perform(get(TAGS_URL)
                        .header("Authorization", BASIC_AUTH_STRING))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ObjectReader listReader = objectMapper.readerForListOf(TagFullDto.class);
        List<TagFullDto> actualTags = listReader.readValue(response);

        assertEquals(expectedTags, actualTags);
    }

    // Save tag

    @Test
    void testSaveTag() throws Exception {
        TagLimitedDto tagLimitedDto = defaultTagLimitedDto();

        mockMvc.perform(post(TAGS_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagLimitedDto)))
                .andExpect(status().isOk());

        verify(tagService, times(1)).save(tagLimitedDto);
    }

    @Test
    void testTrySavingTagWhenSuchAlreadyExists() throws Exception {
        TagLimitedDto tagLimitedDto = defaultTagLimitedDto();
        String expectedErrorMessage = "expectedErrorMessage";

        doThrow(new ObjectAlreadyExistsException(expectedErrorMessage))
                .when(tagService).save(tagLimitedDto);

        String response = mockMvc.perform(post(TAGS_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagLimitedDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, BAD_REQUEST, expectedErrorMessage);
    }

    // Delete tag

    @Test
    void testDeleteTag() throws Exception {
        mockMvc.perform(delete(TAGS_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .param("id", String.valueOf(DEFAULT_TAG_ID)))
                .andExpect(status().isOk());

        verify(tagService, times(1)).delete(DEFAULT_TAG_ID);
    }

    @Test
    void testTryDeletingTagWhenSuchDoesNotExists() throws Exception {
        String expectedErrorMessage = "expectedErrorMessage";

        doThrow(new ObjectNotFoundException(expectedErrorMessage))
                .when(tagService).delete(DEFAULT_TAG_ID);

        String response = mockMvc.perform(delete(TAGS_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .param("id", String.valueOf(DEFAULT_TAG_ID)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, NOT_FOUND, expectedErrorMessage);
    }

    @Test
    void testTryDeletingTagWithDependentExpenses() throws Exception {
        String expectedErrorMessage = "expectedErrorMessage";

        doThrow(new IllegalArgumentException(expectedErrorMessage))
                .when(tagService).delete(DEFAULT_TAG_ID);

        String response = mockMvc.perform(delete(TAGS_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .param("id", String.valueOf(DEFAULT_TAG_ID)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, BAD_REQUEST, expectedErrorMessage);
    }

    // Update tag

    @Test
    void testUpdateTag() throws Exception {
        TagFullDto tag = defaultTagFullDto();

        mockMvc.perform(put(TAGS_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tag)))
                .andExpect(status().isOk());

        verify(tagService, times(1)).update(tag);
    }

    @Test
    void testTryUpdatingTagWhenSuchDoesNotExists() throws Exception {
        TagFullDto tag = defaultTagFullDto();
        String expectedErrorMessage = "expectedErrorMessage";

        doThrow(new ObjectNotFoundException(expectedErrorMessage))
                .when(tagService).update(tag);

        String response = mockMvc.perform(put(TAGS_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tag)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, NOT_FOUND, expectedErrorMessage);
    }
}
