package webmvc;

import com.fasterxml.jackson.databind.ObjectReader;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import yehor.budget.common.date.DateManager;
import yehor.budget.common.exception.ObjectNotFoundException;
import yehor.budget.service.ExpenseService;
import yehor.budget.web.dto.full.ExpenseFullDto;
import yehor.budget.web.dto.limited.ExpenseLimitedDto;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static common.factory.ExpenseFactory.DEFAULT_EXPENSE_ID;
import static common.factory.ExpenseFactory.defaultExpenseFullDto;
import static common.factory.ExpenseFactory.defaultExpenseFullDtoList;
import static common.factory.ExpenseFactory.defaultExpenseLimitedDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ExpenseWebMvcTest extends BaseWebMvcTest {

    @MockBean
    private DateManager dateManager;
    @MockBean
    private ExpenseService expenseService;

    // Get Expense by id

    @Test
    void testGetExpenseById() throws Exception {
        ExpenseFullDto expectedExpense = defaultExpenseFullDto();

        when(expenseService.getById(DEFAULT_EXPENSE_ID)).thenReturn(expectedExpense);

        String response = mockMvc.perform(get(EXPENSES_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .param("id", String.valueOf(DEFAULT_EXPENSE_ID)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ExpenseFullDto actualExpense = objectMapper.readValue(response, ExpenseFullDto.class);

        assertEquals(expectedExpense, actualExpense);
    }

    @Test
    void testTryGettingExpenseByNotExistingId() throws Exception {
        String expectedErrorMessage = "Expense with id " + DEFAULT_EXPENSE_ID + " not found";

        when(expenseService.getById(DEFAULT_EXPENSE_ID)).thenThrow(new EntityNotFoundException());

        String response = mockMvc.perform(get(EXPENSES_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .param("id", String.valueOf(DEFAULT_EXPENSE_ID)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, NOT_FOUND, expectedErrorMessage);
    }

    // Save Expense

    @Test
    void testSaveExpense() throws Exception {
        ExpenseLimitedDto expenseLimitedDto = defaultExpenseLimitedDto();

        mockMvc.perform(post(EXPENSES_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expenseLimitedDto)))
                .andExpect(status().isOk());

        verify(expenseService, times(1)).save(expenseLimitedDto);
    }

    @Test
    void testTrySavingExpenseWithDateBeforeStart() throws Exception {
        ExpenseLimitedDto expenseLimitedDto = defaultExpenseLimitedDto();
        String expectedErrorMessage = "expectedErrorMessage";

        doThrow(new IllegalArgumentException(expectedErrorMessage))
                .when(dateManager).validateDateAfterStart(expenseLimitedDto.getDate());

        String response = mockMvc.perform(post(EXPENSES_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expenseLimitedDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verify(expenseService, never()).save(expenseLimitedDto);
        verifyResponseErrorObject(response, BAD_REQUEST, expectedErrorMessage);
    }

    @Test
    void testTrySavingExpenseWithIllegalCategoryId() throws Exception {
        ExpenseLimitedDto expenseLimitedDto = defaultExpenseLimitedDto();
        expenseLimitedDto.setCategoryId(-1L);
        String expectedErrorMessage = "Provided category id is not valid - -1. Please provide valid category id";

        String response = mockMvc.perform(post(EXPENSES_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expenseLimitedDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verify(expenseService, never()).save(expenseLimitedDto);
        verifyResponseErrorObject(response, BAD_REQUEST, expectedErrorMessage);
    }

    @Test
    void testTrySavingExpenseWhenObjectNotFound() throws Exception {
        ExpenseLimitedDto expenseLimitedDto = defaultExpenseLimitedDto();
        String expectedErrorMessage = "expectedErrorMessage";

        doThrow(new ObjectNotFoundException(expectedErrorMessage))
                .when(expenseService).save(expenseLimitedDto);

        String response = mockMvc.perform(post(EXPENSES_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expenseLimitedDto)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, NOT_FOUND, expectedErrorMessage);
    }

    @Test
    void testTrySavingExpenseWithIllegalTagId() throws Exception {
        ExpenseLimitedDto expenseLimitedDto = defaultExpenseLimitedDto();
        expenseLimitedDto.setTagIds(Collections.singleton(-1L));
        String expectedErrorMessage = "Tag cannot be negative or 0: [-1]";

        String response = mockMvc.perform(post(EXPENSES_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expenseLimitedDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verify(expenseService, never()).save(expenseLimitedDto);
        verifyResponseErrorObject(response, BAD_REQUEST, expectedErrorMessage);
    }

    @Test
    void testTrySavingExpenseWhenNoteIsTooLong() throws Exception {
        ExpenseLimitedDto expenseLimitedDto = defaultExpenseLimitedDto();
        expenseLimitedDto.setNote("charscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharsrscharscharscharscharsrscharscharscharscharsrscharscharscharschars");

        String response = mockMvc.perform(post(EXPENSES_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expenseLimitedDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, BAD_REQUEST, "Note should not be longer than 255 characters");
    }

    // Update Expense

    @Test
    void testUpdateExpense() throws Exception {
        ExpenseFullDto expenseFullDto = defaultExpenseFullDto();

        mockMvc.perform(put(EXPENSES_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expenseFullDto)))
                .andExpect(status().isOk());

        verify(expenseService, times(1)).update(expenseFullDto);
    }

    @Test
    void testTryUpdatingExpenseWithDateBeforeStart() throws Exception {
        ExpenseFullDto expenseFullDto = defaultExpenseFullDto();
        String expectedErrorMessage = "expectedErrorMessage";

        doThrow(new IllegalArgumentException(expectedErrorMessage))
                .when(dateManager).validateDateAfterStart(expenseFullDto.getDate());

        String response = mockMvc.perform(put(EXPENSES_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expenseFullDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verify(expenseService, never()).update(expenseFullDto);
        verifyResponseErrorObject(response, BAD_REQUEST, expectedErrorMessage);
    }

    @Test
    void testTryUpdatingExpenseWithIllegalCategoryId() throws Exception {
        ExpenseFullDto expenseFullDto = defaultExpenseFullDto();
        expenseFullDto.setCategoryId(-1L);
        String expectedErrorMessage = "Provided category id is not valid - -1. Please provide valid category id";

        String response = mockMvc.perform(put(EXPENSES_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expenseFullDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verify(expenseService, never()).update(expenseFullDto);
        verifyResponseErrorObject(response, BAD_REQUEST, expectedErrorMessage);
    }

    @Test
    void testTryUpdatingExpenseWithIllegalTagId() throws Exception {
        ExpenseFullDto expenseFullDto = defaultExpenseFullDto();
        expenseFullDto.setTagIds(Collections.singleton(-1L));
        String expectedErrorMessage = "Tag cannot be negative or 0: [-1]";

        String response = mockMvc.perform(put(EXPENSES_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expenseFullDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verify(expenseService, never()).update(expenseFullDto);
        verifyResponseErrorObject(response, BAD_REQUEST, expectedErrorMessage);
    }

    @Test
    void testTryUpdatingExpenseWhenObjectNotFound() throws Exception {
        ExpenseFullDto expenseFullDto = defaultExpenseFullDto();
        String expectedErrorMessage = "expectedErrorMessage";

        doThrow(new ObjectNotFoundException(expectedErrorMessage))
                .when(expenseService).update(expenseFullDto);

        String response = mockMvc.perform(put(EXPENSES_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expenseFullDto)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, NOT_FOUND, expectedErrorMessage);
    }

    @Test
    void testTryUpdatingExpenseWhenNoteIsTooLong() throws Exception {
        ExpenseLimitedDto expenseLimitedDto = defaultExpenseLimitedDto();
        expenseLimitedDto.setNote("charscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharscharsrscharscharscharscharsrscharscharscharscharsrscharscharscharschars");

        String response = mockMvc.perform(put(EXPENSES_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expenseLimitedDto)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, BAD_REQUEST, "Note should not be longer than 255 characters");
    }

    // Get expenses in interval

    @Test
    void testGetExpensesInInterval() throws Exception {
        List<ExpenseFullDto> expectedExpenseInterval = defaultExpenseFullDtoList();

        String from = "2022-06-06";
        String to = "2022-07-07";
        LocalDate dateFrom = LocalDate.of(2022, 6, 6);
        LocalDate dateTo = LocalDate.of(2022, 7, 7);

        when(dateManager.parse(from)).thenReturn(dateFrom);
        when(dateManager.parse(to)).thenReturn(dateTo);
        when(expenseService.findAllInInterval(dateFrom, dateTo)).thenReturn(expectedExpenseInterval);

        String response = mockMvc.perform(get(EXPENSE_INTERVAL_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .param("dateFrom", from)
                        .param("dateTo", to))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ObjectReader listReader = objectMapper.readerForListOf(ExpenseFullDto.class);
        List<ExpenseFullDto> actualExpenseInterval = listReader.readValue(response);

        verify(expenseService, times(1)).findAllInInterval(dateFrom, dateTo);
        assertEquals(expectedExpenseInterval, actualExpenseInterval);
    }

    @Test
    void testTryGettingExpensesInIntervalFailingSequentialOrderCheck() throws Exception {
        String expectedErrorMessage = "expectedErrorMessage";
        String from = "2022-06-06";
        String to = "2022-07-07";
        LocalDate dateFrom = LocalDate.of(2022, 6, 6);
        LocalDate dateTo = LocalDate.of(2022, 7, 7);

        when(dateManager.parse(from)).thenReturn(dateFrom);
        when(dateManager.parse(to)).thenReturn(dateTo);
        doThrow(new IllegalArgumentException(expectedErrorMessage))
                .when(dateManager).validateDatesInSequentialOrder(dateFrom, dateTo);

        String response = mockMvc.perform(get(EXPENSE_INTERVAL_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .param("dateFrom", from)
                        .param("dateTo", to))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, BAD_REQUEST, expectedErrorMessage);

        verify(expenseService, never()).findAllInInterval(dateFrom, dateTo);
    }

    @Test
    void testTryGettingExpensesInIntervalFailingDatesWithinBudgetCheck() throws Exception {
        String expectedErrorMessage = "expectedErrorMessage";
        String from = "2022-06-06";
        String to = "2022-07-07";
        LocalDate dateFrom = LocalDate.of(2022, 6, 6);
        LocalDate dateTo = LocalDate.of(2022, 7, 7);

        when(dateManager.parse(from)).thenReturn(dateFrom);
        when(dateManager.parse(to)).thenReturn(dateTo);
        doThrow(new IllegalArgumentException(expectedErrorMessage))
                .when(dateManager).validateDatesWithinBudget(dateFrom, dateTo);

        String response = mockMvc.perform(get(EXPENSE_INTERVAL_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .param("dateFrom", from)
                        .param("dateTo", to))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, BAD_REQUEST, expectedErrorMessage);

        verify(expenseService, never()).findAllInInterval(dateFrom, dateTo);
    }

    @Test
    void testTryGettingExpensesInIntervalFailingParsingOfDateParameter() throws Exception {
        String expectedErrorMessage = "expectedErrorMessage";
        String from = "2022-06-06";
        String to = "2022-07-07";
        LocalDate dateFrom = LocalDate.of(2022, 6, 6);
        LocalDate dateTo = LocalDate.of(2022, 7, 7);

        doThrow(new IllegalArgumentException(expectedErrorMessage))
                .when(dateManager).parse(from);

        String response = mockMvc.perform(get(EXPENSE_INTERVAL_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .param("dateFrom", from)
                        .param("dateTo", to))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, BAD_REQUEST, expectedErrorMessage);

        verify(expenseService, never()).findAllInInterval(dateFrom, dateTo);
    }

    // Get expenses sum in interval

    @Test
    void testGetExpensesSumInInterval() throws Exception {
        BigDecimal expectedSum = BigDecimal.TEN;
        String from = "2022-06-06";
        String to = "2022-07-07";
        LocalDate dateFrom = LocalDate.of(2022, 6, 6);
        LocalDate dateTo = LocalDate.of(2022, 7, 7);

        when(dateManager.parse(from)).thenReturn(dateFrom);
        when(dateManager.parse(to)).thenReturn(dateTo);
        when(expenseService.findSumInInterval(dateFrom, dateTo)).thenReturn(expectedSum);

        String response = mockMvc.perform(get(EXPENSE_SUM_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .param("dateFrom", from)
                        .param("dateTo", to))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        BigDecimal actualSum = objectMapper.readValue(response, BigDecimal.class);

        verify(expenseService, times(1)).findSumInInterval(dateFrom, dateTo);
        assertEquals(expectedSum, actualSum);
    }

    @Test
    void testTryGettingExpensesSumInIntervalFailingSequentialOrderCheck() throws Exception {
        String expectedErrorMessage = "expectedErrorMessage";
        String from = "2022-06-06";
        String to = "2022-07-07";
        LocalDate dateFrom = LocalDate.of(2022, 6, 6);
        LocalDate dateTo = LocalDate.of(2022, 7, 7);

        when(dateManager.parse(from)).thenReturn(dateFrom);
        when(dateManager.parse(to)).thenReturn(dateTo);
        doThrow(new IllegalArgumentException(expectedErrorMessage))
                .when(dateManager).validateDatesInSequentialOrder(dateFrom, dateTo);

        String response = mockMvc.perform(get(EXPENSE_SUM_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .param("dateFrom", from)
                        .param("dateTo", to))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, BAD_REQUEST, expectedErrorMessage);

        verify(expenseService, never()).findSumInInterval(dateFrom, dateTo);
    }

    @Test
    void testTryGettingExpensesSumInIntervalFailingDatesWithinBudgetCheck() throws Exception {
        String expectedErrorMessage = "expectedErrorMessage";
        String from = "2022-06-06";
        String to = "2022-07-07";
        LocalDate dateFrom = LocalDate.of(2022, 6, 6);
        LocalDate dateTo = LocalDate.of(2022, 7, 7);

        when(dateManager.parse(from)).thenReturn(dateFrom);
        when(dateManager.parse(to)).thenReturn(dateTo);
        doThrow(new IllegalArgumentException(expectedErrorMessage))
                .when(dateManager).validateDatesWithinBudget(dateFrom, dateTo);

        String response = mockMvc.perform(get(EXPENSE_SUM_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .param("dateFrom", from)
                        .param("dateTo", to))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, BAD_REQUEST, expectedErrorMessage);

        verify(expenseService, never()).findSumInInterval(dateFrom, dateTo);
    }

    @Test
    void testTryGettingExpensesSumInIntervalFailingParsingOfDateParameter() throws Exception {
        String expectedErrorMessage = "expectedErrorMessage";
        String from = "2022-06-06";
        String to = "2022-07-07";
        LocalDate dateFrom = LocalDate.of(2022, 6, 6);
        LocalDate dateTo = LocalDate.of(2022, 7, 7);

        doThrow(new IllegalArgumentException(expectedErrorMessage))
                .when(dateManager).parse(from);

        String response = mockMvc.perform(get(EXPENSE_SUM_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .param("dateFrom", from)
                        .param("dateTo", to))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, BAD_REQUEST, expectedErrorMessage);

        verify(expenseService, never()).findSumInInterval(dateFrom, dateTo);
    }

    // Delete expense

    @Test
    void testDeleteExpenseById() throws Exception {
        mockMvc.perform(delete(EXPENSES_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .param("id", String.valueOf(DEFAULT_EXPENSE_ID)))
                .andExpect(status().isOk());

        verify(expenseService, times(1)).deleteById(DEFAULT_EXPENSE_ID);
    }

    @Test
    void testTryDeletingExpenseByNotExistingId() throws Exception {
        String expectedErrorMessage = "expectedErrorMessage";

        doThrow(new ObjectNotFoundException(expectedErrorMessage))
                .when(expenseService).deleteById(DEFAULT_EXPENSE_ID);

        String response = mockMvc.perform(delete(EXPENSES_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .param("id", String.valueOf(DEFAULT_EXPENSE_ID)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, NOT_FOUND, expectedErrorMessage);
    }

}
