package yehor.budget.service;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import yehor.budget.entity.Category;
import yehor.budget.entity.Expense;
import yehor.budget.exception.CustomResponseStatusException;
import yehor.budget.manager.date.DateManager;
import yehor.budget.repository.CategoryRepository;
import yehor.budget.repository.ExpenseRepository;
import yehor.budget.web.converter.ExpenseConverter;
import yehor.budget.web.dto.full.ExpenseFullDto;
import yehor.budget.web.dto.limited.ExpenseLimitedDto;

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
    private final CategoryRepository categoryRepositoryMock = mock(CategoryRepository.class);

    private final ExpenseService expenseService = new ExpenseService(
            expenseConverterMock, expenseRepositoryMock, categoryRepositoryMock);

    @Test
    void testFindById() {
        Long id = 1L;
        Expense expense = getDailyExpense(id, LocalDate.now(), BigDecimal.TEN, true);
        ExpenseFullDto expectedExpenseDto = getDailyExpenseDto(id, LocalDate.now(), BigDecimal.TEN, true);
        Category category = Category.builder().id(1L).name("Food").build();
        expense.setCategory(category);

        when(expenseRepositoryMock.findById(id)).thenReturn(Optional.of(expense));
        when(expenseConverterMock.convert(expense)).thenReturn(expectedExpenseDto);

        ExpenseFullDto actualResultDto = expenseService.findById(id);

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

        ExpenseFullDto expenseDto1 = getDailyExpenseDto(1L, date1, BigDecimal.TEN, true);
        ExpenseFullDto expenseDto2 = getDailyExpenseDto(1L, date2, BigDecimal.TEN, true);
        List<ExpenseFullDto> expectedDtoList = List.of(expenseDto1, expenseDto2);

        when(expenseRepositoryMock.findAllInInterval(date1, date2)).thenReturn(expectedList);
        when(expenseConverterMock.convert(expense1)).thenReturn(expenseDto1);
        when(expenseConverterMock.convert(expense2)).thenReturn(expenseDto2);

        List<ExpenseFullDto> actualDtoList = expenseService.findAllInInterval(date1, date2);

        assertEquals(expectedDtoList, actualDtoList);
    }

    @Test
    void testSave() {
        Long categoryId = 1L;
        Expense expense = Expense.builder().date(LocalDate.now()).value(BigDecimal.TEN).build();
        ExpenseLimitedDto expenseDto = ExpenseLimitedDto.builder().date(LocalDate.now()).value(BigDecimal.TEN).isRegular(true).categoryId(categoryId).build();
        Category category = Category.builder().id(categoryId).name("Food").build();

        when(expenseConverterMock.convert(expenseDto)).thenReturn(expense);
        when(categoryRepositoryMock.findById(categoryId)).thenReturn(Optional.of(category));

        expenseService.save(expenseDto);

        verify(expenseRepositoryMock, times(1))
                .save(expense);
    }

    @Test
    void testSaveWithEndDateUpdate() {
        Long categoryId1 = 1L;
        Category category1 = Category.builder().id(categoryId1).name("Food").build();
        LocalDate expectedFirstLatestDate = DateManager.getEndDate().plusDays(5);
        Expense expense1 = Expense.builder().date(expectedFirstLatestDate).value(BigDecimal.TEN).isRegular(true).build();
        ExpenseLimitedDto expenseDto1 = ExpenseLimitedDto.builder().date(expectedFirstLatestDate).value(BigDecimal.TEN).isRegular(true).categoryId(categoryId1).build();

        Long categoryId2 = 2L;
        Category category2 = Category.builder().id(categoryId2).name("Meds").build();
        LocalDate expectedSecondLatestDate = DateManager.getEndDate().plusDays(10);
        Expense expense2 = Expense.builder().date(expectedSecondLatestDate).value(BigDecimal.ONE).isRegular(true).build();
        ExpenseLimitedDto expenseDto2 = ExpenseLimitedDto.builder().date(expectedSecondLatestDate).value(BigDecimal.ONE).isRegular(true).categoryId(categoryId2).build();

        when(expenseConverterMock.convert(expenseDto1)).thenReturn(expense1);
        when(expenseConverterMock.convert(expenseDto2)).thenReturn(expense2);
        when(categoryRepositoryMock.findById(categoryId1)).thenReturn(Optional.of(category1));
        when(categoryRepositoryMock.findById(categoryId2)).thenReturn(Optional.of(category2));

        expenseService.save(expenseDto1);
        assertEquals(DateManager.getEndDate(), expectedFirstLatestDate);

        expenseService.save(expenseDto2);
        assertEquals(DateManager.getEndDate(), expectedSecondLatestDate);
    }

    @Test
    void testSaveWithoutEndDateUpdate() {
        Long categoryId1 = 1L;
        Category category1 = Category.builder().id(categoryId1).name("Food").build();
        LocalDate expectedLatestDate = DateManager.getEndDate().plusDays(5);
        Expense expense1 = Expense.builder().date(expectedLatestDate).value(BigDecimal.TEN).isRegular(true).build();
        ExpenseLimitedDto expenseDto1 = ExpenseLimitedDto.builder().date(expectedLatestDate).value(BigDecimal.TEN).isRegular(true).categoryId(categoryId1).build();

        Long categoryId2 = 2L;
        Category category2 = Category.builder().id(categoryId2).name("Meds").build();
        LocalDate expectedNewerDate = DateManager.getEndDate().plusDays(3);
        Expense expense2 = Expense.builder().date(expectedNewerDate).value(BigDecimal.ONE).isRegular(true).build();
        ExpenseLimitedDto expenseDto2 = ExpenseLimitedDto.builder().date(expectedNewerDate).value(BigDecimal.ONE).isRegular(true).categoryId(categoryId2).build();

        when(expenseConverterMock.convert(expenseDto1)).thenReturn(expense1);
        when(expenseConverterMock.convert(expenseDto2)).thenReturn(expense2);
        when(categoryRepositoryMock.findById(categoryId1)).thenReturn(Optional.of(category1));
        when(categoryRepositoryMock.findById(categoryId2)).thenReturn(Optional.of(category2));

        expenseService.save(expenseDto1);
        assertEquals(DateManager.getEndDate(), expectedLatestDate);

        expenseService.save(expenseDto2);
        assertNotEquals(DateManager.getEndDate(), expectedNewerDate);
        assertEquals(DateManager.getEndDate(), expectedLatestDate);
    }

    @Test
    void testTrySavingWithAbsentCategoryId() {
        Long categoryId = 2L;
        ExpenseLimitedDto expenseDto = ExpenseLimitedDto.builder().date(LocalDate.now()).value(BigDecimal.TEN).isRegular(true).categoryId(categoryId).build();

        when(categoryRepositoryMock.findById(categoryId)).thenReturn(Optional.empty());

        try {
            expenseService.save(expenseDto);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(CustomResponseStatusException.class, e.getClass());
            CustomResponseStatusException exception = (CustomResponseStatusException) e;
            assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
            assertEquals("Category with id " + categoryId + " does not exist", exception.getReason());
        }
    }

    @Test
    void testUpdate() {
        Long id = 1L;
        Long categoryId = 1L;
        Expense expense = Expense.builder().date(LocalDate.now()).value(BigDecimal.TEN).build();
        ExpenseFullDto expenseDto = ExpenseFullDto.builder().id(id).date(LocalDate.now()).value(BigDecimal.TEN).isRegular(true).categoryId(categoryId).build();
        Category category = Category.builder().id(1L).name("Food").build();
        expense.setCategory(category);

        when(expenseConverterMock.convert(expenseDto)).thenReturn(expense);
        when(expenseRepositoryMock.existsById(id)).thenReturn(true);
        when(categoryRepositoryMock.findById(categoryId)).thenReturn(Optional.of(category));

        expenseService.updateById(expenseDto);

        verify(expenseRepositoryMock, times(1))
                .updateById(expense);
    }

    @Test
    void testTryUpdatingWithNotExistingId() {
        Long id = 1L;
        Expense expense = getDailyExpense(id, LocalDate.now(), BigDecimal.TEN, true);
        ExpenseFullDto expenseDto = getDailyExpenseDto(id, LocalDate.now(), BigDecimal.TEN, true);

        when(expenseConverterMock.convert(expenseDto)).thenReturn(expense);
        when(expenseRepositoryMock.existsById(id)).thenReturn(false);

        try {
            expenseService.updateById(expenseDto);
            fail("Exception was not thrown");
        } catch (CustomResponseStatusException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
            assertEquals("Expense with id " + id + " not found", e.getReason());
            verify(expenseRepositoryMock, never())
                    .updateById(expense);
        }
    }

    @Test
    void testTryUpdatingWithAbsentCategoryId() {
        Long id = 1L;
        Long categoryId = 2L;
        ExpenseFullDto expenseDto = ExpenseFullDto.builder().id(id).date(LocalDate.now()).value(BigDecimal.TEN).isRegular(true).categoryId(categoryId).build();

        when(expenseRepositoryMock.existsById(id)).thenReturn(true);
        when(categoryRepositoryMock.findById(categoryId)).thenReturn(Optional.empty());

        try {
            expenseService.updateById(expenseDto);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(CustomResponseStatusException.class, e.getClass());
            CustomResponseStatusException exception = (CustomResponseStatusException) e;
            assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
            assertEquals("Category with id " + categoryId + " does not exist", exception.getReason());
        }
    }

    @Test
    void testUpdateByIdWithEndDateUpdate() {
        Long categoryId1 = 1L;
        Category category1 = Category.builder().id(categoryId1).name("Food").build();
        LocalDate expectedFirstLatestDate = DateManager.getEndDate().plusDays(5);
        Expense expense1 = getDailyExpense(1L, expectedFirstLatestDate, BigDecimal.TEN, true);
        ExpenseFullDto expenseDto1 = ExpenseFullDto.builder().id(1L).date(expectedFirstLatestDate).value(BigDecimal.TEN).isRegular(true).categoryId(categoryId1).build();

        Long categoryId2 = 2L;
        Category category2 = Category.builder().id(categoryId2).name("Meds").build();
        LocalDate expectedSecondLatestDate = DateManager.getEndDate().plusDays(10);
        Expense expense2 = getDailyExpense(2L, expectedSecondLatestDate, BigDecimal.ONE, true);
        ExpenseFullDto expenseDto2 = ExpenseFullDto.builder().id(2L).date(expectedSecondLatestDate).value(BigDecimal.ONE).isRegular(true).categoryId(categoryId2).build();

        when(expenseConverterMock.convert(expenseDto1)).thenReturn(expense1);
        when(expenseConverterMock.convert(expenseDto2)).thenReturn(expense2);
        when(expenseRepositoryMock.existsById(expenseDto1.getId())).thenReturn(true);
        when(expenseRepositoryMock.existsById(expenseDto2.getId())).thenReturn(true);
        when(categoryRepositoryMock.findById(categoryId1)).thenReturn(Optional.of(category1));
        when(categoryRepositoryMock.findById(categoryId2)).thenReturn(Optional.of(category2));

        expenseService.updateById(expenseDto1);
        assertEquals(DateManager.getEndDate(), expectedFirstLatestDate);

        expenseService.updateById(expenseDto2);
        assertEquals(DateManager.getEndDate(), expectedSecondLatestDate);
    }

    @Test
    void testUpdateByIdWithoutEndDateUpdate() {
        Long categoryId1 = 1L;
        Category category1 = Category.builder().id(categoryId1).name("Food").build();
        LocalDate expectedLatestDate = DateManager.getEndDate().plusDays(5);
        Expense expense1 = getDailyExpense(1L, expectedLatestDate, BigDecimal.TEN, true);
        ExpenseFullDto expenseDto1 = ExpenseFullDto.builder().id(1L).date(expectedLatestDate).value(BigDecimal.TEN).isRegular(true).categoryId(categoryId1).build();

        Long categoryId2 = 2L;
        Category category2 = Category.builder().id(categoryId2).name("Meds").build();
        LocalDate expectedNewerDate = DateManager.getEndDate().plusDays(3);
        Expense expense2 = getDailyExpense(1L, expectedNewerDate, BigDecimal.ONE, true);
        ExpenseFullDto expenseDto2 = ExpenseFullDto.builder().id(2L).date(expectedNewerDate).value(BigDecimal.ONE).isRegular(true).categoryId(categoryId2).build();

        when(expenseConverterMock.convert(expenseDto1)).thenReturn(expense1);
        when(expenseConverterMock.convert(expenseDto2)).thenReturn(expense2);
        when(expenseRepositoryMock.existsById(expenseDto1.getId())).thenReturn(true);
        when(expenseRepositoryMock.existsById(expenseDto2.getId())).thenReturn(true);
        when(categoryRepositoryMock.findById(categoryId1)).thenReturn(Optional.of(category1));
        when(categoryRepositoryMock.findById(categoryId2)).thenReturn(Optional.of(category2));

        expenseService.updateById(expenseDto1);
        assertEquals(DateManager.getEndDate(), expectedLatestDate);

        expenseService.updateById(expenseDto2);
        assertNotEquals(DateManager.getEndDate(), expectedNewerDate);
        assertEquals(DateManager.getEndDate(), expectedLatestDate);
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

    private Expense getDailyExpense(Long id, LocalDate date, BigDecimal value, Boolean isRegular) {
        return Expense.builder()
                .id(id)
                .date(date)
                .value(value)
                .isRegular(isRegular)
                .build();
    }

    private ExpenseFullDto getDailyExpenseDto(Long id, LocalDate date, BigDecimal value, Boolean isRegular) {
        return ExpenseFullDto.builder()
                .id(id)
                .date(date)
                .value(value)
                .isRegular(isRegular)
                .build();
    }
}