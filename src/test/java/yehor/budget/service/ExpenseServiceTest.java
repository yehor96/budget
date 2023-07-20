package yehor.budget.service;

import org.junit.jupiter.api.Test;
import yehor.budget.common.date.DateManager;
import yehor.budget.common.exception.ObjectNotFoundException;
import yehor.budget.entity.Category;
import yehor.budget.entity.Expense;
import yehor.budget.repository.CategoryRepository;
import yehor.budget.repository.ExpenseRepository;
import yehor.budget.repository.TagRepository;
import yehor.budget.web.converter.ExpenseConverter;
import yehor.budget.web.dto.ExpensesByTagDto;
import yehor.budget.web.dto.full.ExpenseFullDto;
import yehor.budget.web.dto.limited.ExpenseLimitedDto;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static common.factory.CategoryFactory.DEFAULT_CATEGORY_ID;
import static common.factory.CategoryFactory.defaultCategory;
import static common.factory.ExpenseFactory.DEFAULT_EXPENSE_ID;
import static common.factory.ExpenseFactory.defaultExpense;
import static common.factory.ExpenseFactory.defaultExpenseByTagDto;
import static common.factory.ExpenseFactory.defaultExpenseFullDto;
import static common.factory.ExpenseFactory.defaultExpenseFullDtoList;
import static common.factory.ExpenseFactory.defaultExpenseLimitedDto;
import static common.factory.ExpenseFactory.defaultExpenseList;
import static common.factory.ExpenseFactory.emptyExpenseByTagDto;
import static common.factory.ExpenseFactory.secondExpense;
import static common.factory.ExpenseFactory.secondExpenseFullDto;
import static common.factory.ExpenseFactory.thirdExpenseFullDto;
import static common.factory.TagFactory.DEFAULT_TAG_ID;
import static common.factory.TagFactory.defaultTag;
import static common.factory.TagFactory.tagWithExpenses;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExpenseServiceTest {

    private final ExpenseRepository expenseRepositoryMock = mock(ExpenseRepository.class);
    private final ExpenseConverter expenseConverterMock = mock(ExpenseConverter.class);
    private final CategoryRepository categoryRepositoryMock = mock(CategoryRepository.class);
    private final TagRepository tagRepositoryMock = mock(TagRepository.class);
    private final DateManager dateManagerMock = mock(DateManager.class);

    private final ExpenseService expenseService = new ExpenseService(
            expenseConverterMock, expenseRepositoryMock, categoryRepositoryMock, tagRepositoryMock, dateManagerMock);

    @Test
    void testGetById() {
        Long id = DEFAULT_EXPENSE_ID;
        Expense expense = defaultExpense();
        ExpenseFullDto expectedExpenseDto = defaultExpenseFullDto();
        Category category = defaultCategory();
        expense.setCategory(category);

        when(expenseRepositoryMock.getById(id)).thenReturn(expense);
        when(expenseConverterMock.convert(expense)).thenReturn(expectedExpenseDto);

        ExpenseFullDto actualResultDto = expenseService.getById(id);

        assertEquals(expectedExpenseDto, actualResultDto);
    }

    @Test
    void testGetByAbsentId() {
        Long id = DEFAULT_EXPENSE_ID;

        when(expenseRepositoryMock.getById(id)).thenThrow(new EntityNotFoundException());

        try {
            expenseService.getById(id);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(EntityNotFoundException.class, e.getClass());
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
    void testFindSumInIntervalByCategory() {
        Long categoryId = 1L;
        BigDecimal expectedSum = BigDecimal.valueOf(100);
        LocalDate date1 = LocalDate.now();
        LocalDate date2 = date1.plusDays(1);

        when(expenseRepositoryMock.findSumInIntervalByCategory(date1, date2, categoryId)).thenReturn(expectedSum);
        when(categoryRepositoryMock.existsById(categoryId)).thenReturn(true);

        BigDecimal actualSum = expenseService.findSumInIntervalByCategory(date1, date2, categoryId);

        assertEquals(expectedSum, actualSum);
    }

    @Test
    void testFindSumInIntervalByCategoryFailing() {
        Long categoryId = 1L;
        LocalDate date1 = LocalDate.now();
        LocalDate date2 = date1.plusDays(1);

        when(categoryRepositoryMock.existsById(categoryId)).thenReturn(false);

        try {
            expenseService.findSumInIntervalByCategory(date1, date2, categoryId);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(ObjectNotFoundException.class, e.getClass());
            ObjectNotFoundException exception = (ObjectNotFoundException) e;
            assertEquals("Category with id " + categoryId + " does not exist", exception.getMessage());
        }

        verify(expenseRepositoryMock, never()).findSumInIntervalByCategory(any(), any(), any());
    }

    @Test
    void testFindAllInDateByCategory() {
        Long categoryId = 1L;
        LocalDate date = LocalDate.now();
        List<Expense> expenses = defaultExpenseList();
        List<ExpenseFullDto> expected = defaultExpenseFullDtoList();

        when(expenseRepositoryMock.findAllInDateByCategory(date, categoryId)).thenReturn(expenses);
        when(categoryRepositoryMock.existsById(categoryId)).thenReturn(true);
        when(expenseConverterMock.convert(any(Expense.class)))
                .thenReturn(defaultExpenseFullDto())
                .thenReturn(secondExpenseFullDto())
                .thenReturn(thirdExpenseFullDto());

        List<ExpenseFullDto> actual = expenseService.findAllInDateByCategory(date, categoryId);

        assertEquals(expected, actual);
    }

    @Test
    void testFindAllInDateByCategoryFailing() {
        Long categoryId = 1L;
        LocalDate date = LocalDate.now();

        when(categoryRepositoryMock.existsById(categoryId)).thenReturn(false);

        try {
            expenseService.findAllInDateByCategory(date, categoryId);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(ObjectNotFoundException.class, e.getClass());
            ObjectNotFoundException exception = (ObjectNotFoundException) e;
            assertEquals("Category with id " + categoryId + " does not exist", exception.getMessage());
        }

        verify(expenseRepositoryMock, never()).findAllInDateByCategory(any(), any());
    }

    @Test
    void testFindAllInInterval() {
        LocalDate date1 = LocalDate.now();
        LocalDate date2 = date1.plusDays(1);

        Expense expense1 = defaultExpense();
        Expense expense2 = secondExpense();
        List<Expense> expectedList = List.of(expense1, expense2);

        ExpenseFullDto expenseDto1 = defaultExpenseFullDto();
        ExpenseFullDto expenseDto2 = secondExpenseFullDto();
        List<ExpenseFullDto> expectedDtoList = List.of(expenseDto1, expenseDto2);

        when(expenseRepositoryMock.findAllInInterval(date1, date2)).thenReturn(expectedList);
        when(expenseConverterMock.convert(expense1)).thenReturn(expenseDto1);
        when(expenseConverterMock.convert(expense2)).thenReturn(expenseDto2);

        List<ExpenseFullDto> actualDtoList = expenseService.findAllInInterval(date1, date2);

        assertEquals(expectedDtoList, actualDtoList);
    }

    @Test
    void testSave() {
        LocalDate now = LocalDate.now();
        Expense expense = defaultExpense();
        ExpenseLimitedDto expenseDto = defaultExpenseLimitedDto();

        when(expenseConverterMock.convert(expenseDto)).thenReturn(expense);
        when(categoryRepositoryMock.existsById(DEFAULT_CATEGORY_ID)).thenReturn(true);
        when(tagRepositoryMock.existsById(DEFAULT_TAG_ID)).thenReturn(true);

        expenseService.save(expenseDto);

        verify(expenseRepositoryMock, times(1))
                .save(expense);
        verify(dateManagerMock, times(1))
                .updateBudgetDatesIfNecessary(now);
    }

    @Test
    void testTrySavingWithAbsentCategoryId() {
        Long categoryId = DEFAULT_CATEGORY_ID;
        ExpenseLimitedDto expenseDto = defaultExpenseLimitedDto();

        when(categoryRepositoryMock.findById(categoryId)).thenReturn(Optional.empty());

        try {
            expenseService.save(expenseDto);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(ObjectNotFoundException.class, e.getClass());
            ObjectNotFoundException exception = (ObjectNotFoundException) e;
            assertEquals("Category with id " + categoryId + " does not exist", exception.getMessage());
        }
    }

    @Test
    void testTrySavingWithAbsentTagId() {
        Long tagId = DEFAULT_TAG_ID;
        ExpenseLimitedDto expenseDto = defaultExpenseLimitedDto();

        when(expenseConverterMock.convert(expenseDto)).thenReturn(defaultExpense());
        when(expenseRepositoryMock.existsById(DEFAULT_EXPENSE_ID)).thenReturn(false);
        when(categoryRepositoryMock.existsById(DEFAULT_CATEGORY_ID)).thenReturn(true);
        when(tagRepositoryMock.existsById(tagId)).thenReturn(false);

        try {
            expenseService.save(expenseDto);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(ObjectNotFoundException.class, e.getClass());
            ObjectNotFoundException exception = (ObjectNotFoundException) e;
            assertEquals("Tag with id " + tagId + " does not exist", exception.getMessage());
        }
    }

    @Test
    void testUpdate() {
        LocalDate now = LocalDate.now();
        Expense expense = defaultExpense();
        ExpenseFullDto expenseDto = defaultExpenseFullDto();

        when(expenseConverterMock.convert(expenseDto)).thenReturn(expense);
        when(expenseRepositoryMock.existsById(DEFAULT_EXPENSE_ID)).thenReturn(true);
        when(categoryRepositoryMock.existsById(DEFAULT_CATEGORY_ID)).thenReturn(true);
        when(tagRepositoryMock.existsById(DEFAULT_TAG_ID)).thenReturn(true);

        expenseService.update(expenseDto);

        verify(expenseRepositoryMock, times(1))
                .save(expense);
        verify(dateManagerMock, times(1))
                .updateBudgetDatesIfNecessary(now);
    }

    @Test
    void testTryUpdatingWithNotExistingId() {
        Long id = DEFAULT_EXPENSE_ID;
        ExpenseFullDto expenseDto = defaultExpenseFullDto();

        when(expenseRepositoryMock.existsById(id)).thenReturn(false);

        try {
            expenseService.update(expenseDto);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(ObjectNotFoundException.class, e.getClass());
            ObjectNotFoundException exception = (ObjectNotFoundException) e;
            assertEquals("Expense with id " + id + " does not exist", exception.getMessage());
            verify(expenseRepositoryMock, never())
                    .save(any());
        }
    }

    @Test
    void testTryUpdatingWithNullId() {
        Long id = null;
        ExpenseFullDto expenseDto = defaultExpenseFullDto();
        expenseDto.setId(id);

        try {
            expenseService.update(expenseDto);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(ObjectNotFoundException.class, e.getClass());
            ObjectNotFoundException exception = (ObjectNotFoundException) e;
            assertEquals("Expense with id " + id + " does not exist", exception.getMessage());
            verify(expenseRepositoryMock, never())
                    .save(any());
        }
    }

    @Test
    void testTryUpdatingWithAbsentCategoryId() {
        Long categoryId = DEFAULT_CATEGORY_ID;
        ExpenseFullDto expenseDto = defaultExpenseFullDto();

        when(expenseRepositoryMock.existsById(DEFAULT_EXPENSE_ID)).thenReturn(true);
        when(categoryRepositoryMock.findById(categoryId)).thenReturn(Optional.empty());

        try {
            expenseService.update(expenseDto);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(ObjectNotFoundException.class, e.getClass());
            ObjectNotFoundException exception = (ObjectNotFoundException) e;
            assertEquals("Category with id " + categoryId + " does not exist", exception.getMessage());
        }
    }

    @Test
    void testTryUpdatingWithAbsentTagId() {
        Long tagId = DEFAULT_TAG_ID;
        ExpenseFullDto expenseDto = defaultExpenseFullDto();

        when(expenseConverterMock.convert(expenseDto)).thenReturn(defaultExpense());
        when(expenseRepositoryMock.existsById(DEFAULT_EXPENSE_ID)).thenReturn(true);
        when(categoryRepositoryMock.existsById(DEFAULT_CATEGORY_ID)).thenReturn(true);
        when(tagRepositoryMock.existsById(tagId)).thenReturn(false);

        try {
            expenseService.update(expenseDto);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(ObjectNotFoundException.class, e.getClass());
            ObjectNotFoundException exception = (ObjectNotFoundException) e;
            assertEquals("Tag with id " + tagId + " does not exist", exception.getMessage());
        }
    }

    @Test
    void testDeleteById() {
        Long id = DEFAULT_EXPENSE_ID;

        when(expenseRepositoryMock.existsById(id)).thenReturn(true);

        expenseService.deleteById(id);

        verify(expenseRepositoryMock, times(1))
                .deleteById(id);
    }

    @Test
    void testTryDeletingWithNotExistingId() {
        Long id = DEFAULT_EXPENSE_ID;

        when(expenseRepositoryMock.existsById(id)).thenReturn(false);

        try {
            expenseService.deleteById(id);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(ObjectNotFoundException.class, e.getClass());
            ObjectNotFoundException exception = (ObjectNotFoundException) e;
            assertEquals("Expense with id " + id + " does not exist", exception.getMessage());
            verify(expenseRepositoryMock, never())
                    .deleteById(id);
        }
    }

    @Test
    void testTryDeletingWithNullId() {
        Long id = null;

        try {
            expenseService.deleteById(id);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(ObjectNotFoundException.class, e.getClass());
            ObjectNotFoundException exception = (ObjectNotFoundException) e;
            assertEquals("Expense with id " + id + " does not exist", exception.getMessage());
            verify(expenseRepositoryMock, never())
                    .deleteById(any());
        }
    }

    @Test
    void testGetExpensesByTagId() {
        Long id = 1L;
        ExpensesByTagDto expectedResult = defaultExpenseByTagDto();

        when(tagRepositoryMock.existsById(id)).thenReturn(true);
        when(tagRepositoryMock.getById(id)).thenReturn(tagWithExpenses());
        when(expenseConverterMock.convert(any(Expense.class)))
                .thenReturn(defaultExpenseFullDto())
                .thenReturn(thirdExpenseFullDto());

        ExpensesByTagDto actualResult = expenseService.getExpensesByTagId(1L);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testGetExpensesByTagIdWithoutExpenses() {
        Long id = 1L;
        ExpensesByTagDto expectedResult = emptyExpenseByTagDto();

        when(tagRepositoryMock.existsById(id)).thenReturn(true);
        when(tagRepositoryMock.getById(id)).thenReturn(defaultTag());

        ExpensesByTagDto actualResult = expenseService.getExpensesByTagId(1L);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testTryGettingExpensesByNotExistingTagId() {
        Long id = 1L;
        when(tagRepositoryMock.existsById(id)).thenReturn(false);
        try {
            expenseService.getExpensesByTagId(id);
            fail("Exception was not thrown");
        } catch (Exception e) {
            assertEquals(ObjectNotFoundException.class, e.getClass());
            ObjectNotFoundException exception = (ObjectNotFoundException) e;
            assertEquals("Tag with id " + id + " does not exist", exception.getMessage());
            verify(tagRepositoryMock, never())
                    .getById(any());
        }
    }
}