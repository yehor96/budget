package context.webmvc;

import com.fasterxml.jackson.databind.ObjectReader;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import yehor.budget.common.date.DateManager;
import yehor.budget.common.exception.ObjectNotFoundException;
import yehor.budget.service.recording.BalanceRecordingService;
import yehor.budget.web.dto.full.BalanceRecordFullDto;
import yehor.budget.web.dto.limited.BalanceRecordLimitedDto;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static common.factory.BalanceFactory.DEFAULT_BALANCE_RECORD_ID;
import static common.factory.BalanceFactory.balanceRecordFullDtoWithEstimates;
import static common.factory.BalanceFactory.defaultBalanceRecordFullDto;
import static common.factory.BalanceFactory.defaultBalanceRecordLimitedDto;
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

class BalanceWebMvcTest extends BaseWebMvcTest {

    protected static final String BALANCE_INTERVAL_URL = BALANCE_URL + "/interval";

    @MockBean
    private BalanceRecordingService balanceRecordingService;
    @MockBean
    private DateManager dateManager;

    // Get latest balance record

    @Test
    void testGetLatestSuccessfully() throws Exception {
        BalanceRecordFullDto expectedBalanceRecordDto = balanceRecordFullDtoWithEstimates();

        when(balanceRecordingService.getLatest()).thenReturn(Optional.of(expectedBalanceRecordDto));

        String response = mockMvc.perform(get(BALANCE_URL)
                        .header("Authorization", BASIC_AUTH_STRING))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        BalanceRecordFullDto actualBalanceRecordFullDto = objectMapper.readValue(response, BalanceRecordFullDto.class);

        assertEquals(expectedBalanceRecordDto, actualBalanceRecordFullDto);
    }

    @Test
    void testGetLatestThrowsExceptionWhenNotFound() throws Exception {
        String expectedErrorMessage = "There are no balance records";

        when(balanceRecordingService.getLatest()).thenReturn(Optional.empty());

        String response = mockMvc.perform(get(BALANCE_URL)
                        .header("Authorization", BASIC_AUTH_STRING))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, NOT_FOUND, expectedErrorMessage);
    }

    // Save balance record

    @Test
    void testSaveSuccessfully() throws Exception {
        BalanceRecordLimitedDto balanceRecordDto = defaultBalanceRecordLimitedDto();

        mockMvc.perform(post(BALANCE_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(balanceRecordDto)))
                .andExpect(status().isOk());

        verify(balanceRecordingService, times(1)).save(balanceRecordDto);
    }

    @Test
    void testSaveThrowsExceptionWhenInvalidDate() throws Exception {
        String expectedErrorMessage = "expectedErrorMessage";
        BalanceRecordLimitedDto balanceRecordDto = defaultBalanceRecordLimitedDto();

        doThrow(new IllegalArgumentException(expectedErrorMessage)).when(dateManager).validateDateAfterStart(any());

        String response = mockMvc.perform(post(BALANCE_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(balanceRecordDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, BAD_REQUEST, expectedErrorMessage);

        verify(balanceRecordingService, never()).save(balanceRecordDto);
    }

    @Test
    void testSaveThrowsExceptionWhenMissingBalanceItems() throws Exception {
        String expectedErrorMessage = "Balance items are not provided";
        BalanceRecordLimitedDto balanceRecordDto = defaultBalanceRecordLimitedDto();
        balanceRecordDto.setBalanceItems(Collections.emptyList());

        String response = mockMvc.perform(post(BALANCE_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(balanceRecordDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, BAD_REQUEST, expectedErrorMessage);

        verify(balanceRecordingService, never()).save(balanceRecordDto);
    }

    @Test
    void testSaveThrowsExceptionWhenInvalidActorIds() throws Exception {
        String expectedErrorMessage = "Provided actor ids are not valid: [-1, -1]";
        BalanceRecordLimitedDto balanceRecordDto = defaultBalanceRecordLimitedDto();
        balanceRecordDto.getBalanceItems().forEach(balanceItem -> balanceItem.setActorId(-1L));

        String response = mockMvc.perform(post(BALANCE_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(balanceRecordDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, BAD_REQUEST, expectedErrorMessage);

        verify(balanceRecordingService, never()).save(balanceRecordDto);
    }

    // Get balance records in interval

    @Test
    void testGetBalanceRecordsInInterval() throws Exception {
        List<BalanceRecordFullDto> expectedRecordsInterval = List.of(defaultBalanceRecordFullDto());

        String from = "2022-06-06";
        String to = "2022-07-07";
        LocalDate dateFrom = LocalDate.of(2022, 6, 6);
        LocalDate dateTo = LocalDate.of(2022, 7, 7);

        when(dateManager.parse(from)).thenReturn(dateFrom);
        when(dateManager.parse(to)).thenReturn(dateTo);
        when(balanceRecordingService.findAllInInterval(dateFrom, dateTo)).thenReturn(expectedRecordsInterval);

        String response = mockMvc.perform(get(BALANCE_INTERVAL_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .param("dateFrom", from)
                        .param("dateTo", to))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ObjectReader listReader = objectMapper.readerForListOf(BalanceRecordFullDto.class);
        List<BalanceRecordFullDto> actualRecordsInterval = listReader.readValue(response);

        verify(balanceRecordingService, times(1)).findAllInInterval(dateFrom, dateTo);
        assertEquals(expectedRecordsInterval, actualRecordsInterval);
    }

    @Test
    void testTryGettingBalanceRecordsInIntervalFailingSequentialOrderCheck() throws Exception {
        String expectedErrorMessage = "expectedErrorMessage";
        String from = "2022-06-06";
        String to = "2022-07-07";
        LocalDate dateFrom = LocalDate.of(2022, 6, 6);
        LocalDate dateTo = LocalDate.of(2022, 7, 7);

        when(dateManager.parse(from)).thenReturn(dateFrom);
        when(dateManager.parse(to)).thenReturn(dateTo);
        doThrow(new IllegalArgumentException(expectedErrorMessage))
                .when(dateManager).validateDatesInSequentialOrder(dateFrom, dateTo);

        String response = mockMvc.perform(get(BALANCE_INTERVAL_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .param("dateFrom", from)
                        .param("dateTo", to))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, BAD_REQUEST, expectedErrorMessage);

        verify(balanceRecordingService, never()).findAllInInterval(dateFrom, dateTo);
    }

    @Test
    void testTryGettingBalanceRecordsInIntervalFailingDatesWithinBudgetCheck() throws Exception {
        String expectedErrorMessage = "expectedErrorMessage";
        String from = "2022-06-06";
        String to = "2022-07-07";
        LocalDate dateFrom = LocalDate.of(2022, 6, 6);
        LocalDate dateTo = LocalDate.of(2022, 7, 7);

        when(dateManager.parse(from)).thenReturn(dateFrom);
        when(dateManager.parse(to)).thenReturn(dateTo);
        doThrow(new IllegalArgumentException(expectedErrorMessage))
                .when(dateManager).validateDatesWithinBudget(dateFrom, dateTo);

        String response = mockMvc.perform(get(BALANCE_INTERVAL_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .param("dateFrom", from)
                        .param("dateTo", to))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, BAD_REQUEST, expectedErrorMessage);

        verify(balanceRecordingService, never()).findAllInInterval(dateFrom, dateTo);
    }

    @Test
    void testTryGettingBalanceRecordsInIntervalFailingParsingOfDateParameter() throws Exception {
        String expectedErrorMessage = "expectedErrorMessage";
        String from = "2022-06-06";
        String to = "2022-07-07";
        LocalDate dateFrom = LocalDate.of(2022, 6, 6);
        LocalDate dateTo = LocalDate.of(2022, 7, 7);

        doThrow(new IllegalArgumentException(expectedErrorMessage))
                .when(dateManager).parse(from);

        String response = mockMvc.perform(get(BALANCE_INTERVAL_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .param("dateFrom", from)
                        .param("dateTo", to))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, BAD_REQUEST, expectedErrorMessage);

        verify(balanceRecordingService, never()).findAllInInterval(dateFrom, dateTo);
    }

    // Delete balance record

    @Test
    void testDeleteSuccessfully() throws Exception {
        mockMvc.perform(delete(BALANCE_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .param("id", String.valueOf(DEFAULT_BALANCE_RECORD_ID)))
                .andExpect(status().isOk());

        verify(balanceRecordingService, times(1)).delete(DEFAULT_BALANCE_RECORD_ID);
    }

    @Test
    void testTryDeletingNotExistingBalanceRecord() throws Exception {
        String expectedErrorMessage = "expectedErrorMessage";

        doThrow(new ObjectNotFoundException(expectedErrorMessage))
                .when(balanceRecordingService).delete(DEFAULT_BALANCE_RECORD_ID);

        String response = mockMvc.perform(delete(BALANCE_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .param("id", String.valueOf(DEFAULT_BALANCE_RECORD_ID)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, NOT_FOUND, expectedErrorMessage);
    }
}
