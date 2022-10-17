package yehor.budget.currency;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class ExchangeRateHostCurrencyRatesClient implements CurrencyRatesClient {

    private static final String BASE_URL = "https://api.exchangerate.host/convert";

    @Override
    public BigDecimal getRate(Currency fromCurrency, Currency toCurrency) {
        try {
            HttpResponse<JsonNode> httpResponse = Unirest.get(BASE_URL)
                    .queryString("from", fromCurrency)
                    .queryString("to", toCurrency)
                    .asJson();
            if (httpResponse.getStatus() != 200) {
                log.error("Failed response");
                throw new UnirestException("200 code");
            }
            JSONObject jsonObject = new JSONObject(httpResponse.getBody());
            return jsonObject.getJSONObject("object").getJSONObject("info").getBigDecimal("rate");
        } catch (UnirestException e) {
            throw new RuntimeException("Not able to perform a request");
        }
    }
}
