package yehor.budget.web.dto.full;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@ToString
@EqualsAndHashCode
public final class BalanceEstimateDto {

    @Getter
    private final BigDecimal previousTotal;
    @Getter
    private final BigDecimal expenseByEndOfMonth;
    @Getter
    private final BigDecimal incomeByEndOfMonth;
    @Getter
    private final BigDecimal profitByEndOfMonth;
    @Getter
    private final LocalDate endOfMonthDate;

    public BalanceEstimateDto(BigDecimal previousTotal,
                              BigDecimal expenseByEndOfMonth,
                              BigDecimal incomeByEndOfMonth,
                              LocalDate endOfMonthDate) {
        this.previousTotal = previousTotal;
        this.expenseByEndOfMonth = expenseByEndOfMonth;
        this.incomeByEndOfMonth = incomeByEndOfMonth;
        this.profitByEndOfMonth = previousTotal.add(incomeByEndOfMonth).subtract(expenseByEndOfMonth);
        this.endOfMonthDate = endOfMonthDate;
    }
}
