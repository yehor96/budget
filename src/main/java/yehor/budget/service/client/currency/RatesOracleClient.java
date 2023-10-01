package yehor.budget.service.client.currency;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import yehor.budget.common.Currency;
import yehor.budget.common.exception.InternalClientException;

import java.math.BigDecimal;

@Slf4j
@Component
public class RatesOracleClient implements CurrencyRateClient {

    @Value("${rates.oracle.host.url}")
    private String baseUrl;

    @Value("${rates.oracle.host.key}")
    private String apiKey;

    @Override
    public BigDecimal rate(Currency fromCurrency, Currency toCurrency) {
        try {
            HttpResponse<JsonNode> httpResponse = Unirest.get(baseUrl)
                    .header("apikey", apiKey)
                    .queryString("from", fromCurrency)
                    .queryString("to", toCurrency)
                    .queryString("amount", 1)
                    .asJson();
            int status = httpResponse.getStatus();
            if (status != 200) {
                String statusText = httpResponse.getStatusText();
                throw new UnirestException(
                        String.format("Failed to get currency rates. %d status with msg: %s", status, statusText));
            }
            double rate = httpResponse.getBody().getObject().getDouble("result");
            log.info("Received currency rates {}:{} {}", fromCurrency, toCurrency, rate);
            return BigDecimal.valueOf(rate);
        } catch (Exception e) {
            log.error("Not able to perform a request to " + baseUrl, e);
            throw new InternalClientException("Not able to perform a request to " + baseUrl, e);
        }
    }
}
