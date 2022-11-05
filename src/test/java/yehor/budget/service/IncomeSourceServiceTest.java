package yehor.budget.service;

import org.junit.jupiter.api.Test;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.util.ReflectionTestUtils;
import yehor.budget.common.Currency;
import yehor.budget.common.exception.ObjectAlreadyExistsException;
import yehor.budget.common.exception.ObjectNotFoundException;
import yehor.budget.entity.IncomeSource;
import yehor.budget.repository.IncomeSourceRepository;
import yehor.budget.web.converter.IncomeSourceConverter;
import yehor.budget.web.dto.TotalIncomeDto;
import yehor.budget.web.dto.full.IncomeSourceFullDto;
import yehor.budget.web.dto.limited.IncomeSourceLimitedDto;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static common.factory.IncomeSourceFactory.DEFAULT_BASE_CURRENCY;
import static common.factory.IncomeSourceFactory.defaultIncomeSource;
import static common.factory.IncomeSourceFactory.defaultIncomeSourceFullDto;
import static common.factory.IncomeSourceFactory.defaultIncomeSourceLimitedDto;
import static common.factory.IncomeSourceFactory.defaultTotalIncomeDto;
import static common.factory.IncomeSourceFactory.notBaseCurrencyIncomeSource;
import static common.factory.IncomeSourceFactory.notBaseCurrencyIncomeSourceFullDto;
import static common.factory.IncomeSourceFactory.totalIncomeWithNotBaseCurrencyDto;
import static common.factory.IncomeSourceFactory.secondIncomeSource;
import static common.factory.IncomeSourceFactory.secondIncomeSourceFullDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IncomeSourceServiceTest {

    private final IncomeSourceRepository incomeSourceRepositoryMock = mock(IncomeSourceRepository.class);
    private final IncomeSourceConverter incomeSourceConverterMock = mock(IncomeSourceConverter.class);
    private final CurrencyRateService currencyRateServiceMock = mock(CurrencyRateService.class);

    private final IncomeSourceService incomeSourceService =
            new IncomeSourceService(incomeSourceRepositoryMock, incomeSourceConverterMock, currencyRateServiceMock);

    @Test
    void testGetTotalIncome() {
        setBaseCurrency(DEFAULT_BASE_CURRENCY);
        TotalIncomeDto expectedTotalIncomeDto = defaultTotalIncomeDto();
        IncomeSourceFullDto expectedIncomeSourceDto1 = defaultIncomeSourceFullDto();
        IncomeSourceFullDto expectedIncomeSourceDto2 = secondIncomeSourceFullDto();
        IncomeSource expectedIncomeSource1 = defaultIncomeSource();
        IncomeSource expectedIncomeSource2 = secondIncomeSource();

        when(incomeSourceRepositoryMock.findAll()).thenReturn(List.of(expectedIncomeSource1, expectedIncomeSource2));
        when(incomeSourceConverterMock.convert(expectedIncomeSource1)).thenReturn(expectedIncomeSourceDto1);
        when(incomeSourceConverterMock.convert(expectedIncomeSource2)).thenReturn(expectedIncomeSourceDto2);

        TotalIncomeDto totalIncome = incomeSourceService.getTotalIncome();

        assertEquals(expectedTotalIncomeDto, totalIncome);
    }

    @Test
    void testGetTotalIncomeWithNotBaseCurrency() {
        setBaseCurrency(DEFAULT_BASE_CURRENCY);
        TotalIncomeDto expectedTotalIncomeDto = totalIncomeWithNotBaseCurrencyDto();
        IncomeSourceFullDto expectedIncomeSourceDto1 = defaultIncomeSourceFullDto();
        IncomeSourceFullDto expectedIncomeSourceDto2 = notBaseCurrencyIncomeSourceFullDto();
        IncomeSource expectedIncomeSource1 = defaultIncomeSource();
        IncomeSource expectedIncomeSource2 = notBaseCurrencyIncomeSource();

        when(incomeSourceRepositoryMock.findAll()).thenReturn(List.of(expectedIncomeSource1, expectedIncomeSource2));
        when(incomeSourceConverterMock.convert(expectedIncomeSource1)).thenReturn(expectedIncomeSourceDto1);
        when(incomeSourceConverterMock.convert(expectedIncomeSource2)).thenReturn(expectedIncomeSourceDto2);
        when(currencyRateServiceMock.convert(Currency.UAH, DEFAULT_BASE_CURRENCY, new BigDecimal("20.00")))
                .thenReturn(new BigDecimal("2.50"));

        TotalIncomeDto totalIncome = incomeSourceService.getTotalIncome();

        assertEquals(expectedTotalIncomeDto, totalIncome);
    }

    @Test
    void testGetEmptyTotalIncome() {
        setBaseCurrency(DEFAULT_BASE_CURRENCY);
        TotalIncomeDto expectedTotalIncomeDto = TotalIncomeDto.builder()
                .incomeSources(Collections.emptyList())
                .total(BigDecimal.ZERO)
                .totalCurrency(DEFAULT_BASE_CURRENCY)
                .build();

        when(incomeSourceRepositoryMock.findAll()).thenReturn(Collections.emptyList());

        TotalIncomeDto totalIncome = incomeSourceService.getTotalIncome();

        assertEquals(expectedTotalIncomeDto, totalIncome);
    }

    @Test
    void testSave() {
        IncomeSourceLimitedDto incomeSourceLimitedDto = defaultIncomeSourceLimitedDto();
        IncomeSource incomeSource = defaultIncomeSource();

        when(incomeSourceConverterMock.convert(incomeSourceLimitedDto)).thenReturn(incomeSource);

        incomeSourceService.save(incomeSourceLimitedDto);

        verify(incomeSourceRepositoryMock, times(1))
                .save(incomeSource);
    }

    @Test
    void testTrySavingExistingIncomeSource() {
        IncomeSourceLimitedDto expectedIncomeSourceLimitedDto = defaultIncomeSourceLimitedDto();
        IncomeSource expectedIncomeSource = defaultIncomeSource();

        when(incomeSourceConverterMock.convert(expectedIncomeSourceLimitedDto)).thenReturn(expectedIncomeSource);
        when(incomeSourceRepositoryMock.existsByName(expectedIncomeSource.getName())).thenReturn(true);

        try {
            incomeSourceService.save(expectedIncomeSourceLimitedDto);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(ObjectAlreadyExistsException.class, e.getClass());
            ObjectAlreadyExistsException exception = (ObjectAlreadyExistsException) e;
            assertEquals("Income source " + expectedIncomeSource.getName() + " already exists", exception.getMessage());
            verify(incomeSourceRepositoryMock, never())
                    .save(expectedIncomeSource);
        }
    }

    @Test
    void testDeleteIncomeSourceService() {
        incomeSourceService.delete(1L);
        verify(incomeSourceRepositoryMock, times(1))
                .deleteById(1L);
    }

    @Test
    void testTryDeletingNotExistingIncomeSource() {
        doThrow(new EmptyResultDataAccessException(1)).when(incomeSourceRepositoryMock).deleteById(1L);

        try {
            incomeSourceService.delete(1L);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(ObjectNotFoundException.class, e.getClass());
            ObjectNotFoundException exception = (ObjectNotFoundException) e;
            assertEquals("Income source with id " + 1L + " not found", exception.getMessage());
            verify(incomeSourceRepositoryMock, times(1))
                    .deleteById(1L);
        }
    }

    @Test
    void testUpdateIncomeSource() {
        IncomeSourceFullDto expectedIncomeSourceDto = defaultIncomeSourceFullDto();
        IncomeSource expectedIncomeSource = defaultIncomeSource();

        when(incomeSourceConverterMock.convert(expectedIncomeSourceDto)).thenReturn(expectedIncomeSource);
        when(incomeSourceRepositoryMock.existsById(expectedIncomeSourceDto.getId())).thenReturn(true);

        incomeSourceService.update(expectedIncomeSourceDto);

        verify(incomeSourceRepositoryMock, times(1))
                .save(expectedIncomeSource);
    }

    @Test
    void testTryUpdatingNotExistingIncomeSource() {
        IncomeSourceFullDto expectedIncomeSourceDto = defaultIncomeSourceFullDto();
        IncomeSource expectedIncomeSource = defaultIncomeSource();

        when(incomeSourceConverterMock.convert(expectedIncomeSourceDto)).thenReturn(expectedIncomeSource);
        when(incomeSourceRepositoryMock.existsById(expectedIncomeSourceDto.getId())).thenReturn(false);

        try {
            incomeSourceService.update(expectedIncomeSourceDto);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(ObjectNotFoundException.class, e.getClass());
            ObjectNotFoundException exception = (ObjectNotFoundException) e;
            assertEquals("Income source with id " + 1L + " does not exist", exception.getMessage());
            verify(incomeSourceRepositoryMock, never())
                    .save(expectedIncomeSource);
        }
    }

    private void setBaseCurrency(Currency currency) {
        ReflectionTestUtils.setField(incomeSourceService, "baseCurrency", currency, Currency.class);
    }
}
