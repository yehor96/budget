package common.factory;

import yehor.budget.common.Currency;
import yehor.budget.service.client.currency.Exchangeable;
import yehor.budget.web.dto.full.IncomeSourceFullDto;

import java.math.BigDecimal;

public class ExchangeableFactory {

    public static Exchangeable valueInUah() {
        return IncomeSourceFullDto.builder()
                .currency(Currency.UAH)
                .value(new BigDecimal("100.00"))
                .build();
    }

    public static Exchangeable valueInUsd() {
        return IncomeSourceFullDto.builder()
                .currency(Currency.USD)
                .value(new BigDecimal("100.00"))
                .build();
    }
}
