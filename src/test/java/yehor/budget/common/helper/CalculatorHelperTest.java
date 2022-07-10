package yehor.budget.common.helper;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculatorHelperTest {

    private final CalculatorHelper calculatorHelper = new CalculatorHelper();

    private final List<BigDecimal> values = List.of(BigDecimal.valueOf(2), BigDecimal.valueOf(3), BigDecimal.valueOf(25));

    @Test
    void testSum() {
        BigDecimal result = calculatorHelper.sum(values);
        assertEquals(BigDecimal.valueOf(30), result);
    }

    @Test
    void testSumWithEmptyList() {
        BigDecimal result = calculatorHelper.sum(Collections.emptyList());
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void testAverage() {
        BigDecimal result = calculatorHelper.average(values);
        assertEquals(BigDecimal.valueOf(10), result);
    }

    @Test
    void testAverageEmptyList() {
        BigDecimal result = calculatorHelper.average(Collections.emptyList());
        assertEquals(BigDecimal.ZERO, result);
    }

}