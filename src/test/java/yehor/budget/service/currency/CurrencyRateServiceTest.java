package yehor.budget.service.currency;

import org.junit.jupiter.api.Test;
import yehor.budget.common.Currency;
import yehor.budget.service.client.currency.CurrencyRateClient;
import yehor.budget.service.client.currency.CurrencyRateService;
import yehor.budget.service.client.currency.Exchangeable;

import java.math.BigDecimal;

import static common.factory.ExchangeableFactory.valueInUah;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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

        BigDecimal actualResult1 = currencyRateService.convert(from, to, value);
        BigDecimal actualResult2 = currencyRateService.convert(from, to, value);

        assertEquals(expectedResult, actualResult1);
        assertEquals(expectedResult, actualResult2);
        verify(currencyRateClient, times(1)).rate(from, to);
    }

    @Test
    void testGetValueInCurrencyReturnsInitialValueWhenRequiredCurrencyMatches() {
        Exchangeable exchangeable = valueInUah();
        BigDecimal expectedValue = exchangeable.getValue();

        BigDecimal actualValue = currencyRateService.getValueInCurrency(exchangeable, Currency.UAH);

        assertEquals(expectedValue, actualValue);
        verifyNoInteractions(currencyRateClient);
    }

    @Test
    void testGetValueInCurrencyReturnsExchangedValueWhenRequiredCurrencyDoesNotMatch() {
        Exchangeable exchangeable = valueInUah();
        BigDecimal rate = new BigDecimal("2");
        BigDecimal expectedValue = new BigDecimal("200.00");

        when(currencyRateClient.rate(any(), any())).thenReturn(rate);

        BigDecimal actualValue = currencyRateService.getValueInCurrency(exchangeable, Currency.USD);

        assertEquals(expectedValue, actualValue);
        verify(currencyRateClient, times(1))
                .rate(any(), any());
    }

}