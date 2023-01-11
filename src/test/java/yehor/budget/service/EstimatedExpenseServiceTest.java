package yehor.budget.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import yehor.budget.common.util.CalculatorHelper;
import yehor.budget.entity.RowEstimatedExpense;
import yehor.budget.repository.RowEstimatedExpenseRepository;
import yehor.budget.web.converter.EstimatedExpenseConverter;
import yehor.budget.web.dto.full.EstimatedExpenseFullDto;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static common.factory.EstimatedExpenseFactory.defaultEstimatedExpenseFullDto;
import static common.factory.EstimatedExpenseFactory.defaultRowEstimatedExpense;
import static common.factory.EstimatedExpenseFactory.defaultRowEstimatedExpenseFullDto;
import static common.factory.EstimatedExpenseFactory.secondRowEstimatedExpense;
import static common.factory.EstimatedExpenseFactory.secondRowEstimatedExpenseFullDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EstimatedExpenseServiceTest {

    private final RowEstimatedExpenseRepository rowEstimatedExpenseRepositoryMock =
            mock(RowEstimatedExpenseRepository.class);
    private final EstimatedExpenseConverter estimatedExpenseConverterMock =
            mock(EstimatedExpenseConverter.class);
    private final CalculatorHelper calculatorHelperMock = mock(CalculatorHelper.class);
    private final CurrencyRateService currencyRateServiceMock = mock(CurrencyRateService.class);

    private final EstimatedExpenseService estimatedExpenseService = new EstimatedExpenseService(
            rowEstimatedExpenseRepositoryMock, estimatedExpenseConverterMock,
            calculatorHelperMock, currencyRateServiceMock);

    @Test
    void testGetOne() {
        EstimatedExpenseFullDto expectedFullDto = defaultEstimatedExpenseFullDto();

        when(rowEstimatedExpenseRepositoryMock.findAll())
                .thenReturn(List.of(defaultRowEstimatedExpense(), secondRowEstimatedExpense()));
        when(estimatedExpenseConverterMock.convert(any(RowEstimatedExpense.class)))
                .thenReturn(defaultRowEstimatedExpenseFullDto())
                .thenReturn(secondRowEstimatedExpenseFullDto());

        when(calculatorHelperMock.sum(anyList()))
                .thenReturn(new BigDecimal("51.00"))
                .thenReturn(new BigDecimal("105.00"))
                .thenReturn(new BigDecimal("220.00"))
                .thenReturn(new BigDecimal("40.00"));
        when(calculatorHelperMock.sum(ArgumentMatchers.<BigDecimal>any())) // matching vararg scenario
                .thenReturn(new BigDecimal("416.00"));
        when(currencyRateServiceMock.convert(any(), any(), any()))
                .thenReturn(new BigDecimal("11.25"));

        EstimatedExpenseFullDto actualFullDto = estimatedExpenseService.getOne();

        assertEquals(expectedFullDto, actualFullDto);
    }

    @Test
    void testGetOneEmpty() {
        EstimatedExpenseFullDto expectedFullDto = EstimatedExpenseFullDto.builder()
                .rows(Collections.emptyList())
                .total1to7(BigDecimal.ZERO)
                .total8to14(BigDecimal.ZERO)
                .total15to21(BigDecimal.ZERO)
                .total22to31(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .totalUsd(BigDecimal.ZERO)
                .build();

        when(rowEstimatedExpenseRepositoryMock.findAll())
                .thenReturn(Collections.emptyList());
        when(estimatedExpenseConverterMock.convert(any(RowEstimatedExpense.class)))
                .thenReturn(defaultRowEstimatedExpenseFullDto())
                .thenReturn(secondRowEstimatedExpenseFullDto());

        when(calculatorHelperMock.sum(anyList()))
                .thenReturn(BigDecimal.ZERO);
        when(calculatorHelperMock.sum(ArgumentMatchers.<BigDecimal>any())) // matching vararg scenario
                .thenReturn(BigDecimal.ZERO);
        when(currencyRateServiceMock.convert(any(), any(), any()))
                .thenReturn(BigDecimal.ZERO);

        EstimatedExpenseFullDto actualFullDto = estimatedExpenseService.getOne();

        assertEquals(expectedFullDto, actualFullDto);
    }
}