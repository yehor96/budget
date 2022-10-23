package yehor.budget.service.currency;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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

    private final CurrencyRateClient ratesClient;

    private final Map<String, BigDecimal> cachedRates = new ConcurrentHashMap<>();

    public BigDecimal getRate(Currency fromCurrency, Currency toCurrency, BigDecimal value) {
        String currencyPair = fromCurrency + ":" + toCurrency;
        BigDecimal rate = cachedRates.get(currencyPair);
        if (Objects.isNull(rate)) {
            rate = ratesClient.rate(fromCurrency, toCurrency);
            cachedRates.put(currencyPair, rate);
        }
        return value.multiply(rate);
    }

    @PostConstruct
    private void cacheEvictionScheduler() {
        new ScheduledThreadPoolExecutor(1)
                .scheduleAtFixedRate(() -> {
                    log.info("Evicting rates cache");
                    cachedRates.clear();
                }, 60, 60, MINUTES);
    }

}