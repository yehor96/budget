package yehor.budget.common.util;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class CalculatorHelper {

    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;

    public BigDecimal average(List<BigDecimal> bigDecimals) {
        if (bigDecimals.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal sum = sum(bigDecimals);
        return sum.divide(new BigDecimal(bigDecimals.size()), ROUNDING_MODE);
    }

    public BigDecimal divide(BigDecimal value, BigDecimal divider) {
        if (BigDecimal.ZERO.equals(value)) {
            return BigDecimal.ZERO;
        }
        return value.divide(divider, ROUNDING_MODE);
    }

    public BigDecimal sum(List<BigDecimal> bigDecimals) {
        return bigDecimals.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal sum(BigDecimal... bigDecimals) {
        return sum(List.of(bigDecimals));
    }
}
