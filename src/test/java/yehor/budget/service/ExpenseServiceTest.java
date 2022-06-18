package yehor.budget.service;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import yehor.budget.entity.Expense;
import yehor.budget.exception.CustomResponseStatusException;
import yehor.budget.manager.date.DateManager;
import yehor.budget.repository.ExpenseRepository;
import yehor.budget.web.converter.ExpenseConverter;
import yehor.budget.web.dto.ExpenseDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExpenseServiceTest {

    private final ExpenseRepository expenseRepositoryMock = mock(ExpenseRepository.class);
    private final ExpenseConverter expenseConverterMock = mock(ExpenseConverter.class);

    private final ExpenseService expenseService = new ExpenseService(expenseConverterMock, expenseRepositoryMock);

    @Test
    void testFindById() {
        Long id = 1L;
        Expense expense = getDailyExpense(id, LocalDate.now(), BigDecimal.TEN, true);
        ExpenseDto expectedExpenseDto = getDailyExpenseDto(id, LocalDate.now(), BigDecimal.TEN, true);

        when(expenseRepositoryMock.findById(id)).thenReturn(Optional.of(expense));
        when(expenseConverterMock.convert(expense)).thenReturn(expectedExpenseDto);

        ExpenseDto actualResultDto = expenseService.findById(id);

        assertEquals(expectedExpenseDto, actualResultDto);
    }

    @Test
    void testFindByAbsentId() {
        Long id = 1L;

        when(expenseRepositoryMock.findById(id)).thenReturn(Optional.empty());

        try {
            expenseService.findById(id);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(CustomResponseStatusException.class, e.getClass());
            CustomResponseStatusException exception = (CustomResponseStatusException) e;
            assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
            assertEquals("Expense with id " + id + " not found", exception.getReason());
        }
    }

    @Test
    void testFindSumInInterval() {
        BigDecimal expectedSum = BigDecimal.valueOf(100);
        LocalDate date1 = LocalDate.now();
        LocalDate date2 = date1.plusDays(1);

        when(expenseRepositoryMock.findSumInInterval(date1, date2)).thenReturn(expectedSum);

        BigDecimal actualSum = expenseService.findSumInInterval(date1, date2);

        assertEquals(expectedSum, actualSum);
    }

    @Test
    void testFindAllInInterval() {
        LocalDate date1 = LocalDate.now();
        LocalDate date2 = date1.plusDays(1);

        Expense expense1 = getDailyExpense(1L, date1, BigDecimal.TEN, true);
        Expense expense2 = getDailyExpense(1L, date2, BigDecimal.TEN, true);
        List<Expense> expectedList = List.of(expense1, expense2);

        ExpenseDto expenseDto1 = getDailyExpenseDto(1L, date1, BigDecimal.TEN, true);
        ExpenseDto expenseDto2 = getDailyExpenseDto(1L, date2, BigDecimal.TEN, true);
        List<ExpenseDto> expectedDtoList = List.of(expenseDto1, expenseDto2);

        when(expenseRepositoryMock.findAllInInterval(date1, date2)).thenReturn(expectedList);
        when(expenseConverterMock.convert(expense1)).thenReturn(expenseDto1);
        when(expenseConverterMock.convert(expense2)).thenReturn(expenseDto2);

        List<ExpenseDto> actualDtoList = expenseService.findAllInInterval(date1, date2);

        assertEquals(expectedDtoList, actualDtoList);
    }

    @Test
    void testSave() {
        Expense expense = getDailyExpense(1L, LocalDate.now(), BigDecimal.TEN, true);
        ExpenseDto expenseDto = getDailyExpenseDto(1L, LocalDate.now(), BigDecimal.TEN, true);

        when(expenseConverterMock.convert(expenseDto)).thenReturn(expense);

        expenseService.save(expenseDto);

        verify(expenseRepositoryMock, times(1))
                .save(expense);
    }

    @Test
    void testSaveWithEndDateUpdate() {
        LocalDate expectedFirstLatestDate = DateManager.getEndDate().plusDays(5);
        Expense expense1 = getDailyExpense(1L, expectedFirstLatestDate, BigDecimal.TEN, true);
        ExpenseDto expenseDto1 = getDailyExpenseDto(1L, expectedFirstLatestDate, BigDecimal.TEN, true);

        LocalDate expectedSecondLatestDate = DateManager.getEndDate().plusDays(10);
        Expense expense2 = getDailyExpense(1L, expectedSecondLatestDate, BigDecimal.ONE, true);
        ExpenseDto expenseDto2 = getDailyExpenseDto(1L, expectedSecondLatestDate, BigDecimal.ONE, true);

        when(expenseConverterMock.convert(expenseDto1)).thenReturn(expense1);
        when(expenseConverterMock.convert(expenseDto2)).thenReturn(expense2);

        expenseService.save(expenseDto1);
        assertEquals(DateManager.getEndDate(), expectedFirstLatestDate);

        expenseService.save(expenseDto2);
        assertEquals(DateManager.getEndDate(), expectedSecondLatestDate);
    }

    @Test
    void testSaveWithoutEndDateUpdate() {
        LocalDate expectedLatestDate = DateManager.getEndDate().plusDays(5);
        Expense expense1 = getDailyExpense(1L, expectedLatestDate, BigDecimal.TEN, true);
        ExpenseDto expenseDto1 = getDailyExpenseDto(1L, expectedLatestDate, BigDecimal.TEN, true);

        LocalDate expectedNewerDate = DateManager.getEndDate().plusDays(3);
        Expense expense2 = getDailyExpense(1L, expectedNewerDate, BigDecimal.ONE, true);
        ExpenseDto expenseDto2 = getDailyExpenseDto(1L, expectedNewerDate, BigDecimal.ONE, true);

        when(expenseConverterMock.convert(expenseDto1)).thenReturn(expense1);
        when(expenseConverterMock.convert(expenseDto2)).thenReturn(expense2);

        expenseService.save(expenseDto1);
        assertEquals(DateManager.getEndDate(), expectedLatestDate);

        expenseService.save(expenseDto2);
        assertNotEquals(DateManager.getEndDate(), expectedNewerDate);
        assertEquals(DateManager.getEndDate(), expectedLatestDate);
    }

    @Test
    void testTrySavingWithExistingId() {
        Long id = 1L;
        Expense expense = getDailyExpense(id, LocalDate.now(), BigDecimal.TEN, true);
        ExpenseDto expenseDto = getDailyExpenseDto(id, LocalDate.now(), BigDecimal.TEN, true);

        when(expenseConverterMock.convert(expenseDto)).thenReturn(expense);
        when(expenseRepositoryMock.existsById(id)).thenReturn(true);

        try {
            expenseService.save(expenseDto);
            fail("Exception was not thrown");
        } catch (CustomResponseStatusException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
            assertEquals("Expense with id " + id + " already exists", e.getReason());
            verify(expenseRepositoryMock, never())
                    .save(expense);
        }
    }

    @Test
    void testUpdate() {
        Long id = 1L;
        Expense expense = getDailyExpense(id, LocalDate.now(), BigDecimal.TEN, true);
        ExpenseDto expenseDto = getDailyExpenseDto(id, LocalDate.now(), BigDecimal.TEN, true);

        when(expenseConverterMock.convert(expenseDto)).thenReturn(expense);
        when(expenseRepositoryMock.existsById(id)).thenReturn(true);

        expenseService.updateById(id, expenseDto);

        verify(expenseRepositoryMock, times(1))
                .updateById(expense);
    }

    @Test
    void testTryUpdatingWithExistingId() {
        Long id = 1L;
        Expense expense = getDailyExpense(id, LocalDate.now(), BigDecimal.TEN, true);
        ExpenseDto expenseDto = getDailyExpenseDto(id, LocalDate.now(), BigDecimal.TEN, true);

        when(expenseConverterMock.convert(expenseDto)).thenReturn(expense);
        when(expenseRepositoryMock.existsById(id)).thenReturn(false);

        try {
            expenseService.updateById(id, expenseDto);
            fail("Exception was not thrown");
        } catch (CustomResponseStatusException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
            assertEquals("Expense with id " + id + " not found", e.getReason());
            verify(expenseRepositoryMock, never())
                    .updateById(expense);
        }
    }

    @Test
    void testDeleteById() {
        Long id = 1L;

        when(expenseRepositoryMock.existsById(id)).thenReturn(true);

        expenseService.deleteById(id);

        verify(expenseRepositoryMock, times(1))
                .deleteById(id);
    }

    @Test
    void testTryDeletingWithNotExistingId() {
        Long id = 1L;

        when(expenseRepositoryMock.existsById(id)).thenReturn(false);

        try {
            expenseService.deleteById(id);
            fail("Exception was not thrown");
        } catch (CustomResponseStatusException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
            assertEquals("Expense with id " + id + " not found", e.getReason());
            verify(expenseRepositoryMock, never())
                    .deleteById(id);
        }
    }

    private Expense getDailyExpense(Long id, LocalDate date, BigDecimal value, boolean isRegular) {
        return Expense.builder()
                .id(id)
                .date(date)
                .value(value)
                .isRegular(isRegular)
                .build();
    }

    private ExpenseDto getDailyExpenseDto(Long id, LocalDate date, BigDecimal value, boolean isRegular) {
        return ExpenseDto.builder()
                .id(id)
                .date(date)
                .value(value)
                .isRegular(isRegular)
                .build();
    }
}