package context.webmvc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import yehor.budget.common.exception.ObjectAlreadyExistsException;
import yehor.budget.common.exception.ObjectNotFoundException;
import yehor.budget.service.IncomeSourceService;
import yehor.budget.web.dto.TotalIncomeDto;
import yehor.budget.web.dto.full.IncomeSourceFullDto;
import yehor.budget.web.dto.limited.IncomeSourceLimitedDto;

import static common.factory.IncomeSourceFactory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class IncomeSourceWebMvcTest extends BaseWebMvcTest {

    @MockBean
    private IncomeSourceService incomeSourceService;

    // Get total income

    @Test
    void testGetTotalIncome() throws Exception {
        TotalIncomeDto expectedTotalIncomeDto = defaultTotalIncomeDto();

        when(incomeSourceService.getTotalIncome()).thenReturn(expectedTotalIncomeDto);

        String response = mockMvc.perform(get(INCOME_SOURCES_URL))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        TotalIncomeDto actualTotalIncomeDto = objectMapper.readValue(response, TotalIncomeDto.class);

        assertEquals(expectedTotalIncomeDto, actualTotalIncomeDto);
    }

    // Save total income

    @Test
    void testSaveTotalIncome() throws Exception {
        IncomeSourceLimitedDto incomeSourceLimitedDto = defaultIncomeSourceLimitedDto();
        IncomeSourceFullDto expectedIncomeSourceDto = defaultIncomeSourceFullDto();

        when(incomeSourceService.save(incomeSourceLimitedDto)).thenReturn(expectedIncomeSourceDto);

        String response = mockMvc.perform(post(INCOME_SOURCES_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomeSourceLimitedDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        IncomeSourceFullDto actualIncomeSourceDto = objectMapper.readValue(response, IncomeSourceFullDto.class);

        verify(incomeSourceService, times(1)).save(incomeSourceLimitedDto);
        assertEquals(expectedIncomeSourceDto, actualIncomeSourceDto);
    }

    @Test
    void testTrySavingIncomeSourceWhenSuchAlreadyExists() throws Exception {
        IncomeSourceLimitedDto incomeSourceLimitedDto = defaultIncomeSourceLimitedDto();
        String expectedErrorMessage = "expectedErrorMessage";

        doThrow(new ObjectAlreadyExistsException(expectedErrorMessage))
                .when(incomeSourceService).save(incomeSourceLimitedDto);

        String response = mockMvc.perform(post(INCOME_SOURCES_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomeSourceLimitedDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, BAD_REQUEST, expectedErrorMessage);
    }

    // Delete total income

    @Test
    void testDeleteIncomeSource() throws Exception {
        mockMvc.perform(delete(INCOME_SOURCES_URL)
                        .param("id", String.valueOf(DEFAULT_INCOME_SOURCE_ID)))
                .andExpect(status().isOk());

        verify(incomeSourceService, times(1)).delete(DEFAULT_INCOME_SOURCE_ID);
    }

    @Test
    void testTryDeletingIncomeSourceWhenSuchDoesNotExists() throws Exception {
        String expectedErrorMessage = "expectedErrorMessage";

        doThrow(new ObjectNotFoundException(expectedErrorMessage))
                .when(incomeSourceService).delete(DEFAULT_INCOME_SOURCE_ID);

        String response = mockMvc.perform(delete(INCOME_SOURCES_URL)
                        .param("id", String.valueOf(DEFAULT_INCOME_SOURCE_ID)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, NOT_FOUND, expectedErrorMessage);
    }

    // Update total income

    @Test
    void testUpdateIncomeSource() throws Exception {
        IncomeSourceFullDto incomeSourceDto = defaultIncomeSourceFullDto();

        when(incomeSourceService.update(incomeSourceDto)).thenReturn(incomeSourceDto);

        String response = mockMvc.perform(put(INCOME_SOURCES_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomeSourceDto)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        IncomeSourceFullDto actualIncomeSourceDto = objectMapper.readValue(response, IncomeSourceFullDto.class);

        verify(incomeSourceService, times(1)).update(incomeSourceDto);
        assertEquals(incomeSourceDto, actualIncomeSourceDto);
    }

    @Test
    void testTryUpdatingIncomeSourceWhenSuchDoesNotExists() throws Exception {
        IncomeSourceFullDto incomeSourceDto = defaultIncomeSourceFullDto();
        String expectedErrorMessage = "expectedErrorMessage";

        doThrow(new ObjectNotFoundException(expectedErrorMessage))
                .when(incomeSourceService).update(incomeSourceDto);

        String response = mockMvc.perform(put(INCOME_SOURCES_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incomeSourceDto)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, NOT_FOUND, expectedErrorMessage);
    }
}
