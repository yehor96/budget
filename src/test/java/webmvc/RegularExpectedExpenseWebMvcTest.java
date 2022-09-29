package webmvc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import yehor.budget.service.RegularExpectedExpenseService;
import yehor.budget.web.dto.full.RegularExpectedExpenseFullDto;

import static common.factory.RegularExpectedExpenseFactory.defaultRegularExpectedExpenseFullDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RegularExpectedExpenseWebMvcTest extends BaseWebMvcTest {

    @MockBean
    private RegularExpectedExpenseService regularExpectedExpenseService;

    // Get regular expected expense

    @Test
    void testGetRegularExpectedExpense() throws Exception {
        RegularExpectedExpenseFullDto expenseFullDto = defaultRegularExpectedExpenseFullDto();

        when(regularExpectedExpenseService.getOne()).thenReturn(expenseFullDto);

        String response = mockMvc.perform(get(REGULAR_EXPECTED_EXPENSES_URL)
                        .header("Authorization", BASIC_AUTH_STRING))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        RegularExpectedExpenseFullDto actualFullDto = objectMapper.readValue(response, RegularExpectedExpenseFullDto.class);

        verify(regularExpectedExpenseService, times(1)).getOne();
        assertEquals(expenseFullDto, actualFullDto);
    }
}
