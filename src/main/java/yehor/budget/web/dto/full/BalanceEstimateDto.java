package yehor.budget.web.dto.full;

import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@ToString
public final class BalanceEstimateDto {

    @Getter
    private final BigDecimal previousMonthTotal;
    @Getter
    private final BigDecimal expenseByEOM;
    @Getter
    private final BigDecimal incomeByEOM;
    @Getter
    private final BigDecimal profitByEOM;
    @Getter
    private final LocalDate endOfMonthDate;

    public BalanceEstimateDto(BigDecimal previousMonthTotal,
                              BigDecimal expenseByEOM,
                              BigDecimal incomeByEOM,
                              LocalDate endOfMonthDate) {
        this.previousMonthTotal = previousMonthTotal;
        this.expenseByEOM = expenseByEOM;
        this.incomeByEOM = incomeByEOM;
        this.profitByEOM = previousMonthTotal.add(incomeByEOM).subtract(expenseByEOM);
        this.endOfMonthDate = endOfMonthDate;
    }
}
