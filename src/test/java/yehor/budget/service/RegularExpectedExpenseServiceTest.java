package yehor.budget.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import yehor.budget.common.helper.CalculatorHelper;
import yehor.budget.entity.RowRegularExpectedExpense;
import yehor.budget.repository.RowRegularExpectedExpenseRepository;
import yehor.budget.web.converter.RegularExpectedExpenseConverter;
import yehor.budget.web.dto.full.RegularExpectedExpenseFullDto;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static common.factory.RegularExpectedExpenseFactory.defaultRegularExpectedExpenseFullDto;
import static common.factory.RegularExpectedExpenseFactory.defaultRowRegularExpectedExpense;
import static common.factory.RegularExpectedExpenseFactory.defaultRowRegularExpectedExpenseFullDto;
import static common.factory.RegularExpectedExpenseFactory.secondRowRegularExpectedExpense;
import static common.factory.RegularExpectedExpenseFactory.secondRowRegularExpectedExpenseFullDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RegularExpectedExpenseServiceTest {

    private final RowRegularExpectedExpenseRepository rowRegularExpectedExpenseRepositoryMock =
            mock(RowRegularExpectedExpenseRepository.class);
    private final RegularExpectedExpenseConverter regularExpectedExpenseConverterMock =
            mock(RegularExpectedExpenseConverter.class);
    private final CalculatorHelper calculatorHelperMock = mock(CalculatorHelper.class);

    private final RegularExpectedExpenseService regularExpectedExpenseService = new RegularExpectedExpenseService(
            rowRegularExpectedExpenseRepositoryMock, regularExpectedExpenseConverterMock, calculatorHelperMock);

    @Test
    void testGetOne() {
        RegularExpectedExpenseFullDto expectedFullDto = defaultRegularExpectedExpenseFullDto();

        when(rowRegularExpectedExpenseRepositoryMock.findAll())
                .thenReturn(List.of(defaultRowRegularExpectedExpense(), secondRowRegularExpectedExpense()));
        when(regularExpectedExpenseConverterMock.convert(any(RowRegularExpectedExpense.class)))
                .thenReturn(defaultRowRegularExpectedExpenseFullDto())
                .thenReturn(secondRowRegularExpectedExpenseFullDto());

        when(calculatorHelperMock.sum(anyList()))
                .thenReturn(new BigDecimal("51.00"))
                .thenReturn(new BigDecimal("105.00"))
                .thenReturn(new BigDecimal("220.00"))
                .thenReturn(new BigDecimal("40.00"));
        when(calculatorHelperMock.sum(ArgumentMatchers.<BigDecimal>any())) // matching vararg scenario
                .thenReturn(new BigDecimal("416.00"));

        RegularExpectedExpenseFullDto actualFullDto = regularExpectedExpenseService.getOne();

        assertEquals(expectedFullDto, actualFullDto);
    }

    @Test
    void testGetOneEmpty() {
        RegularExpectedExpenseFullDto expectedFullDto = RegularExpectedExpenseFullDto.builder()
                .rows(Collections.emptyList())
                .total1to7(BigDecimal.ZERO)
                .total8to14(BigDecimal.ZERO)
                .total15to21(BigDecimal.ZERO)
                .total22to31(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .build();

        when(rowRegularExpectedExpenseRepositoryMock.findAll())
                .thenReturn(Collections.emptyList());
        when(regularExpectedExpenseConverterMock.convert(any(RowRegularExpectedExpense.class)))
                .thenReturn(defaultRowRegularExpectedExpenseFullDto())
                .thenReturn(secondRowRegularExpectedExpenseFullDto());

        when(calculatorHelperMock.sum(anyList()))
                .thenReturn(BigDecimal.ZERO);
        when(calculatorHelperMock.sum(ArgumentMatchers.<BigDecimal>any())) // matching vararg scenario
                .thenReturn(BigDecimal.ZERO);

        RegularExpectedExpenseFullDto actualFullDto = regularExpectedExpenseService.getOne();

        assertEquals(expectedFullDto, actualFullDto);
    }
}