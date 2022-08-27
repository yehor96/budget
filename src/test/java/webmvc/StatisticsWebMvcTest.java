package webmvc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import yehor.budget.common.date.DateManager;
import yehor.budget.service.StatisticsService;
import yehor.budget.web.dto.MonthlyStatistics;
import yehor.budget.web.dto.PeriodicStatistics;

import static common.factory.StatisticsFactory.defaultMonthlyStatistics;
import static common.factory.StatisticsFactory.defaultPeriodicStatistics;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StatisticsWebMvcTest extends BaseWebMvcTest {

    @MockBean
    private StatisticsService statisticsService;
    @MockBean
    private DateManager dateManager;

    // Get monthly statistics

    @Test
    void testGetMonthlyStatistics() throws Exception {
        MonthlyStatistics expectedMonthlyStatistics = defaultMonthlyStatistics();

        when(statisticsService.getMonthlyStatistics(any())).thenReturn(expectedMonthlyStatistics);

        String response = mockMvc.perform(get(MONTHLY_STATISTICS_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .param("month", "JANUARY")
                        .param("year", "2022"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        MonthlyStatistics actualMonthlyStatistics = objectMapper.readValue(response, MonthlyStatistics.class);

        verify(statisticsService, times(1)).getMonthlyStatistics(any());
        assertEquals(expectedMonthlyStatistics, actualMonthlyStatistics);
    }

    @Test
    void testGetMonthlyStatisticsWhenMonthOutsideOfBudget() throws Exception {
        String expectedErrorMessage = "expectedErrorMessage";

        doThrow(new IllegalArgumentException(expectedErrorMessage))
                .when(dateManager).validateMonthWithinBudget(any());

        String response = mockMvc.perform(get(MONTHLY_STATISTICS_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .param("month", "JUNE")
                        .param("year", "2022"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, BAD_REQUEST, expectedErrorMessage);
        verify(statisticsService, never()).getMonthlyStatistics(any());
    }

    // Get periodic statistics

    @Test
    void testGetPeriodicStatistics() throws Exception {
        PeriodicStatistics expectedPeriodicStatistics = defaultPeriodicStatistics();

        when(statisticsService.getPeriodicStatistics(any(), any())).thenReturn(expectedPeriodicStatistics);

        String response = mockMvc.perform(get(PERIODIC_STATISTICS_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .param("startMonth", "JUNE")
                        .param("startYear", "2022")
                        .param("endMonth", "JULY")
                        .param("endYear", "2022"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        PeriodicStatistics actualPeriodicStatistics = objectMapper.readValue(response, PeriodicStatistics.class);

        verify(statisticsService, times(1)).getPeriodicStatistics(any(), any());
        assertEquals(expectedPeriodicStatistics, actualPeriodicStatistics);
    }

    @Test
    void testTryGettingPeriodicStatisticsWhenMonthsNotInSequentialOrder() throws Exception {
        String expectedErrorMessage = "expectedErrorMessage";

        doThrow(new IllegalArgumentException(expectedErrorMessage))
                .when(dateManager).validateMonthsInSequentialOrder(any(), any());

        String response = mockMvc.perform(get(PERIODIC_STATISTICS_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .param("startMonth", "JUNE")
                        .param("startYear", "2022")
                        .param("endMonth", "JULY")
                        .param("endYear", "2022"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, BAD_REQUEST, expectedErrorMessage);
        verify(statisticsService, never()).getPeriodicStatistics(any(), any());
    }

    @Test
    void testTryGettingPeriodicStatisticsWhenMonthOutsideOfBudget() throws Exception {
        String expectedErrorMessage = "expectedErrorMessage";

        doThrow(new IllegalArgumentException(expectedErrorMessage))
                .when(dateManager).validateMonthWithinBudget(any());

        String response = mockMvc.perform(get(PERIODIC_STATISTICS_URL)
                        .header("Authorization", BASIC_AUTH_STRING)
                        .param("startMonth", "JUNE")
                        .param("startYear", "2022")
                        .param("endMonth", "JULY")
                        .param("endYear", "2022"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        verifyResponseErrorObject(response, BAD_REQUEST, expectedErrorMessage);
        verify(statisticsService, never()).getPeriodicStatistics(any(), any());
    }

}
