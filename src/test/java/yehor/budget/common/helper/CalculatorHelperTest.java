package yehor.budget.common.helper;

import org.junit.jupiter.api.Test;
import yehor.budget.common.util.CalculatorHelper;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class CalculatorHelperTest {

    private final CalculatorHelper calculatorHelper = new CalculatorHelper();

    private final List<BigDecimal> values = List.of(BigDecimal.valueOf(2), BigDecimal.valueOf(3), BigDecimal.valueOf(25));

    @Test
    void testSumWithList() {
        BigDecimal result = calculatorHelper.sum(values);
        assertEquals(BigDecimal.valueOf(30), result);
    }

    @Test
    void testSumWithVararg() {
        BigDecimal result = calculatorHelper.sum(values.get(0), values.get(1), values.get(2));
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

    @Test
    void testAverageWithDivider() {
        BigDecimal result = calculatorHelper.divide(BigDecimal.valueOf(50), BigDecimal.valueOf(10));
        assertEquals(BigDecimal.valueOf(5), result);
    }

    @Test
    void testAverageWithDividerZero() {
        try {
            calculatorHelper.divide(BigDecimal.valueOf(50), BigDecimal.ZERO);
            fail("Exception not thrown");
        } catch (Exception e) {
            assertEquals(ArithmeticException.class, e.getClass());
            ArithmeticException exception = (ArithmeticException) e;
            assertEquals("/ by zero", exception.getMessage());
        }
    }

}