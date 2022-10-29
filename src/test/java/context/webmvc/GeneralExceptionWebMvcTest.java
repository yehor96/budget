package context.webmvc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import yehor.budget.service.ExpenseService;

import static common.factory.ExpenseFactory.DEFAULT_EXPENSE_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GeneralExceptionWebMvcTest extends BaseWebMvcTest {

    @MockBean
    private ExpenseService expenseService;

    @Test
    void testGeneralExceptionIsThrown() throws Exception {
        when(expenseService.getById(any())).thenThrow(new RuntimeException());

        String response = mockMvc.perform(get(EXPENSES_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .param("id", String.valueOf(DEFAULT_EXPENSE_ID)))
                .andExpect(status().isInternalServerError())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, INTERNAL_SERVER_ERROR, "Unknown error occurred");
    }
}
