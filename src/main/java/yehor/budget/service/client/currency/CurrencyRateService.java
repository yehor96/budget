package yehor.budget.service.client.currency;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import yehor.budget.common.Currency;
import yehor.budget.common.util.CurrencyUtil;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static java.util.concurrent.TimeUnit.MINUTES;

@RequiredArgsConstructor
@Component
@Slf4j
public class CurrencyRateService {

    private final CurrencyRateClient currencyRateClient;

    private final Map<String, BigDecimal> cachedRates = new ConcurrentHashMap<>();

    public BigDecimal convert(Currency fromCurrency, Currency toCurrency, BigDecimal value) {
        String currencyPair = CurrencyUtil.currencyPair(fromCurrency, toCurrency);
        BigDecimal rate = cachedRates.get(currencyPair);
        if (Objects.isNull(rate)) {
            rate = currencyRateClient.rate(fromCurrency, toCurrency);
            cachedRates.put(currencyPair, rate);
        }
        return value.multiply(rate);
    }

    public BigDecimal getValueInCurrency(Exchangeable exchangeable, Currency requiredCurrency) {
        Currency actualCurrency = exchangeable.getCurrency();
        BigDecimal value = exchangeable.getValue();
        if (actualCurrency == requiredCurrency) {
            return value;
        } else {
            return convert(actualCurrency, requiredCurrency, value);
        }
    }

    @PostConstruct
    private void cacheEvictionScheduler() {
        new ScheduledThreadPoolExecutor(1)
                .scheduleAtFixedRate(() -> {
                    log.info("Evicting cached rates");
                    cachedRates.clear();
                }, 60, 60, MINUTES);
    }

}