package common.factory;

import lombok.experimental.UtilityClass;
import yehor.budget.entity.RowEstimatedExpense;
import yehor.budget.web.dto.full.EstimatedExpenseFullDto;
import yehor.budget.web.dto.full.RowEstimatedExpenseFullDto;

import java.math.BigDecimal;
import java.util.List;

import static common.factory.CategoryFactory.DEFAULT_CATEGORY_ID;
import static common.factory.CategoryFactory.defaultCategory;
import static common.factory.CategoryFactory.secondCategory;

@UtilityClass
public class EstimatedExpenseFactory {

    public static final long DEFAULT_ROW_ESTIMATED_EXPENSE_ID = 1L;

    public static EstimatedExpenseFullDto defaultEstimatedExpenseFullDto() {
        return EstimatedExpenseFullDto.builder()
                .rows(List.of(defaultRowEstimatedExpenseFullDto(), secondRowEstimatedExpenseFullDto()))
                .total1to7(new BigDecimal("51.00"))
                .total8to14(new BigDecimal("105.00"))
                .total15to21(new BigDecimal("220.00"))
                .total22to31(new BigDecimal("40.00"))
                .total(new BigDecimal("416.00"))
                .totalUsd(new BigDecimal("11.25"))
                .build();
    }

    public static RowEstimatedExpenseFullDto defaultRowEstimatedExpenseFullDto() {
        return RowEstimatedExpenseFullDto.builder()
                .categoryId(DEFAULT_CATEGORY_ID)
                .days1to7(new BigDecimal("50.00"))
                .days8to14(new BigDecimal("100.00"))
                .days15to21(new BigDecimal("20.00"))
                .days22to31(BigDecimal.TEN)
                .totalPerRow(new BigDecimal("180.00"))
                .build();
    }

    public static RowEstimatedExpenseFullDto secondRowEstimatedExpenseFullDto() {
        return RowEstimatedExpenseFullDto.builder()
                .categoryId(2L)
                .days1to7(BigDecimal.ONE)
                .days8to14(new BigDecimal("5.00"))
                .days15to21(new BigDecimal("200.00"))
                .days22to31(new BigDecimal("30.00"))
                .totalPerRow(new BigDecimal("236.00"))
                .build();
    }

    public static RowEstimatedExpense defaultRowEstimatedExpense() {
        return RowEstimatedExpense.builder()
                .id(DEFAULT_ROW_ESTIMATED_EXPENSE_ID)
                .category(defaultCategory())
                .days1to7(new BigDecimal("50.00"))
                .days8to14(new BigDecimal("100.00"))
                .days15to21(new BigDecimal("20.00"))
                .days22to31(BigDecimal.TEN)
                .build();
    }

    public static RowEstimatedExpense secondRowEstimatedExpense() {
        return RowEstimatedExpense.builder()
                .id(2L)
                .category(secondCategory())
                .days1to7(BigDecimal.ONE)
                .days8to14(new BigDecimal("5.00"))
                .days15to21(new BigDecimal("200.00"))
                .days22to31(new BigDecimal("30.00"))
                .build();
    }
}
