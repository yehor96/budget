package yehor.budget.currency;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public interface CurrencyRatesClient {

    BigDecimal getRate(Currency fromCurrency, Currency toCurrency);
}
