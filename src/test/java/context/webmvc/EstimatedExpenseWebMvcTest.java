package context.webmvc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import yehor.budget.service.EstimatedExpenseService;
import yehor.budget.web.dto.full.EstimatedExpenseFullDto;

import static common.factory.EstimatedExpenseFactory.defaultEstimatedExpenseFullDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EstimatedExpenseWebMvcTest extends BaseWebMvcTest {

    @MockBean
    private EstimatedExpenseService estimatedExpenseService;

    // Get estimated expense

    @Test
    void testGetEstimatedExpense() throws Exception {
        EstimatedExpenseFullDto expenseFullDto = defaultEstimatedExpenseFullDto();

        when(estimatedExpenseService.getOne()).thenReturn(expenseFullDto);

        String response = mockMvc.perform(get(ESTIMATED_EXPENSES_URL))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        EstimatedExpenseFullDto actualFullDto = objectMapper.readValue(response, EstimatedExpenseFullDto.class);

        verify(estimatedExpenseService, times(1)).getOne();
        assertEquals(expenseFullDto, actualFullDto);
    }
}
