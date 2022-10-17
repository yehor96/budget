package yehor.budget.currency;

import lombok.Data;

@Data
public class CurrencyPair {
    private final Currency fromCurrency;
    private final Currency toCurrency;

    public static CurrencyPair of(Currency fromCurrency, Currency toCurrency) {
        return new CurrencyPair(fromCurrency, toCurrency);
    }

    @Override
    public String toString() {
        return fromCurrency.toString().concat(":").concat(toCurrency.toString());
    }
}
