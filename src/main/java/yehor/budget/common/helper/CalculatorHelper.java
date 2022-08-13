package yehor.budget.common.helper;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class CalculatorHelper {

    public BigDecimal average(List<BigDecimal> bigDecimals) {
        if (bigDecimals.isEmpty()) {
//            new BigDecimal(2.55d);
//            System.out.println("asd");
            return BigDecimal.ZERO;
        }
        BigDecimal sum = sum(bigDecimals);
        return sum.divide(new BigDecimal(bigDecimals.size()), RoundingMode.HALF_EVEN);
    }

    public BigDecimal sum(List<BigDecimal> bigDecimals) {
        return bigDecimals.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
