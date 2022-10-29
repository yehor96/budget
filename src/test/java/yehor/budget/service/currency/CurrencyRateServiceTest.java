package yehor.budget.service.currency;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CurrencyRateServiceTest {

    private final CurrencyRateClient currencyRateClient = mock(CurrencyRateClient.class);

    private final CurrencyRateService currencyRateService = new CurrencyRateService(currencyRateClient);

    @Test
    void getRateCachesRates() {
        Currency from = Currency.UAH;
        Currency to = Currency.USD;
        BigDecimal value = BigDecimal.valueOf(5);
        BigDecimal expectedResult = BigDecimal.valueOf(50);

        when(currencyRateClient.rate(from, to)).thenReturn(BigDecimal.TEN);

        BigDecimal actualResult1 = currencyRateService.getRate(from, to, value);
        BigDecimal actualResult2 = currencyRateService.getRate(from, to, value);

        assertEquals(expectedResult, actualResult1);
        assertEquals(expectedResult, actualResult2);
        verify(currencyRateClient, times(1)).rate(from, to);
    }

}