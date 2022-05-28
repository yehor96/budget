package yehor.budget.service;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import yehor.budget.entity.DailyExpense;
import yehor.budget.exception.CustomExceptionManager.CustomException;
import yehor.budget.manager.date.DateManager;
import yehor.budget.repository.ExpenseRepository;
import yehor.budget.web.converter.ExpenseConverter;
import yehor.budget.web.dto.DailyExpenseDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
        DailyExpenseDto expectedDailyExpenseDto = getDailyExpenseDto(LocalDate.now(), 10);
        DailyExpense dailyExpense = getDailyExpense(LocalDate.now(), 10);

        when(expenseRepositoryMock.findByDate(LocalDate.now())).thenReturn(Optional.of(dailyExpense));
        when(expenseConverterMock.convertToDto(dailyExpense)).thenReturn(expectedDailyExpenseDto);

        DailyExpenseDto actualResultDto = expenseService.findByDate(LocalDate.now());

        assertEquals(expectedDailyExpenseDto, actualResultDto);
    }

    @Test
    void testFindByAbsentDate() {
        when(expenseRepositoryMock.findByDate(any())).thenReturn(Optional.empty());

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

        DailyExpense dailyExpense1 = getDailyExpense(date1, 10);
        DailyExpense dailyExpense2 = getDailyExpense(date2, 10);
        List<DailyExpense> expectedList = List.of(dailyExpense1, dailyExpense2);

        DailyExpenseDto dailyExpenseDto1 = getDailyExpenseDto(date1, 10);
        DailyExpenseDto dailyExpenseDto2 = getDailyExpenseDto(date2, 10);
        List<DailyExpenseDto> expectedDtoList = List.of(dailyExpenseDto1, dailyExpenseDto2);

        when(expenseRepositoryMock.findAllInInterval(date1, date2)).thenReturn(expectedList);
        when(expenseConverterMock.convertToDto(dailyExpense1)).thenReturn(dailyExpenseDto1);
        when(expenseConverterMock.convertToDto(dailyExpense2)).thenReturn(dailyExpenseDto2);

        List<DailyExpenseDto> actualDtoList = expenseService.findAllInInterval(date1, date2);

        assertEquals(expectedDtoList, actualDtoList);
    }

    @Test
    void testSave() {
        DailyExpense dailyExpense = getDailyExpense(LocalDate.now(), 10);
        DailyExpenseDto dailyExpenseDto = getDailyExpenseDto(LocalDate.now(), 10);

        when(expenseConverterMock.convertToEntity(dailyExpenseDto)).thenReturn(dailyExpense);

        expenseService.save(dailyExpenseDto);

        verify(expenseRepositoryMock, times(1))
                .save(dailyExpense);
    }

    @Test
    void testSaveWithEndDateUpdate() {
        LocalDate expectedFirstLatestDate = DateManager.getEndDate().plusDays(5);
        DailyExpense dailyExpense1 = getDailyExpense(expectedFirstLatestDate, 10);
        DailyExpenseDto dailyExpenseDto1 = getDailyExpenseDto(expectedFirstLatestDate, 10);

        LocalDate expectedSecondLatestDate = DateManager.getEndDate().plusDays(10);
        DailyExpense dailyExpense2 = getDailyExpense(expectedSecondLatestDate, 20);
        DailyExpenseDto dailyExpenseDto2 = getDailyExpenseDto(expectedSecondLatestDate, 20);

        when(expenseConverterMock.convertToEntity(dailyExpenseDto1)).thenReturn(dailyExpense1);
        when(expenseConverterMock.convertToEntity(dailyExpenseDto2)).thenReturn(dailyExpense2);

        expenseService.save(dailyExpenseDto1);
        assertEquals(DateManager.getEndDate(), expectedFirstLatestDate);

        expenseService.save(dailyExpenseDto2);
        assertEquals(DateManager.getEndDate(), expectedSecondLatestDate);
    }

    @Test
    void testSaveWithoutEndDateUpdate() {
        LocalDate expectedLatestDate = DateManager.getEndDate().plusDays(5);
        DailyExpense dailyExpense1 = getDailyExpense(expectedLatestDate, 10);
        DailyExpenseDto dailyExpenseDto1 = getDailyExpenseDto(expectedLatestDate, 10);

        LocalDate expectedNewerDate = DateManager.getEndDate().plusDays(3);
        DailyExpense dailyExpense2 = getDailyExpense(expectedNewerDate, 20);
        DailyExpenseDto dailyExpenseDto2 = getDailyExpenseDto(expectedNewerDate, 20);

        when(expenseConverterMock.convertToEntity(dailyExpenseDto1)).thenReturn(dailyExpense1);
        when(expenseConverterMock.convertToEntity(dailyExpenseDto2)).thenReturn(dailyExpense2);

        expenseService.save(dailyExpenseDto1);
        assertEquals(DateManager.getEndDate(), expectedLatestDate);

        expenseService.save(dailyExpenseDto2);
        assertNotEquals(DateManager.getEndDate(), expectedNewerDate);
        assertEquals(DateManager.getEndDate(), expectedLatestDate);
    }

    private DailyExpense getDailyExpense(LocalDate date, int value) {
        return DailyExpense.builder()
                .date(date)
                .value(value)
                .build();
    }

    private DailyExpenseDto getDailyExpenseDto(LocalDate date, int value) {
        return DailyExpenseDto.builder()
                .date(date)
                .value(value)
                .build();
    }
}