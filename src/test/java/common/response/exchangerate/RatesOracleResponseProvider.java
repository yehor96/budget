package common.response.exchangerate;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.sql.Timestamp;

@UtilityClass
public class RatesOracleResponseProvider {

    public String responseBody(String from, String to, BigDecimal rate) {
        return String.format("""    
                {
                  "success": true,
                  "query": {
                    "from": "%s",
                    "to": "%s",
                    "amount": 1
                  },
                  "info": {
                    "timestamp": "%s",
                    "quote": "%f"
                  },
                  "result": "%f"
                }
                """, from, to, new Timestamp(System.currentTimeMillis()), rate, rate);
    }
}
