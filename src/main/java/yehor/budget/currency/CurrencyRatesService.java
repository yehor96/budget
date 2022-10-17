package yehor.budget.currency;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class CurrencyRatesService {

    private final CurrencyRatesClient ratesClient;

    private final Map<CurrencyPair, BigDecimal> cachedRates;

    public BigDecimal getRate(Currency fromCurrency, Currency toCurrency, BigDecimal value) {
        CurrencyPair currencyPair = CurrencyPair.of(fromCurrency, toCurrency);
        BigDecimal rate = cachedRates.get(currencyPair);
        if (Objects.isNull(rate)) {
            rate = ratesClient.getRate(fromCurrency, toCurrency);
            cachedRates.put(currencyPair, rate);
        }
        return value.multiply(rate);
    }
}
