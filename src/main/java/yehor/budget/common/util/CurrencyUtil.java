package yehor.budget.common.util;

import lombok.experimental.UtilityClass;
import yehor.budget.common.Currency;

@UtilityClass
public class CurrencyUtil {

    public static String currencyPair(Currency from, Currency to) {
        return from + ":" + to;
    }
}
