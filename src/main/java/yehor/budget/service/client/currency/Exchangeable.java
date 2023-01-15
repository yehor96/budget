package yehor.budget.service.client.currency;

import yehor.budget.common.Currency;

import java.math.BigDecimal;

public interface Exchangeable {
    Currency getCurrency();
    BigDecimal getValue();
}
