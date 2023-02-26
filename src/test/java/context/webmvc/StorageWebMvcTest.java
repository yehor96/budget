package context.webmvc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import yehor.budget.common.date.DateManager;
import yehor.budget.common.exception.ObjectNotFoundException;
import yehor.budget.service.recording.StorageRecordingService;
import yehor.budget.web.dto.full.StorageRecordFullDto;
import yehor.budget.web.dto.limited.StorageRecordLimitedDto;

import java.util.Collections;
import java.util.Optional;

import static common.factory.StorageFactory.DEFAULT_STORAGE_RECORD_ID;
import static common.factory.StorageFactory.defaultStorageRecordFullDto;
import static common.factory.StorageFactory.defaultStorageRecordLimitedDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StorageWebMvcTest extends BaseWebMvcTest {

    @MockBean
    private StorageRecordingService storageRecordingService;
    @MockBean
    private DateManager dateManager;

    // Get latest storage record

    @Test
    void testGetLatestSuccessfully() throws Exception {
        StorageRecordFullDto expectedStorageRecordDto = defaultStorageRecordFullDto();

        when(storageRecordingService.getLatest()).thenReturn(Optional.of(expectedStorageRecordDto));

        String response = mockMvc.perform(get(STORAGE_URL)
                        .header("Authorization", BASIC_AUTH_STRING))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        StorageRecordFullDto actualStorageRecordDto = objectMapper.readValue(response, StorageRecordFullDto.class);

        assertEquals(expectedStorageRecordDto, actualStorageRecordDto);
    }

    @Test
    void testGetLatestThrowsExceptionWhenNotFound() throws Exception {
        String expectedErrorMessage = "There are no storage records";

        when(storageRecordingService.getLatest()).thenReturn(Optional.empty());

        String response = mockMvc.perform(get(STORAGE_URL)
                        .header("Authorization", BASIC_AUTH_STRING))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, NOT_FOUND, expectedErrorMessage);
    }

    // Save storage record

    @Test
    void testSaveSuccessfully() throws Exception {
        StorageRecordLimitedDto storageRecordDto = defaultStorageRecordLimitedDto();

        mockMvc.perform(post(STORAGE_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storageRecordDto)))
                .andExpect(status().isOk());

        verify(storageRecordingService, times(1)).save(storageRecordDto);
    }

    @Test
    void testSaveThrowsExceptionWhenInvalidDate() throws Exception {
        String expectedErrorMessage = "expectedErrorMessage";
        StorageRecordLimitedDto storageRecordDto = defaultStorageRecordLimitedDto();

        doThrow(new IllegalArgumentException(expectedErrorMessage)).when(dateManager).validateDateAfterStart(any());

        String response = mockMvc.perform(post(STORAGE_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storageRecordDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, BAD_REQUEST, expectedErrorMessage);

        verify(storageRecordingService, never()).save(storageRecordDto);
    }

    @Test
    void testSaveThrowsExceptionWhenMissingStorageItems() throws Exception {
        String expectedErrorMessage = "Storage items are not provided";
        StorageRecordLimitedDto storageRecordDto = defaultStorageRecordLimitedDto();
        storageRecordDto.setStorageItems(Collections.emptyList());

        String response = mockMvc.perform(post(STORAGE_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(storageRecordDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, BAD_REQUEST, expectedErrorMessage);

        verify(storageRecordingService, never()).save(storageRecordDto);
    }

    // Delete storage record

    @Test
    void testDeleteSuccessfully() throws Exception {
        mockMvc.perform(delete(STORAGE_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .param("id", String.valueOf(DEFAULT_STORAGE_RECORD_ID)))
                .andExpect(status().isOk());

        verify(storageRecordingService, times(1)).delete(DEFAULT_STORAGE_RECORD_ID);
    }

    @Test
    void testTryDeletingNotExistingStorageRecord() throws Exception {
        String expectedErrorMessage = "expectedErrorMessage";

        doThrow(new ObjectNotFoundException(expectedErrorMessage))
                .when(storageRecordingService).delete(DEFAULT_STORAGE_RECORD_ID);

        String response = mockMvc.perform(delete(STORAGE_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .param("id", String.valueOf(DEFAULT_STORAGE_RECORD_ID)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, NOT_FOUND, expectedErrorMessage);
    }
}
