package common.response.exchangerate;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDate;

@UtilityClass
public class ExchangeRateResponseProvider {

    public String responseBody(String from, String to, BigDecimal rate) {
        return String.format("""
                {
                  "motd": {
                    "msg": "Some test information",
                    "url": "https://exchangerate.host/#/donate"
                  },
                  "success": true,
                  "query": {
                    "from": "%s",
                    "to": "%s",
                    "amount": 1
                  },
                  "info": {
                    "rate": %f
                  },
                  "historical": false,
                  "date": "%s",
                  "result": %f
                }
                """, from, to, rate, LocalDate.now(), rate);
    }
}
