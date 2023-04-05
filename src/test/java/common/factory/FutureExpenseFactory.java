package common.factory;

import lombok.experimental.UtilityClass;
import yehor.budget.entity.FutureExpense;

import java.math.BigDecimal;
import java.time.LocalDate;

@UtilityClass
public class FutureExpenseFactory {

    public static final long DEFAULT_FUTURE_EXPENSE_ID = 1L;

    public static FutureExpense defaultFutureExpenseWithDate() {
        return FutureExpense.builder()
                .id(DEFAULT_FUTURE_EXPENSE_ID)
                .value(new BigDecimal("1000.00"))
                .date(LocalDate.now())
                .build();
    }

    public static FutureExpense secondFutureExpenseWithDate() {
        return FutureExpense.builder()
                .id(2L)
                .value(new BigDecimal("550.00"))
                .date(LocalDate.now())
                .build();
    }
}
