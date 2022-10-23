package yehor.budget.service.currency;

import java.math.BigDecimal;

public interface CurrencyRateClient {

    BigDecimal rate(Currency fromCurrency, Currency toCurrency);
}
