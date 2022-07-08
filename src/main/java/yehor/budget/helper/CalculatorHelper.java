package yehor.budget.helper;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

@Component
public class CalculatorHelper {

    public BigDecimal average(List<BigDecimal> bigDecimals) {
        if (bigDecimals.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal sum = bigDecimals.stream()
                .map(Objects::requireNonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(new BigDecimal(bigDecimals.size()), RoundingMode.HALF_EVEN);
    }

    public BigDecimal sum(List<BigDecimal> bigDecimals) {
        return bigDecimals.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
