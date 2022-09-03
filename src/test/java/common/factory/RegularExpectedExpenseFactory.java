package common.factory;

import lombok.experimental.UtilityClass;
import yehor.budget.entity.RowRegularExpectedExpense;
import yehor.budget.web.dto.full.RegularExpectedExpenseFullDto;
import yehor.budget.web.dto.full.RowRegularExpectedExpenseFullDto;

import java.math.BigDecimal;
import java.util.List;

import static common.factory.CategoryFactory.DEFAULT_CATEGORY_ID;
import static common.factory.CategoryFactory.defaultCategory;
import static common.factory.CategoryFactory.secondCategory;

@UtilityClass
public class RegularExpectedExpenseFactory {

    public static final long DEFAULT_ROW_REGULAR_EXPECTED_EXPENSE_ID = 1L;

    public static RegularExpectedExpenseFullDto defaultRegularExpectedExpenseFullDto() {
        return RegularExpectedExpenseFullDto.builder()
                .rows(List.of(defaultRowRegularExpectedExpenseFullDto(), secondRowRegularExpectedExpenseFullDto()))
                .total1to7(new BigDecimal("51.00"))
                .total8to14(new BigDecimal("105.00"))
                .total15to21(new BigDecimal("220.00"))
                .total22to31(new BigDecimal("40.00"))
                .total(new BigDecimal("416.00"))
                .build();
    }

    public static RowRegularExpectedExpenseFullDto defaultRowRegularExpectedExpenseFullDto() {
        return RowRegularExpectedExpenseFullDto.builder()
                .categoryId(DEFAULT_CATEGORY_ID)
                .days1to7(new BigDecimal("50.00"))
                .days8to14(new BigDecimal("100.00"))
                .days15to21(new BigDecimal("20.00"))
                .days22to31(BigDecimal.TEN)
                .totalPerRow(new BigDecimal("180.00"))
                .build();
    }

    public static RowRegularExpectedExpenseFullDto secondRowRegularExpectedExpenseFullDto() {
        return RowRegularExpectedExpenseFullDto.builder()
                .categoryId(2L)
                .days1to7(BigDecimal.ONE)
                .days8to14(new BigDecimal("5.00"))
                .days15to21(new BigDecimal("200.00"))
                .days22to31(new BigDecimal("30.00"))
                .totalPerRow(new BigDecimal("236.00"))
                .build();
    }

    public static RowRegularExpectedExpense defaultRowRegularExpectedExpense() {
        return RowRegularExpectedExpense.builder()
                .id(DEFAULT_ROW_REGULAR_EXPECTED_EXPENSE_ID)
                .category(defaultCategory())
                .days1to7(new BigDecimal("50.00"))
                .days8to14(new BigDecimal("100.00"))
                .days15to21(new BigDecimal("20.00"))
                .days22to31(BigDecimal.TEN)
                .build();
    }

    public static RowRegularExpectedExpense secondRowRegularExpectedExpense() {
        return RowRegularExpectedExpense.builder()
                .id(2L)
                .category(secondCategory())
                .days1to7(BigDecimal.ONE)
                .days8to14(new BigDecimal("5.00"))
                .days15to21(new BigDecimal("200.00"))
                .days22to31(new BigDecimal("30.00"))
                .build();
    }
}
