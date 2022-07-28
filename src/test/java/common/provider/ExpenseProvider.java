package common.provider;

import lombok.experimental.UtilityClass;
import yehor.budget.web.dto.full.ExpenseFullDto;
import yehor.budget.web.dto.limited.ExpenseLimitedDto;

import java.math.BigDecimal;
import java.time.LocalDate;

@UtilityClass
public class ExpenseProvider {

    private static final long DEFAULT_ID = 1L;
    private static final long DEFAULT_CATEGORY_ID = 1L;

    public static ExpenseFullDto defaultExpenseFullDto() {
        return ExpenseFullDto.builder()
                .id(DEFAULT_ID)
                .value(new BigDecimal("10.00"))
                .date(LocalDate.now())
                .isRegular(true)
                .categoryId(DEFAULT_CATEGORY_ID)
                .build();
    }

    public static ExpenseLimitedDto defaultExpenseLimitedDto() {
        return ExpenseLimitedDto.builder()
                .value(new BigDecimal("10.00"))
                .date(LocalDate.now())
                .isRegular(true)
                .categoryId(DEFAULT_CATEGORY_ID)
                .build();
    }

}
