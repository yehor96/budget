package yehor.budget.service.client.currency;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import yehor.budget.common.Currency;
import yehor.budget.common.util.CurrencyUtil;

import java.math.BigDecimal;
import java.util.Map;

import static yehor.budget.common.Currency.UAH;
import static yehor.budget.common.Currency.USD;

@RequiredArgsConstructor
@Component
public class CurrencyRateSimulator implements CurrencyRateClient {

    private final Map<String, BigDecimal> simulatedRates = Map.of(
            CurrencyUtil.currencyPair(USD, UAH), BigDecimal.valueOf(36.6),
            CurrencyUtil.currencyPair(UAH, USD), BigDecimal.valueOf(0.027)
    );

    @Override
    public BigDecimal rate(Currency fromCurrency, Currency toCurrency) {
        String currencyPair = CurrencyUtil.currencyPair(fromCurrency, toCurrency);
        return simulatedRates.get(currencyPair);
    }
}
