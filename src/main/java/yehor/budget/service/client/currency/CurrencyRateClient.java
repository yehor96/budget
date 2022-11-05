package yehor.budget.service.client.currency;

import yehor.budget.common.Currency;

import java.math.BigDecimal;

public interface CurrencyRateClient {

    BigDecimal rate(Currency fromCurrency, Currency toCurrency);
}
