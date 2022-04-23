package yehor.budget.service;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import yehor.budget.entity.DailyExpense;
import yehor.budget.exception.CustomExceptionManager.CustomException;
import yehor.budget.repository.ExpenseRepository;
import yehor.budget.web.converter.ExpenseConverter;
import yehor.budget.web.dto.DailyExpenseDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExpenseServiceTest {

    private final ExpenseRepository expenseRepositoryMock = mock(ExpenseRepository.class);
    private final ExpenseConverter expenseConverterMock = mock(ExpenseConverter.class);

    private final ExpenseService expenseService = new ExpenseService(expenseConverterMock, expenseRepositoryMock);

    @Test
    void testFindByDate() {
        DailyExpenseDto expectedDailyExpenseDto = new DailyExpenseDto(10, LocalDate.now());
        DailyExpense dailyExpense = new DailyExpense(10, LocalDate.now());

        when(expenseRepositoryMock.findOne(LocalDate.now())).thenReturn(Optional.of(dailyExpense));
        when(expenseConverterMock.convertToDto(dailyExpense)).thenReturn(expectedDailyExpenseDto);

        DailyExpenseDto actualResultDto = expenseService.findByDate(LocalDate.now());

        assertEquals(expectedDailyExpenseDto, actualResultDto);
    }

    @Test
    void testFindByAbsentDate() {
        when(expenseRepositoryMock.findOne(any())).thenReturn(Optional.empty());

        try {
            expenseService.findByDate(LocalDate.now());
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(CustomException.class, e.getClass());
            CustomException exception = (CustomException) e;
            assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
            assertEquals("Records for " + LocalDate.now() + " are not found.", exception.getReason());
        }
    }

    @Test
    void testFindSumInInterval() {
        int expectedSum = 100;
        LocalDate date1 = LocalDate.now();
        LocalDate date2 = date1.plusDays(1);

        when(expenseRepositoryMock.findSumInInterval(date1, date2)).thenReturn(expectedSum);

        int actualSum = expenseService.findSumInInterval(date1, date2);

        assertEquals(expectedSum, actualSum);
    }

    @Test
    void testFindAllInInterval() {
        LocalDate date1 = LocalDate.now();
        LocalDate date2 = date1.plusDays(1);

        DailyExpense dailyExpense1 = new DailyExpense(10, date1);
        DailyExpense dailyExpense2 = new DailyExpense(10, date2);
        List<DailyExpense> expectedList = List.of(dailyExpense1, dailyExpense2);

        DailyExpenseDto dailyExpenseDto1 = new DailyExpenseDto(10, date1);
        DailyExpenseDto dailyExpenseDto2 = new DailyExpenseDto(10, date2);
        List<DailyExpenseDto> expectedDtoList = List.of(dailyExpenseDto1, dailyExpenseDto2);

        when(expenseRepositoryMock.findAllInInterval(date1, date2)).thenReturn(expectedList);
        when(expenseConverterMock.convertToDto(dailyExpense1)).thenReturn(dailyExpenseDto1);
        when(expenseConverterMock.convertToDto(dailyExpense2)).thenReturn(dailyExpenseDto2);

        List<DailyExpenseDto> actualDtoList = expenseService.findAllInInterval(date1, date2);

        assertEquals(expectedDtoList, actualDtoList);
    }

    @Test
    void testAddOne() {
        DailyExpense dailyExpense = new DailyExpense(10, LocalDate.now());
        DailyExpenseDto dailyExpenseDto = new DailyExpenseDto(10, LocalDate.now());

        when(expenseConverterMock.convertToEntity(dailyExpenseDto)).thenReturn(dailyExpense);

        expenseService.addOne(dailyExpenseDto);

        verify(expenseRepositoryMock, times(1))
                .addOne(dailyExpense);
    }
}